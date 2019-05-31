package agents.crawlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.ACLMessage;
import model.AgentI;
import model.Car;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Stateful
@Remote(AgentI.class)
public class MojAutoCrawler extends CrawlerAgent {

    public static final Integer MAX_DEPTH = 16;
    private Map<String, Car> cars;
    private Set<String> visited;

    @Override
    protected void initArgs(Map<String, String> args) {
        // TODO Determine save location
        initCrawler("https://www.mojauto.rs/", "");
    }

    @Override
    protected void onMessage(ACLMessage message) {

        visited = new HashSet<>();
        cars = new HashMap<>();

        broadcastInfo("Received message: " + message);
        System.out.println("started crawling on www.mojauto.rs");

        visitPage("https://www.mojauto.rs/", 0);

        MongoDatabase db = mongoDB.getDb();
        db.getCollection("mojauto").drop();

        MongoCollection<org.bson.Document> coll = db.getCollection("mojauto");

        cars.forEach((k,v) -> {
            coll.insertOne(mongoDB.carToDocument(v));
        });

        broadcastInfo("finished crawling");
        broadcastInfo("found " + cars.size() + " cars on www.mojauto.rs!");
    }

    private void visitPage(String url, Integer depth) {
        if (!visited.contains(url) && depth <= MAX_DEPTH &&
            url.startsWith("https://www.mojauto.rs/") &&
            !url.contains("/auto-servisi-usluge/") && !url.contains("/delovi/") &&
            !url.contains("/po_stranici/20/") && !url.contains("/po_stranici/40/") &&
            !url.contains("/prikazi_kao/galerija/") && !url.contains("/polovni-delovi/") &&
            !url.endsWith("#") && !url.contains("/polovni-kamioni/") &&
            !url.contains("/polovni-gradjevinska/") && !url.contains("/polovni-prikolice/") &&
            !url.contains("/polovni-poljoprivredna/") && !url.endsWith(".jpg") &&
            !url.contains("/print_oglas/") && !url.contains("/dodaj/")) {

            if (cars.size() == 250)
                return;

            try {

                Document document = Jsoup.connect(url).get();

                visited.add(url);

//                System.out.println(url);

                Elements links = document.select("a");

                if (url.startsWith("https://www.mojauto.rs/polovni-automobili/")) {
                    String heading = document.select("h1").first().ownText();
                    Integer yearsOld;

                    Element dataSelection = document.select("div.sidePanel:nth-child(2) > ul:nth-child(2) > li:nth-child(2) > span:nth-child(1)").first();

                    try {
                        yearsOld = Integer.valueOf(dataSelection.ownText().split("\\.")[0]);
                    } catch (NumberFormatException e) {
                        yearsOld = 0;
                    }

                    String fuel = "";

                    dataSelection = document.select("div.sidePanel:nth-child(2) > ul:nth-child(2) > li:nth-child(4) > span:nth-child(2)").first();
                    if (dataSelection != null && !dataSelection.ownText().equals("") && dataSelection.ownText().equals("Dizel") || dataSelection.ownText().equals("Benzin"))
                        fuel = dataSelection.ownText();

                    String manufacturer = document.select(".breadcrumb > ol:nth-child(1) > li:nth-child(3) > a:nth-child(1) > span:nth-child(1)").first().ownText();
                    String model = document.select(".breadcrumb > ol:nth-child(1) > li:nth-child(4) > a:nth-child(1) > span:nth-child(1)").first().ownText();

                    String priceStr = document.select("div.sidebarPrice span.priceReal").first().ownText().split(" ")[0].replaceAll("\\.", "");
                    Double price;

                    try {
                        price = Double.valueOf(priceStr);
                    } catch (NumberFormatException e) {
                        price = 0.0;
                    }

                    Integer numberOfSeats = 0;
                    String doorCount = "";
                    String color = "";
                    Integer cubicCapacity = 0;

                    Elements elements = document.select("div.singleBox:nth-child(8) li span");

                    for (Element e : elements) {
                        if (e.ownText().equals("Broj sedišta")) {
                            String nos = e.parent().select("strong").first().ownText();
                            try {
                                numberOfSeats = Integer.valueOf(nos);
                            } catch(NumberFormatException ex) {
                                numberOfSeats = 0;
                            }
                            continue;
                        }

                        if (e.ownText().equals("Broj vrata")) {
                            doorCount = e.parent().select("strong").first().ownText();
                            continue;
                        }

                        if (e.ownText().equals("Boja")) {
                            color = e.parent().select("strong").first().ownText();
                            continue;
                        }

                        if (e.ownText().equals("Snaga")) {
                            String cc = e.parent().select("strong").first().ownText().split(" ")[0];
                            try {
                                cubicCapacity = Integer.valueOf(cc);
                            } catch (NumberFormatException ex) {
                                cubicCapacity = 0;
                            }
                            continue;
                        }
                    }

                    String id = document.select(".additionalInfoSidebar > li:nth-child(1) > span:nth-child(2)").first().ownText().split(" ")[2];

                    if (id != null && !id.equals("") && !cars.containsKey(id)) {
                        Car car = new Car();
                        car.setId(id);
                        car.setManufacturer(manufacturer);
                        car.setLink(url);
                        car.setModel(model);
                        car.setYear(yearsOld);
                        car.setPrice(price);
                        car.setCubicCapacity(cubicCapacity);
                        car.setFuel(fuel);
                        car.setNumberOfSeats(numberOfSeats);
                        car.setDoorCount(doorCount);
                        car.setColor(color);
                        car.setHeading(heading);

                        cars.put(id, car);
                    }
                }

                depth++;

                for (Element link : links) {
                    String nextUrl = link.attr("abs:href");
                    visitPage(nextUrl, depth);
                }

            } catch (Exception e) {
                // invalid url, do nothing
            }
        }
    }
}
