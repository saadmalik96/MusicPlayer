package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.Scanner;

public class MusicPlayer {
    protected Library library;

    public MusicPlayer() {
        library = new Library();
    }

    public static void main (String[] args) {
        MusicPlayer mp = new MusicPlayer();
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to your Music Player! \nWhat would you like to do?");
        System.out.println("1. Add a song");
        System.out.println("2. Remove a song");
        System.out.println("3. Lookup a song");
        System.out.println("4. View songs");
        System.out.println("5. View artists");
        System.out.println("6. View albums");
        System.out.println("7. Display your Song Library");
        System.out.println("8. Generate random playlist based on genre");
        System.out.println("9. Import library from JSON/XML");
        System.out.println("10. Export library to JSON/XML");
    }
}





/*
Core Functionality:
Manually add a song to library
    - Manually ask user for song name, artist name, album name, interval, etc;
    - How do you connect existing album and/or artists to a new song?
Remove a song from Library
    -Enter name

Ideas:
--> Using streams to deal with the ResultSet



 */