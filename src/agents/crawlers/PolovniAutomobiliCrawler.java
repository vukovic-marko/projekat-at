package agents.crawlers;

import com.mongodb.client.MongoCollection;
import model.ACLMessage;
import model.AgentI;
import model.Car;
import model.Performative;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class PolovniAutomobiliCrawler extends CrawlerAgent {

    private static final String URL = "https://www.polovniautomobili.com/";

    @Override
    protected void initArgs(Map<String, String> args) {
        initCrawler(URL);
    }

    @Override
    protected void onMessage(ACLMessage message) {

        visited = new HashSet<>();
        cars = new HashMap<>();

        //System.out.println("I crawl on polovniautomobili.com!");
        broadcastInfo("Received message: " + message);

        if (message.getPerformative()== Performative.REQUEST) {

            prepareCrawler(message);

            broadcastInfo("Started crawling on polovniautomobili.com");
            visitPage("https://www.polovniautomobili.com/", 0);

            MongoCollection<org.bson.Document> collection = mongoDB.prepareCollection(message.getContent() + ".crw");

            cars.forEach((k,v) -> {
                try {
                    collection.insertOne(mongoDB.carToDocument(v));
                } catch (Exception e) {
                    // just continue
                }
            });

            broadcastInfo("Finished crawling");
            broadcastInfo("Found " + cars.size() + " cars on www.polovniautomobili.com!");
        }

    }

    private void visitPage(String url, Integer depth) {
        if (!visited.contains(url) && depth <= MAX_DEPTH &&
            url.startsWith("https://www.polovniautomobili.com/") &&
            !url.endsWith(".pdf") && !url.contains("/delovi-i-oprema/") &&
            !url.contains("?image=") && !url.contains("/auto-vesti/") &&
            !url.endsWith("#call-to-seller") && !url.endsWith("#write-to-seller") &&
            !url.contains("/classifieds/") && !url.contains("/interesed") &&
            !url.endsWith("#") && !url.contains("navigation-bar") &&
            !url.contains("/prati-oglas/") && !url.contains("?evaluate=")) {

//            if (cars.size() == 250)
//                return;

            try {
                Document document = Jsoup.connect(url).get();

                visited.add(url);

//                System.out.println(url);

                Elements links = document.select("a");

                if (url.matches("https://www.polovniautomobili.com/auto-oglasi/[0-9]+/.+")) {
                    String heading = document.select("h1").first().ownText();
                    Integer yearsOld;

                    Element dataSection = document.select(".side-price-padding-left-10").first();
                    dataSection = dataSection.select("section").first();

                    dataSection = dataSection.child(1);

                    String manufacturer = dataSection.child(3).ownText();
                    String model = dataSection.child(5).ownText();

                    String mileageStr = dataSection.child(9).ownText();
                    mileageStr = mileageStr.substring(0, mileageStr.indexOf("km")).trim();
                    mileageStr = mileageStr.replaceAll("\\.", "");
                    Double mileage = Double.parseDouble(mileageStr);

                    String ccStr = dataSection.child(15).ownText();
                    ccStr = ccStr.substring(0, ccStr.indexOf("cm")).trim();
                    Integer cc = Integer.parseInt(ccStr);

                    String powerStr = dataSection.child(17).ownText();
                    powerStr = powerStr.substring(powerStr.indexOf("/") + 1, powerStr.indexOf(" "));
                    Integer power = Integer.parseInt(powerStr);

                    try {
                        yearsOld = Integer.valueOf(document.select("h1 small").first().text().split("\\.")[0]);
                    } catch (NumberFormatException e) {
                        yearsOld = 0;
                    }
                    Elements elements = document.select("span.uk-hidden-medium");

                    String priceStr = "";
                    if (document.select("div.price-item").size() != 0) {
                        Element element = document.select("div.price-item").first();
                        if (element != null && !element.ownText().equals("")) {
                            priceStr = element.ownText().split(" ")[0];
                            priceStr = priceStr.replaceAll("\\.", "");
                        }
                    }

                    if (document.select("div.price-item-discount").size() != 0) {
                        Element element = document.select("div.price-item-discount").first();
                        if (element != null && !element.ownText().equals("")) {
                            priceStr = element.ownText().split(" ")[0];
                            priceStr = priceStr.replaceAll("\\.", "");
                        }
                    }

                    Double price;

                    try {
                        price = Double.valueOf(priceStr);
                    } catch (NumberFormatException e) {
                        price = 0.0;
                    }

                    String id = "";

                    for (Element e : elements) {
                        if (e.text().contains("Broj oglasa:")) {
                            id = e.parent().after(e).text();
                            break;
                        }
                    }

                    elements = document.select("div.uk-text-bold");

                    Integer numberOfSeats = 0;
                    String doorCount = "";

                    for (Element e : elements) {
                        if (e.text().contains("sedišta")) {
                            try {
                                numberOfSeats = Integer.valueOf(e.text().split(" ")[0]);
                            } catch (NumberFormatException e1) {
                                numberOfSeats = 0;
                            }

                            continue;
                        }

                        if (e.text().contains("vrata")) {
                            doorCount = e.text().split(" ")[0];
                            continue;
                        }

                    }

                    elements = document.select("div.uk-width-medium-1-4");

                    String color = "";

                    for (Element e : elements) {
                        if (e.text().equals("Boja")) {
                            color = e.nextElementSibling().text();
                            break;
                        }
                    }

                    elements = document.select("div.uk-width-1-2");

                    Integer cubicCapacity = 0;
                    String fuel = "";

                    for (Element e : elements) {
                        if (e.text().contains("(kW/KS)")) {
                            try {
                                cubicCapacity = Integer.valueOf(e.text().split("/")[1].split(" ")[0]);
                            } catch (NumberFormatException e1) {
                                cubicCapacity = 0;
                            }

                            continue;
                        }

                        if (e.text().equals("Benzin")) {
                            fuel = e.text();
                            continue;
                        }

                        if (e.text().equals("Dizel")) {
                            fuel = e.text();
                            continue;
                        }

                    }

                    if (id != null && !id.equals("") && !cars.containsKey(id)) {
                        Car car = new Car();

                        car.setId(id);
                        car.setModel(model);
                        car.setManufacturer(manufacturer);
                        car.setHeading(heading);
                        car.setYear(yearsOld);
                        car.setNumberOfSeats(numberOfSeats);
                        car.setDoorCount(doorCount);
                        car.setCubicCapacity(cc);
                        car.setColor(color);
                        car.setFuel(fuel);
                        car.setPrice(price);
                        car.setLink(url);
                        car.setHorsepower(power);
                        car.setMileage(mileage);

                        cars.put(id, car);
                        System.out.println(cars.size());
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
