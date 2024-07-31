package edu.uob;

public class GamePlayer {
    private Locations currentLocation;
    private Locations carryList;
    private int health;
    private Locations initial;

    public GamePlayer(){
        health = 3;
        initial = null;
    }

    public void changeHealth(Boolean increment) {
        if (increment) {
            if (this.health < 3) {
                this.health++;
            }
        } else {
            if (this.health > 0) {
                this.health--;
            }
        }
    }

    public void resetHealth() {
        this.health = 3;
    }

    public int getHealth(){
        return health;
    }

    public void setInitialLocation(Locations initialLocation) {
        this.initial = initialLocation;
        this.currentLocation = initialLocation;
    }

    public Locations getInitial() {
        return initial;
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
