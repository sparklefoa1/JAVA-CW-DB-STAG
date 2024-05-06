package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Locations extends GameEntity {
    private List<String> artefacts;
    private List<String> furniture;
    private List<String> characters;
    public Locations(String name, String description) {
        super(name, description);
        this.artefacts = new ArrayList<>();
        this.furniture = new ArrayList<>();
        this.characters = new ArrayList<>();
    }
    // 添加道具
    public void addArtefact(String artefactName, String artefactDescription) {
        artefacts.add(artefactName + ": " + artefactDescription);
    }

    // 添加家具
    public void addFurniture(String furnitureName, String furnitureDescription) {
        furniture.add(furnitureName + ": " + furnitureDescription);
    }

    // 添加角色
    public void addCharacter(String characterName, String characterDescription) {
        characters.add(characterName + ": " + characterDescription);
    }

    // Getter
    public List<String> getArtefacts() {
        return artefacts;
    }

    public List<String> getFurniture() {
        return furniture;
    }

    public List<String> getCharacters() {
        return characters;
    }
}
