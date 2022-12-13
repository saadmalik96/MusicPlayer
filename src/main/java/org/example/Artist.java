package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Artist extends Entity {

    public Artist(){
        this.name = "";
    }
    public Artist(String name) {
        super(name);
    }

    public String getName() {
        return this.name;
    }


    public boolean checkDuplicate(Statement statement) {
        try {
            ResultSet rs = statement.executeQuery("select * from artists where name = \"" + this.getName() +"\"");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addArtistToDB(Statement statement) {
        try {
            String artistName = this.getName();
            if (checkDuplicate(statement)) {
                ResultSet rs = statement.executeQuery("select * from artists where name = \"" + artistName + "\"");
                this.setEntityID(rs.getInt("id"));
                System.out.println("Artist already exists in database.");
            } else {
                statement.executeUpdate(this.toSQL());
                ResultSet rs = statement.executeQuery("select * from artists where name = \"" + artistName + "\"");
                this.setEntityID(rs.getInt("id"));
                System.out.println("Added new artist. Name: " + this.getName() + " | id: " + this.getEntityID());
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }



    public String toSQL() {
        return "insert into artists (name) values (" + "\"" + this.getName() + "\");";
    }
}
