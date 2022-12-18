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


    /**
     * Checks whether an album already exists in the database.
     * @param statement
     * @return
     */
    public boolean checkDuplicate(Statement statement) {
        try {
            ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() +"\"");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Adds an album to the database (given it doesn't already exist).
     * @param statement
     */
    public void addAlbumToDB(Statement statement) {
        try {
            String albumName = this.getName();
            if (checkDuplicate(statement)) {
                ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
            } else {
                this.getArtist().addArtistToDB(statement);
                statement.executeUpdate(this.toSQL());
                ResultSet rs = statement.executeQuery("select * from albums where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
                this.setMbid(rs.getString("mbid"));
                this.setAdbid(rs.getString("adbid"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Method for generating SQL query to add an album to the database.
     * @return
     */
    public String toSQL() {
        return "insert into albums (name, artist, adbid, mbid) values ("
                + "\"" + this.getName() + "\""
                + ", " + this.getArtist().getEntityID() + ", "
                + this.getAdbid() + ", "
                + "\"" + this.getMbid() + "\"" +
                ");";
    }
}
