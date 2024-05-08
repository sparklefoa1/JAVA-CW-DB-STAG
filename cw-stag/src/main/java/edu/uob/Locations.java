package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locations extends GameEntity {
    private Map<String, Artefacts> artefacts;
    private Map<String, Furniture> furniture;
    private Map<String, Characters> characters;
    public Locations(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.furniture = new HashMap<>();
        this.characters = new HashMap<>();
    }
    // add artefact
    public void addArtefact(String artefactName, String artefactDescription) {
        artefacts.put(artefactName, new Artefacts(artefactName, artefactDescription));
    }

    // add furniture
    public void addFurniture(String furnitureName, String furnitureDescription) {
        furniture.put(furnitureName, new Furniture(furnitureName, furnitureDescription));
    }

    // add character
    public void addCharacter(String characterName, String characterDescription) {
        characters.put(characterName, new Characters(characterName, characterDescription));
    }

    // Getter
    public Artefacts getArtefacts(String arteFactName) {
        return artefacts.get(arteFactName);
    }
    public Map<String, Artefacts> getAllArtefacts() {return artefacts;}

    public Furniture getFurniture(String furnitureName) {
        return furniture.get(furnitureName);
    }
    public Map<String, Furniture> getAllFurniture() {return furniture;}
    public Characters getCharacters(String characterName) {return characters.get(characterName);}
    public Map<String, Characters> getAllCharacters() {return characters;}

}
