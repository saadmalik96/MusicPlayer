package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Artist extends Entity {

    String adbid;
    String mbid;
    public Artist(){
        this.name = "";
        this.adbid = "0";
        this.mbid = "0";
    }
    public Artist(String name) {
        super(name);
        this.adbid = "0";
        this.mbid = "0";
    }
    public String getAdbid() {
        return adbid;
    }
    public void setAdbid(String adbid) {
        this.adbid = adbid;
    }
    public String getMbid() {
        return mbid;
    }
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }
    public String getName() {
        return this.name;
    }


    /**
     * Checks whether an artist already exists in the database.
     * @param statement
     * @return
     */
    public boolean checkDuplicate(Statement statement) {
        try {
            ResultSet rs = statement.executeQuery("select * from artists where name = \"" + this.getName() +"\"");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Adds an artist to the database (given it doesn't already exist).
     * @param statement
     */
    public void addArtistToDB(Statement statement) {
        try {
            String artistName = this.getName();
            if (checkDuplicate(statement)) {
                ResultSet rs = statement.executeQuery("select * from artists where name = \"" + artistName + "\"");
                this.setEntityID(rs.getInt("id"));
            } else {
                statement.executeUpdate(this.toSQL());
                ResultSet rs = statement.executeQuery("select * from artists where name = \"" + artistName + "\"");
                this.setEntityID(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Method for generating SQL query to add an artist to the database.
     * @return
     */
    public String toSQL() {
        return "insert into artists (name, adbid, mbid) values(" +
                "\"" + this.getName() + "\", "
                + this.getAdbid() + ", " +
                "\"" + this.getMbid() + "\"" +
                ");";
    }
}
