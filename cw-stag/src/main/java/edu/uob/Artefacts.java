package edu.uob;

public class Artefacts extends GameEntity{
    public Artefacts(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Artefacts{" + "name='" + getName() + '\'' + ", description='" + getDescription() + '\'' + '}';
    }
}
