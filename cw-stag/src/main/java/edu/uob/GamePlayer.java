package edu.uob;

public class GamePlayer {
    private Locations currentLocation;
    private Locations carryList;

    public void setInitialLocation(Locations initialLocation) {
        this.currentLocation = initialLocation;
    }
    public void setCurrentLocation(Locations currentLocation) {
        this.currentLocation = currentLocation;
    }
    public Locations getCurrentLocation() {
        return currentLocation;
    }
    public void setCarryList(Locations carryList) {
        this.carryList = carryList;
    }
    public  Locations getCarryList() {
        return carryList;
    }
}
