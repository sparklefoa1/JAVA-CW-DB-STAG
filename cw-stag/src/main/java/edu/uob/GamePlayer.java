package edu.uob;

public class GamePlayer {
    private Locations currentLocation;

    public void setInitialLocation(Locations initialLocation) {
        this.currentLocation = initialLocation;
    }
    public void setCurrentLocation(Locations currentLocation) {
        this.currentLocation = currentLocation;
    }
    public Locations getCurrentLocation() {
        return currentLocation;
    }
}
