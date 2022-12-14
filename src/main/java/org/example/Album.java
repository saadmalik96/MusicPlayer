package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Album extends Entity {
    protected ArrayList<Song> songs;
    protected Artist artist;
    String adbid;
    String mbid;

    public Album() {
        this.name = "";
        this.artist = new Artist();
        this.adbid = "0";
        this.mbid = "0";
    }

    public Album(String name) {
        super(name);
        this.artist = new Artist();
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
    public Artist getArtist() {
        return this.artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public boolean checkDuplicate(Statement statement) {
        try {
            ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() +"\"");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addAlbumToDB(Statement statement) {
        try {
            String albumName = this.getName();
            if (checkDuplicate(statement)) {
                ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
                System.out.println("Album already exists in database.");
            } else {
                this.getArtist().addArtistToDB(statement);
                statement.executeUpdate(this.toSQL());
                ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
                this.setMbid(rs.getString("mbid"));
                this.setAdbid(rs.getString("adbid"));
                System.out.println("Added new album. Name: " + this.getName() + " | id: " + this.getEntityID());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }


    public String toSQL() {
        return "insert into albums (name, artist, adbid, mbid) values ("
                + "\"" + this.getName() + "\""
                + ", " + this.getArtist().getEntityID() + ", "
                + this.getAdbid() + ", "
                + "\"" + this.getMbid() + "\"" +
                ");";
    }
}
