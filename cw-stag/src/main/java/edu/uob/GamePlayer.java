package edu.uob;

public class GamePlayer {
    private Locations currentLocation;
    private Locations storeroom;

    public void setInitialLocation(Locations initialLocation) {
        this.currentLocation = initialLocation;
    }
    public void setCurrentLocation(Locations currentLocation) {
        this.currentLocation = currentLocation;
    }
    public Locations getCurrentLocation() {
        return currentLocation;
    }
    public void setStoreroom(Locations storeroom) {
        this.storeroom = storeroom;
    }
    public  Locations getStoreroom() {
        return storeroom;
    }
}
