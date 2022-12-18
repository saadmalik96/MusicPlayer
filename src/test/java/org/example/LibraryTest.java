package org.example;

import org.json.simple.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    void getAlbumsFromArtist() {
        Library lib = new Library();
        //checking on number of albums
        JSONArray albums = lib.getAlbumsFromArtist("Nas");
        assertEquals(21, albums.size());

        albums = lib.getAlbumsFromArtist("The Beatles");
        assertEquals(51, albums.size());

        albums = lib.getAlbumsFromArtist("Queen");
        assertEquals(62, albums.size());
    }

    @Test
    void addSongFromAPI() {
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

            lib.addSongFromAPI("32793500");
            ResultSet rs = lib.statement.executeQuery("select * from songs");
            assertEquals("D.D.", rs.getString("name"));


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
    void addSongTrackAndArtist() {
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

            lib.addSongTrackAndArtist("Halftime", "Nas");
            ResultSet rs = lib.statement.executeQuery("select * from songs");
            assertEquals("Halftime", rs.getString("name"));


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
    void addAnAlbum() {
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

            lib.addAnAlbum("2115030");
            ResultSet rs = lib.statement.executeQuery("select * from songs");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            assertEquals(10, counter);


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
    void getYearMood() {
        Library lib = new Library();
        assertEquals("1994,Epic", lib.getYearMood("2115030"));
    }
}