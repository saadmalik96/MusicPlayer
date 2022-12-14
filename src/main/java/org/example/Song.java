package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Song extends Entity {
    protected Album album;
    protected Artist artist;
    //protected SongInterval duration;
    protected String genre;
    protected String mood;
    String year;
    String adbid;
    String mbid;


    public Song() {
        this.name = name;
        this.artist = new Artist();
        this.album = new Album();
        this.album.setArtist(this.artist);
        this.genre = "";
        this.mood = "";
        this.year = "0";
        this.adbid = "";
        this.mbid = "";
    }

    public Song(String name) {
        super(name);
        this.album = new Album("");
        this.artist = new Artist("");
        this.genre = "";
        this.adbid = "";
        this.mbid = "";
    }

    public Song(String name, String artistName, String albumName, String genre, String mood, String year, String adbid, String mbid) {
        this.name = name;
        this.artist = new Artist(artistName);
        this.album = new Album(albumName);
        this.album.setArtist(this.artist);
        this.genre = genre;
        this.mood = mood;
        this.year = year;
        this.adbid = adbid;
        this.mbid = mbid;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    protected Album getAlbum() {
        return album;
    }
    protected void setAlbum(Album album) {
        this.album = album;
    }
    public Artist getArtist() {
        return artist;
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



    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public void addSongtoDB(Statement statement, Statement statement1) {
        try {
            String artistName = this.getName();
            if (this.checkDuplicateSong(statement, statement1)) {
                ResultSet rs = statement.executeQuery("select * from songs where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
                System.out.println("Song already exists in database.");
            } else {
                this.getAlbum().addAlbumToDB(statement);
                this.getArtist().addArtistToDB(statement);
                statement.executeUpdate(this.toSQL());
                ResultSet rs = statement.executeQuery("select * from songs where name = \"" + this.getName() + "\"");
                this.setEntityID(rs.getInt("id"));
                System.out.println("Added new song. Name: " + this.getName() + " | id: " + this.getEntityID());
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public boolean checkDuplicateSong(Statement statement, Statement statement1) {
        try {
            ResultSet rs = statement.executeQuery("select * from songs where name = \"" + this.getName() +"\"");
            while (rs.next()) {
                ResultSet rs1 = statement1.executeQuery("select * from artists where id = " + rs.getInt("artist"));
                if (rs1.getString("name").equals(this.getArtist().getName()) && rs.getString("name").equals(this.getName())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public String toSQL() {
        return "insert into songs(name, artist, album, year, genre, mood, adbid, mbid) values ("
                + "\"" + getName() + "\", "
                + this.getArtist().getEntityID() + ", "
                + this.getAlbum().getEntityID() + ", "
                + this.year + ", "
                +  "\"" + this.genre + "\", "
                +  "\"" + this.mood + "\", "
                + this.adbid + ", "
                +  "\"" + this.mbid + "\""
                + ");";
    }
}
