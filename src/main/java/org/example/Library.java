package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    private ArrayList<Song> songs;
    Statement statement;
    Statement statement1;
    public Library() {
        songs = new ArrayList<Song>();
    }
    public Library(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
    public void addSong(Song s) {
        songs.add(s);
    }

    /**
     * Displays the complete song library
     */
    public void viewSongs() {
        try {
            ResultSet rs = this.statement.executeQuery("select * from songs");
            System.out.printf("%-4s %-40s %-40s %-40s\n","#", "Name", "Album", "Artist");
            System.out.println("----------------------------------------------------------------------------------------------");
            int counter = 1;
            while (rs.next()) {
                int artistID = rs.getInt("artist");
                int albumID = rs.getInt("album");
                ResultSet rs1 = this.statement1.executeQuery("select * from artists where id = " + artistID);
                String artistName = rs1.getString("name");

                rs1 = this.statement1.executeQuery("select * from albums where id = " + albumID);
                String albumName = rs1.getString("name");
                System.out.printf("%-4s %-40s %-40s %-40s\n", counter++, rs.getString("name"), albumName, artistName);
//                System.out.println(rs.getString("name"));
            }
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("Enter any key to continue.");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Displays all the artists
     */
    public void viewArtists() {
        System.out.printf("%-4s %-40s\n","#", "Name");
        System.out.println("-----------");
        try {
            ResultSet rs = this.statement.executeQuery("select * from artists");
            int counter = 1;
            while (rs.next()) {
                System.out.printf("%-4s %-20s\n",counter, rs.getString("name"));
                counter++;
            }
            System.out.println("-----------");
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter any key to continue.");
            String input = sc.nextLine();
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Displays all the albums
     */
    public void viewAlbums() {
        System.out.printf("%-4s %-40s\n","#", "Name");
        System.out.println("-----------");
        try {
            ResultSet rs = this.statement.executeQuery("select * from albums");
            while (rs.next()) {
                int counter = 1;
                System.out.printf("%-4s %-20s\n",counter, rs.getString("name"));
                counter++;
            }
            System.out.println("-----------");
            System.out.println("Enter any key to continue.");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Function takes in an artist name and returns all albums produced by that artist.
     * @param artistName
     * @return
     */
    public JSONArray getAlbumsFromArtist(String artistName) {
        artistName = artistName.toLowerCase();
        artistName = artistName.replaceAll(" ", "_");
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/searchalbum.php?s=" + artistName;
        return  (JSONArray)connection(requestURL).get("album"); // get the list of all albums returned.
    }


    /**
     * Function displays all albums of an artist for user input decision.
     * @param albums
     */
    public void displayAlbums(JSONArray albums) {
        JSONObject album;
        try {
            for (int i = 0; i < albums.size(); i++) {
                album = (JSONObject) albums.get(i);
                String albumName = (String)album.get("strAlbum");
                System.out.println((i+1) + ". " + albumName);
            }
        } catch (NullPointerException e) {
            System.out.println("Error:" + e);
            System.out.println("Artist does not exist.");
        }
    }


    /**
     * Displays all tracks within an album. Implemented for user input decision.
     * @param albumID
     */
    public void displayAlbum(String albumID) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?m=" + albumID;
        JSONArray tracks = (JSONArray)connection(requestURL).get("track"); // get the list of all tracks returned.
        JSONObject track;
        for (int i = 0; i < tracks.size(); i++) {
            track = (JSONObject) tracks.get(i);
            String trackName = (String)track.get("strTrack");
            System.out.println((i+1) + ". " + trackName);
        }
    }


    /**
     * Function adds a song that the user picks from a specific album
     * @param albums
     * @param input
     */
    public void addSongFromAlbum(JSONArray albums, int input) { //
        JSONObject album = (JSONObject)albums.get(input);
        String trackId = searchAlbum((String)album.get("idAlbum"));
        this.addSongFromAPI(trackId);
    }


    /**
     * function returns a trackID of a song chosen by user from a specific album
     * @param albumID
     * @return
     */
    public String searchAlbum(String albumID) {  //returns TrackID given an Album
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?m=" + albumID;
        JSONArray tracks = (JSONArray)connection(requestURL).get("track"); // get the list of all tracks returned.
        JSONObject track;
        displayAlbum(albumID);
        Scanner sc = new Scanner(System.in);
        System.out.println("What song would you like to pick? If it isn't in the list enter 0.");
        int input = Integer.valueOf(sc.nextLine());

        if (input == 0) {
            return "-1";
        }
        track = (JSONObject)tracks.get(input-1);
        return (String)track.get("idTrack");
    }


    /**
     * Establishes a connection with the API and returns a JSONObject given a specific request URL.
     * @param url
     * @return
     */
    public static JSONObject connection(String url) {
        StringBuilder response = new StringBuilder();
        URL u;
        u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
        }
        JSONObject jsonObject = null;
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            //System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                System.out.println("HTTP Error");
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            jsonObject = (JSONObject) obj;
        } catch (IOException e) {
            System.out.println("Error reading response");
        } catch (ParseException p) {
            System.out.println("Parsing error");
        }
        return jsonObject;
    } //processes requestURL and return JSONObject

    /**
     * Given a trackID, this function adds that song to the database. (Along with any missing values like artists, albums, etc
     * if needed (via helper functions)
     * @param trackID
     */
    public void addSongFromAPI (String trackID) {
        try {
            String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?h=" + trackID;
            JSONArray tracks = (JSONArray) connection(requestURL).get("track");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            String trackName = (String) track.get("strTrack");
            String artistName = (String)track.get("strArtist");
            String albumName = (String)track.get("strAlbum");
            String mood = (String)track.get("strMood");
            String year = (String)track.get("intYearReleased");
            String genre = (String)track.get("strGenre");

            String trackMBID =  (String) track.get("strMusicBrainzID");
            String trackADBID =  (String) track.get("idTrack");
            String albumMBID =  (String) track.get("strMusicBrainzAlbumID");
            String albumADBID =  (String) track.get("idAlbum");
            String artistMBID =  (String) track.get("strMusicBrainzArtistID");
            String artistADBID =  (String) track.get("idArtist");

            if (mood == null || year == null || mood.equals("...")) {
                String yearAndMood = getYearMood(albumADBID);
                String[] YMArray = yearAndMood.split(",");
                year = YMArray[0];
                mood = YMArray[1];
            }

            Song song = new Song(trackName, artistName, albumName, genre, mood, year, trackADBID, trackMBID);

            song.getArtist().setAdbid(artistADBID);
            song.getArtist().setMbid(artistMBID);
            song.getAlbum().setAdbid(albumADBID);
            song.getAlbum().setMbid(albumMBID);

            song.addSongtoDB(this.statement, this.statement1);
            this.addSong(song);
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
            System.out.println("Invalid TrackID");
        }

    }


    /**
     * If the user has complete details of the song i.e the Artist name and the track name, this function can add that song
     * to the database.
     * @param songName
     * @param artistName
     */
    public void addSongTrackAndArtist(String songName, String artistName) {
        artistName = artistName.toLowerCase();
        artistName = artistName.replaceAll(" ", "_");
        songName = songName.toLowerCase();
        songName = songName.replaceAll(" ", "_");
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/searchtrack.php?s=" + artistName + "&t=" + songName;

        try {
            JSONArray tracks = (JSONArray) connection(requestURL).get("track");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.w

            String trackName = (String) track.get("strTrack");
            artistName = (String)track.get("strArtist");
            String albumName = (String)track.get("strAlbum");
            String mood = (String)track.get("strMood");
            String year = (String)track.get("intYearReleased");
            String genre = (String)track.get("strGenre");

            String trackMBID =  (String) track.get("strMusicBrainzID");
            String trackADBID =  (String) track.get("idTrack");
            String albumMBID =  (String) track.get("strMusicBrainzAlbumID");
            String albumADBID =  (String) track.get("idAlbum");
            String artistMBID =  (String) track.get("strMusicBrainzArtistID");
            String artistADBID =  (String) track.get("idArtist");

            if (mood == null || year == null || mood.equals("...")) {
                String yearAndMood = getYearMood(albumADBID);
                String[] YMArray = yearAndMood.split(",");
                try {
                    year = YMArray[0];
                    mood = YMArray[1];
                } catch (IndexOutOfBoundsException index) {
                    System.out.println("Missing values");
                }

            }
            Song song = new Song(trackName, artistName, albumName, genre, mood, year, trackADBID, trackMBID);

            song.getArtist().setAdbid(artistADBID);
            song.getArtist().setMbid(artistMBID);
            song.getAlbum().setAdbid(albumADBID);
            song.getAlbum().setMbid(albumMBID);


            song.addSongtoDB(this.statement, this.statement1);
            this.addSong(song);
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
            System.out.println("Invalid Artist or Track Name");
        }

    }

    /**
     * This function can add all tracks from an album to the database given a valid albumID.
     * @param albumID
     */
    public void addAnAlbum(String albumID) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?m=" + albumID;
        JSONArray tracks = (JSONArray)connection(requestURL).get("track"); // get the list of all tracks returned.
        JSONObject track;
        for (int i = 0; i < tracks.size(); i++) {
            track = (JSONObject) tracks.get(i);
            String trackID = (String)track.get("idTrack");
            addSongFromAPI(trackID);
        }
    }


    /**
     * The AudioDB API is weird. Sometimes, tracks do not contain mood or year information, however, most of the time
     * the album data contains this information. This function return that information after getting it from the album data.
     * @param albumID
     * @return
     */
    public String getYearMood(String albumID) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/album.php?m=" + albumID;
        try {
            JSONArray tracks = (JSONArray) connection(requestURL).get("album");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            String year = (String) track.get("intYearReleased");
            String mood = (String) track.get("strMood");
            if (mood.equals("")) {
                mood = "null";
            }
            return year +"," + mood;

        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    /**
     * This method takes in the initial userInput from the main menu and runs the required method in the library class.
     * @param input
     */
    public void userInput(String input) {
        Scanner sc = new Scanner(System.in);

        if (input.equals("exit")) {
            return;
        } else if (input.equals("1")) {
            addingMusic();
        } else if (input.equals("2")) {
            viewSongs();
        } else if (input.equals("3")) {
            viewArtists();
        } else if (input.equals("4")) {
            viewAlbums();
        } else if (input.equals("5")) {
            generatePlaylist();
        }
    }



    /**
     * This method is the menu for generating playlists. It can generate playlists based on the year, mood or genre.
     * Since the user doesn't know what options are avaialble in each category, the method provides this information and
     * makes it easy for the user to chose from a given list. This should minimize incorrect user input.
     */
    public void generatePlaylist() {
        try {
            ArrayList<String> array = new ArrayList<>();
            int counter = 0;
            Scanner sc = new Scanner(System.in);
            System.out.println("What would you like to generate a playlist on? \n" +
                    "1. Genre \n" +
                    "2. Year \n" +
                    "3. Mood");
            String type = sc.nextLine();
            if (type.equals("1")) {
                type = "genre";
                System.out.print("What genre? \n");
                ResultSet rs = this.statement.executeQuery("select distinct genre from songs");
                System.out.println("Available options: ");
                while(rs.next()) {
                    array.add(rs.getString(type));
                    System.out.println((counter+1) + ". " + array.get(counter));
                    counter++;
                }
                System.out.print("Input: ");
                int input =   Integer.valueOf( sc.nextLine());
                writeAsXML(type, array.get(input - 1));
            } else if  (type.equals("2")) {
                ArrayList<Integer> numArray = new ArrayList<>();
                type = "year";
                System.out.print("What year? \n");
                ResultSet rs = this.statement.executeQuery("select distinct year from songs");
                System.out.println("Available options: ");
                while(rs.next()) {
                    numArray.add(rs.getInt(type));
                    System.out.println((counter+1) + ". " + numArray.get(counter));
                    counter++;
                }
                System.out.print("Input: ");
                int input =   Integer.valueOf( sc.nextLine());
                writeAsXML(type, String.valueOf(numArray.get(input - 1)));
            } else if  (type.equals("3")) {
                type = "mood";
                System.out.print("What mood? \n");
                ResultSet rs = this.statement.executeQuery("select distinct mood from songs");
                System.out.println("Available options: ");
                while(rs.next()) {
                    array.add(rs.getString(type));
                    System.out.println((counter+1) + ". " + array.get(counter));
                    counter++;
                }
                System.out.print("Input: ");
                int input =   Integer.valueOf( sc.nextLine());
                writeAsXML(type, array.get(input - 1));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } catch (IndexOutOfBoundsException index) {
            System.out.println("---------------");
            System.out.println("Invalid choice");
            System.out.println("---------------");
        }

    }


    /**
     * This method is the menu for adding music to the database. There are 3 ways to do this:
     * 1. You know the artist name. You can explore all the albums of that artist and pick a song from one of their albums.
     * 2. You have both the artist and track name. This will add the song directly.
     * 3. You want to add an entire album from a specific artist. The method will display all the albums from an artist and
     * allow you to pick an album of your choice.
     */
    public void addingMusic() {
        Scanner sc = new Scanner(System.in);
        String input = "";

        boolean action = true;
        while (action) {
            System.out.println("What would you like to search with?  Enter exit for main menu.\n" +
                    "1. I'd like to explore an artist. \n" +
                    "2. I already know the artist and song name. \n" +
                    "3. I want to add an entire album from an artist!");
            System.out.print("Input: ");
            input = sc.nextLine();
            if (input.equals("exit")) {
                return;
            } else if (input.equals("1")) {
                System.out.print("What is the artist's name? \nInput: ");
                input = sc.nextLine();
                JSONArray albums = getAlbumsFromArtist(input);
                if (albums == null) {
                    System.out.println("--------------------------------------------");
                    System.out.println("Artist does not exist in database. Try again.");
                    System.out.println("--------------------------------------------");
                    continue;
                }
                displayAlbums(albums);
                System.out.print("What album would you like to pick? Enter 0 for the previous menu. \nInput: ");
                int choice = -1;
                try {
                    choice = Integer.valueOf(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e);
                }

                if (choice == 0) {
                    continue;
                }
                addSongFromAlbum(albums, choice - 1);
                action = false;
            } else if (input.equals("2")) {
                System.out.print("What is the artist's name?\nInput: ");
                String artistName = sc.nextLine();
                System.out.print("\nWhat is the song name?\nInput: ");
                String songName = sc.nextLine();
                addSongTrackAndArtist(songName, artistName);
                action = false;
            } else if (input.equals("3")) {
                System.out.print("What is the artist's name?\nInput: ");
                String artistName = sc.nextLine();
                JSONArray albums = getAlbumsFromArtist(artistName);
                displayAlbums(albums);
                System.out.print("Which album would you like to add?\nInput: ");
                int choice = 0;
                try {
                    choice = Integer.valueOf(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e);
                }
                if (choice == 0) {
                    continue;
                }
                try {
                    JSONObject album = (JSONObject) albums.get(choice - 1);
                    String albumID = (String) album.get("idAlbum");
                    addAnAlbum(albumID);
                    action = false;
                } catch (IndexOutOfBoundsException index) {
                    System.out.println("--------------------------");
                    System.out.println("Invalid choice. Try again!");
                    System.out.println("--------------------------");
                }

            }
        }
    }

    /**
     * Given a column name and the user decision, this function generates an xml file from the database.
     * @param column
     * @param input
     */
    public void writeAsXML(String column, String input) {
        try {
            ResultSet rs;
            if (column.equals("year")) {
                rs = this.statement.executeQuery("select * from songs where " + column + " = " + input + ";");
            } else {
                rs = this.statement.executeQuery("select * from songs where " + column + " = \'" + input + "\';");
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("songs");

            while (rs.next()) {
                Element song = doc.createElement("song");
                rootElement.appendChild(song);

                int artistID = rs.getInt("artist");
                int albumID = rs.getInt("album");
                String genreName = rs.getString("genre");
                int yearValue = rs.getInt("year");
                String moodName = rs.getString("mood");

                song.setAttribute("ID", rs.getString("id"));
                Element songName = doc.createElement("title");
                songName.setTextContent(rs.getString("name"));
                song.appendChild(songName);

                Element artist = doc.createElement("artist");
                ResultSet rs1 = this.statement1.executeQuery("select * from artists where id = " + artistID + ";");
                String artistName = rs1.getString("name");
                artist.setTextContent(artistName);
                song.appendChild(artist);

                Element album = doc.createElement("album");
                rs1 = this.statement1.executeQuery("select * from albums where id = " + albumID + ";");
                String albumName = rs1.getString("name");
                album.setTextContent(albumName);
                song.appendChild(album);

                Element mood = doc.createElement("mood");
                mood.setTextContent(moodName);
                song.appendChild(mood);

                Element genre = doc.createElement("genre");
                genre.setTextContent(genreName);
                song.appendChild(genre);

                Element year = doc.createElement("year");
                year.setTextContent(String.valueOf(yearValue));
                song.appendChild(year);

                rootElement.appendChild(song);
            }

            doc.appendChild(rootElement);
            TransformerFactory tranFac = TransformerFactory.newInstance();
            Transformer transformer = tranFac.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            try (FileWriter file = new FileWriter("playlist.xml")) {
                file.write(sw.toString());
            } catch (IOException e) {
                System.out.println("IOException:" + e);
            }
        } catch (Exception e) {
            System.out.println("Parsing error.");
        }
    }
    public static void main (String[] args) {
        Library lib = new Library();
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
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
            System.out.println("\n-------------- Welcome to your Music Player ---------------\n");

            Scanner sc = new Scanner(System.in);
            String input = "";

            while (!input.equals("exit")) {
                System.out.println("------------------------ Main Menu ------------------------");
                System.out.println("What would you like to do? Type 'exit' to exit the program.");
                System.out.println("" +
                        "1. Add a song. \n" +
                        "2. View your song library. \n" +
                        "3. View your artists. \n" +
                        "4. View your albums. \n" +
                        "5. Generate a playlist based on mood, genre or era. \n" +
                        "--------------------------------------------------");
                System.out.print("Input: ");
                input = sc.nextLine();
                lib.userInput(input);

            }
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
}
