package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {
    @Test
    void addSongtoDB() {
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

            Song song = new Song("Halftime", "Nas", "Illmatic", "Hip-Hop", "Energetic", "1994", "92831", "2awe12");
            Song song2 = new Song("The Genesis", "Nas", "Illmatic", "Hip-Hop", "Energetic", "1994", "92831", "2awe12");
            Song song3 = new Song("Speechless", "Nas", "Magic", "Hip-Hop", "Energetic", "2021", "92831", "2awe12");
            song.addSongtoDB(lib.statement, lib.statement1);
            song2.addSongtoDB(lib.statement, lib.statement1);
            song3.addSongtoDB(lib.statement, lib.statement1);


            ResultSet rs = lib.statement.executeQuery("select * from songs");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            assertEquals(3,counter);


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
    void checkDuplicateSong() {
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

            Song s1 = new Song("Halftime", "Nas", "Illmatic", "Hip-Hop", "Energetic", "1994", "92831", "2awe12");
            Song s2 = new Song("Halftime", "Nas", "Illmatic", "Hip-Hop", "Energetic", "1994", "92831", "2awe12");
            s1.addSongtoDB(lib.statement, lib.statement1);
            s2.addSongtoDB(lib.statement, lib.statement1);

            ResultSet rs = lib.statement.executeQuery("select * from songs");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            assertEquals(1,counter);

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
        Song song = new Song("Halftime", "Nas", "Illmatic", "Hip-Hop", "Energetic", "1994", "92831", "2awe12");
        System.out.println(song.toSQL());
        assertEquals("insert into songs(name, artist, album, year, genre, mood, adbid, mbid) values (\"Halftime\", 2, 3, 1994, \"Hip-Hop\", \"Energetic\", 92831, \"2awe12\");", song.toSQL());
    }
}