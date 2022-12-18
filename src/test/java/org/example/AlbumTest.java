package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {
    @Test
    void checkDuplicate() {
        Library lib = new Library();
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tests.db");
            connection.setAutoCommit(false);
            lib.statement = connection.createStatement();
            lib.statement1 = connection.createStatement();
            System.out.println("Opened database successfully");
            lib.statement.executeUpdate("drop table if exists songs");
            lib.statement.executeUpdate("drop table if exists albums");
            lib.statement.executeUpdate("drop table if exists artists");
            lib.statement.executeUpdate("create table songs (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, artist INTEGER, album INTEGER, year INTEGER, genre TEXT, mood TEXT, adbid INTEGER, mbid TEXT)");
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, adbid INTEGER, mbid TEXT)");
            lib.statement.executeUpdate("create table albums (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, artist INTEGER, adbid INTEGER, mbid TEXT)");

            Artist artist = new Artist("Nas");
            Artist artist1 = new Artist("Allah Las");

            Album album = new Album("Illmatic");
            Album album2 = new Album("Illmatic");
            Album album3 = new Album("Worship the Sun");

            album.setArtist(artist);
            album2.setArtist(artist);
            album3.setArtist(artist1);

            album.addAlbumToDB(lib.statement);
            album2.addAlbumToDB(lib.statement);
            album3.addAlbumToDB(lib.statement);

            ResultSet rs = lib.statement.executeQuery("select * from albums");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            assertEquals(2,counter);
            album.addAlbumToDB(lib.statement);
            album2.addAlbumToDB(lib.statement);
            album3.addAlbumToDB(lib.statement);

            lib.statement.close();
            lib.statement1.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void addAlbumToDB() {
        Library lib = new Library();
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tests.db");
            connection.setAutoCommit(false);
            lib.statement = connection.createStatement();
            lib.statement1 = connection.createStatement();
            System.out.println("Opened database successfully");
            lib.statement.executeUpdate("drop table if exists songs");
            lib.statement.executeUpdate("drop table if exists albums");
            lib.statement.executeUpdate("drop table if exists artists");
            lib.statement.executeUpdate("create table songs (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, artist INTEGER, album INTEGER, year INTEGER, genre TEXT, mood TEXT, adbid INTEGER, mbid TEXT)");
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, adbid INTEGER, mbid TEXT)");
            lib.statement.executeUpdate("create table albums (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, artist INTEGER, adbid INTEGER, mbid TEXT)");

            Artist artist = new Artist("Nas");
            Artist artist1 = new Artist("Allah Las");

            Album album = new Album("Illmatic");
            Album album2 = new Album("Magic");
            Album album3 = new Album("Worship the Sun");

            album.setArtist(artist);
            album2.setArtist(artist);
            album3.setArtist(artist1);

            album.addAlbumToDB(lib.statement);
            album2.addAlbumToDB(lib.statement);
            album3.addAlbumToDB(lib.statement);

            ResultSet rs = lib.statement.executeQuery("select * from albums");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            assertEquals(3,counter);
            album.addAlbumToDB(lib.statement);
            album2.addAlbumToDB(lib.statement);
            album3.addAlbumToDB(lib.statement);

            lib.statement.close();
            lib.statement1.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void toSQL() {
        Album album = new Album("Illmatic");
        Artist artist = new Artist("Artist");
        album.setArtist(artist);
        album.getArtist().setEntityID(3);
        album.setMbid("abcd");
        album.setAdbid("123");

        assertEquals("insert into albums (name, artist, adbid, mbid) values (\"Illmatic\", 3, 123, \"abcd\");", album.toSQL());


    }
}