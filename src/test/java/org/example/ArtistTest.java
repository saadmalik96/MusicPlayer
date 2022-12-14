package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void checkDuplicatebyName() {
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
            lib.statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT)");

            Artist artist = new Artist("Nas");
            Artist artist2 = new Artist("Nas");
            artist.addArtistToDB(lib.statement);
            artist2.addArtistToDB(lib.statement);

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

        System.out.println(artist.toSQL());

    }
}