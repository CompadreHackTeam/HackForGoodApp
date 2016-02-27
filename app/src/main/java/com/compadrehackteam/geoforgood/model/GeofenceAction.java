package com.compadrehackteam.geoforgood.model;

/**
 * Created by ricardo on 24/11/15.
 */
public class GeofenceAction {

    /**
     * Enumerated for the possible transition types.
     */
    public enum Transition {
        ENTER,
        EXIT,
        DWELL
    }

    /**
     * The transition
     */
    private Transition transition;

    /**
     * The content of the action (Json)
     */
    private String content;

    /**
     * Parametrized constructor
     *
     * @param transition The transition (enter,exit,dwell)
     * @param content    The content (JSON)
     */
    public GeofenceAction(Transition transition, String content) {
        this.transition = transition;
        this.content = content;
    }

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
