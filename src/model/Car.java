package model;

import java.io.Serializable;
import java.util.Objects;

public class Car implements Serializable {

    private String id;
    private String manufacturer;
    private String link;
    private String model;
    private Integer year;
    private Double price;
    private Integer cubicCapacity;
    private String fuel;
    private Integer numberOfSeats;
    private String doorCount;
    private String color;
    private String heading;

    private Integer horsepower;
    private Double mileage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCubicCapacity() {
        return cubicCapacity;
    }

    public void setCubicCapacity(Integer cubicCapacity) {
        this.cubicCapacity = cubicCapacity;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getDoorCount() {
        return doorCount;
    }

    public void setDoorCount(String doorCount) {
        this.doorCount = doorCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof Car) {
            Car car = (Car) obj;
            if (car.getLink().equals(link)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturer, link);
    }
}
