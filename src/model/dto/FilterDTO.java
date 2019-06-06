package model.dto;

import java.io.Serializable;

public class FilterDTO implements Serializable {

    private String maker;
    private String model;

    private double priceFrom;
    private double priceTo;

    private String fuel;

    private double ccFrom;
    private double ccTo;

    private double yearFrom;
    private double yearTo;

    private double powerFrom;
    private double powerTo;

    private double mileageFrom;
    private double mileageTo;

    private int seats;
    private int doors;
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

    public double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(double priceTo) {
        this.priceTo = priceTo;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public double getCcFrom() {
        return ccFrom;
    }

    public void setCcFrom(double ccFrom) {
        this.ccFrom = ccFrom;
    }

    public double getCcTo() {
        return ccTo;
    }

    public void setCcTo(double ccTo) {
        this.ccTo = ccTo;
    }

    public double getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(double yearFrom) {
        this.yearFrom = yearFrom;
    }

    public double getYearTo() {
        return yearTo;
    }

    public void setYearTo(double yearTo) {
        this.yearTo = yearTo;
    }

    public double getPowerFrom() {
        return powerFrom;
    }

    public void setPowerFrom(double powerFrom) {
        this.powerFrom = powerFrom;
    }

    public double getPowerTo() {
        return powerTo;
    }

    public void setPowerTo(double powerTo) {
        this.powerTo = powerTo;
    }

    public double getMileageFrom() {
        return mileageFrom;
    }

    public void setMileageFrom(double mileageFrom) {
        this.mileageFrom = mileageFrom;
    }

    public double getMileageTo() {
        return mileageTo;
    }

    public void setMileageTo(double mileageTo) {
        this.mileageTo = mileageTo;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getDoors() {
        return doors;
    }

    public void setDoors(int doors) {
        this.doors = doors;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}


