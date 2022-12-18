package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void checkDuplicatebyName() {
        Library lib = new Library();
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tests.db");
            connection.setAutoCommit(false);
            lib.statement = connection.createStatement();
            lib.statement1 = connection.createStatement();
            System.out.println("Opened database successfully");
            lib.statement.executeUpdate("drop table if exists artists");
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, adbid INTEGER, mbid TEXT)");

            Artist artist = new Artist("Nas");
            Artist artist2 = new Artist("Nas");
            artist.addArtistToDB(lib.statement);
            artist2.addArtistToDB(lib.statement);

            ResultSet rs = lib.statement.executeQuery("select * from artists");
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
    void addArtistToDB() {
        Library lib = new Library();
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tests.db");
            connection.setAutoCommit(false);
            lib.statement = connection.createStatement();
            lib.statement1 = connection.createStatement();
            System.out.println("Opened database successfully");
            lib.statement.executeUpdate("drop table if exists artists");
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, adbid INTEGER, mbid TEXT)");

            Artist artist = new Artist("Nas");
            Artist artist2 = new Artist("Queen");
            artist.addArtistToDB(lib.statement);
            artist2.addArtistToDB(lib.statement);

            ResultSet rs = lib.statement.executeQuery("select * from artists");
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            assertEquals(2,counter);

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
        Artist artist = new Artist("Nas");
        artist.setMbid("asd");
        artist.setAdbid("1234");

        assertEquals("insert into artists (name, adbid, mbid) values(\"Nas\", 1234, \"asd\");", artist.toSQL());

    }
}