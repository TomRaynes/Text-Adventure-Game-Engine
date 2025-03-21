package edu.uob.entity;

public abstract class GameEntity {

    private String name;
    private String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        // TODO: remove string concatenation
        return name + ": " + description;
    }

    public String getNameDescription() {
        return name + ": " + description;
    }
}
