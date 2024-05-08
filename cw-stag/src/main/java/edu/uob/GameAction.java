package edu.uob;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameAction {
    private List<String> triggerKeyphrases;
    private List<String> subjectEntities;
    private List<String> consumedEntities;
    private List<String> producedEntities;
    private String narration;
    public GameAction(List<String> triggerKeyphrases, List<String> subjectEntities, List<String> consumedEntities, List<String> producedEntities, String narration) {
        this.triggerKeyphrases = triggerKeyphrases;
        this.subjectEntities = subjectEntities;
        this.consumedEntities = consumedEntities;
        this.producedEntities = producedEntities;
        this.narration = narration;
    }
    public List<String> getTriggerKeyphrases() {
        return triggerKeyphrases;
    }
    public List<String> getSubjectEntities()
    {
        return subjectEntities;
    }
    public List<String> getConsumedEntities() {
        return consumedEntities;
    }

    public List<String> getProducedEntities() {
        return producedEntities;
    }

    public String getNarration() {
        return narration;
    }
}
