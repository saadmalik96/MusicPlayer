package org.example;

import java.util.Date;

public class Entity {
    protected String name;
    protected static int counter = 0;
    protected int entityID;
    protected Date dateCreated;

    public Entity() {
        this.name = "";
        counter++;
        this.entityID = counter;
        dateCreated = new Date();
    }

    public boolean equals(Entity otherEntity) {
        return entityID == otherEntity.entityID;
    }


    public Entity(String name) {
        this.name = name;
        counter++;
        this.entityID = counter;
        dateCreated = new Date();
    }

    public void setEntityID(int id) {
        this.entityID = id;
    }

    public int getEntityID() {
        return this.entityID;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
