package edu.uob;

import org.w3c.dom.Element;

import java.util.List;

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
    public List<String> getSubjectEntities()
    {
        return subjectEntities;
    }
}
