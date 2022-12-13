package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @BeforeAll
    void setup() {

    }

    @Test
    void checkDuplicate() {
    }

    @Test
    void addAlbumToDB() {
    }

    @Test
    void toSQL() {
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
            lib.statement.executeUpdate("drop table if exists albums");
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT)");
            lib.statement.executeUpdate("create table albums (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, artist INTEGER)");

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
//            artist2.addArtistToDB(lib.statement);

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

    @AfterAll
    void closeDB() {

    }
}