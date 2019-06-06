package model.dto;

import java.io.Serializable;

public class FilterDTO implements Serializable {

    private String maker;
    private String model;

    private Double priceFrom;
    private Double priceTo;

    private String fuel;

    private Integer ccFrom;
    private Integer ccTo;

    private Integer yearFrom;
    private Integer yearTo;

    private Integer powerFrom;
    private Integer powerTo;

    private Double mileageFrom;
    private Double mileageTo;

    private Integer seats;
    private String doors;
    private String color;

    private FilterDTO() {

    }


    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Double priceTo) {
        this.priceTo = priceTo;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getCcFrom() {
        return ccFrom;
    }

    public void setCcFrom(Integer ccFrom) {
        this.ccFrom = ccFrom;
    }

    public Integer getCcTo() {
        return ccTo;
    }

    public void setCcTo(Integer ccTo) {
        this.ccTo = ccTo;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public Integer getPowerFrom() {
        return powerFrom;
    }

    public void setPowerFrom(Integer powerFrom) {
        this.powerFrom = powerFrom;
    }

    public Integer getPowerTo() {
        return powerTo;
    }

    public void setPowerTo(Integer powerTo) {
        this.powerTo = powerTo;
    }

    public Double getMileageFrom() {
        return mileageFrom;
    }

    public void setMileageFrom(Double mileageFrom) {
        this.mileageFrom = mileageFrom;
    }

    public Double getMileageTo() {
        return mileageTo;
    }

    public void setMileageTo(Double mileageTo) {
        this.mileageTo = mileageTo;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getDoors() {
        return doors;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}


