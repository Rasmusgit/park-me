package com.example.rasmus.parkme;

/**
 * Created by rasmus on 2017-01-29.
 */

public class ParkingLot {
    private String name;
    private int spaces;
    private int freeSpaces;
    private int distance;
    private String maxTime;
    private String cost;


    public ParkingLot(String name, int spaces, int freeSpaces, int distance, String maxTime, String cost) {
        this.name = name;
        this.spaces = spaces;
        this.freeSpaces = freeSpaces;
        this.distance = distance;
        this.maxTime = maxTime;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getSpaces() {
        return spaces;
    }

    public int getFreeSpaces() {
        return freeSpaces;
    }

    public int getDistance() {
        return distance;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public String getCost() {
        return cost;
    }
}
