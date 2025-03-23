package edu.uob.entity;

import java.lang.Character;

public abstract class GameEntity {

    private final String name;
    private final String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getTitleCaseName() {
        StringBuilder sb = new StringBuilder(Character.toString(name.toUpperCase().charAt(0)));

        if (name.length() > 1) {
            sb.append(name.substring(1));
        }
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        // TODO: remove string concatenation
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(description);
        return sb.toString();
    }

    public String getNameDescription() {
        return name + ": " + description;
    }
}
