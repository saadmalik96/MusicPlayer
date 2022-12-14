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

    public boolean findSong(Song s) {
        return songs.contains(s);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song s) {
        songs.add(s);
    }

    public void viewSongs() {
        try {
            ResultSet rs = this.statement.executeQuery("select * from songs");
            while (rs.next()) {
                int artistID = rs.getInt("artist");
                int albumID = rs.getInt("album");
                ResultSet rs1 = this.statement1.executeQuery("select * from artists where id = " + artistID);
                String artistName = rs1.getString("name");

                rs1 = this.statement1.executeQuery("select * from albums where id = " + albumID);
                String albumName = rs1.getString("name");

                System.out.println(rs.getString("name") + " " + albumName + " " + artistName);
//                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void searchArtist(String artistName) {
        artistName = artistName.toLowerCase();
        artistName = artistName.replaceAll(" ", "_");
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/searchalbum.php?s=" + artistName;
        StringBuilder response = connection(requestURL);
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray albums = (JSONArray)jsonObject.get("album"); // get the list of all albums returned.

            Scanner sc = new Scanner(System.in);
            String albumID = "";
            JSONObject album;
            for (int i = 0; i < albums.size(); i++) {
                album = (JSONObject) albums.get(i);
                String albumName = (String)album.get("strAlbum");
                System.out.println((i+1) + ". " + albumName);
            }
            System.out.println("What album would you like to pick? If it isn't in the list enter 0.");
            int input = 0;
            try {
                input = Integer.valueOf(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: " + e);
            }


            if (input == 0) {
                return;
            }
            album = (JSONObject)albums.get(input-1);
            String trackId = searchAlbum((String)album.get("idAlbum"));
            this.addSongFromAPI(trackId);

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        }
    } //takes in artist name and returns list of JSONArray of albums.

    public static String searchAlbumArray(JSONArray albums) { //this function takes in a list of albums and returns the albumID for the song user is interested in.
        Scanner sc = new Scanner(System.in);
        String albumID = "";
        JSONObject album;
        for (int i = 0; i < albums.size(); i++) {
            album = (JSONObject) albums.get(i);
            String albumName = (String)album.get("strAlbum");
            System.out.println((i+1) + ". " + albumName);
            }
        System.out.println("What album would you like to pick? If it isn't in the list enter 0.");
        int input = Integer.valueOf(sc.nextLine());

        if (input == 0) {
            return "-1";
        }
        album = (JSONObject)albums.get(input-1);
        return (String)album.get("idAlbum");
    }

    public static String searchAlbum(String albumID) {  //returns TrackID given an Album
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?m=" + albumID;
        StringBuilder response = connection(requestURL);
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject track;
            JSONArray tracks = (JSONArray)jsonObject.get("track"); // get the list of all tracks returned.
            for (int i = 0; i < tracks.size(); i++) {
                track = (JSONObject) tracks.get(i);
                String trackName = (String)track.get("strTrack");
                System.out.println((i+1) + ". " + trackName);
            }
            Scanner sc = new Scanner(System.in);
            System.out.println("What album would you like to pick? If it isn't in the list enter 0.");
            int input = Integer.valueOf(sc.nextLine());

            if (input == 0) {
                return "-1";
            }
            track = (JSONObject)tracks.get(input-1);
            return (String)track.get("idTrack");

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        }
    }
    public static StringBuilder connection(String url) {
        StringBuilder response = new StringBuilder();
        URL u;
        u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                System.out.println("HTTP Error");
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
        }
        return response;
    }

    public void addSongFromAPI (String trackID) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/track.php?h=" + trackID;
        StringBuilder response = connection(requestURL);//new StringBuilder();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray) jsonObject.get("track");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            System.out.println(track.get("strTrack"));
            System.out.println(track.get("strArtist"));
            System.out.println(track.get("strAlbum"));
            System.out.println(track.get("strMood"));
            System.out.println(track.get("intYearReleased"));
            System.out.println(track.get("strGenre"));

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

            System.out.println(trackName + " " + artistName + " " + albumName + " " + mood + " " + year + " " + genre);
            Song song = new Song(trackName, artistName, albumName, genre, mood, year, trackADBID, trackMBID);

            song.getArtist().setAdbid(artistADBID);
            song.getArtist().setMbid(artistMBID);
            song.getAlbum().setAdbid(albumADBID);
            song.getAlbum().setMbid(albumMBID);

            song.addSongtoDB(this.statement, this.statement1);
            this.addSong(song);

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        }
    }

    public void addSongTrackandArtist(String songName, String artistName) {
        artistName = artistName.toLowerCase();
        artistName = artistName.replaceAll(" ", "_");
        songName = songName.toLowerCase();
        songName = songName.replaceAll(" ", "_");

        String requestURL = "https://theaudiodb.com/api/v1/json/523532/searchtrack.php?s=" + artistName + "&t=" + songName;
        StringBuilder response = connection(requestURL);
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray) jsonObject.get("track");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            System.out.println(track.get("strTrack"));
            System.out.println(track.get("strArtist"));
            System.out.println(track.get("strAlbum"));
            System.out.println(track.get("strMood"));
            System.out.println(track.get("intYearReleased"));
            System.out.println(track.get("strGenre"));

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
                year = YMArray[0];
                mood = YMArray[1];
            }

            System.out.println(trackName + " " + artistName + " " + albumName + " " + mood + " " + year + " " + genre);
            Song song = new Song(trackName, artistName, albumName, genre, mood, year, trackADBID, trackMBID);

            song.getArtist().setAdbid(artistADBID);
            song.getArtist().setMbid(artistMBID);
            song.getAlbum().setAdbid(albumADBID);
            song.getAlbum().setMbid(albumMBID);


            song.addSongtoDB(this.statement, this.statement1);
            this.addSong(song);
        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }

    }

    public void addAnAlbum(String artistName, String albumName) {
        artistName = artistName.toLowerCase();
        artistName = artistName.replaceAll(" ", "_");
        albumName = albumName.toLowerCase();
        albumName = albumName.replaceAll(" ", "_");

        String requestURL = "theaudiodb.com/api/v1/json/523532/searchalbum.php?s=" + artistName + "&a=" + albumName;
        StringBuilder response = connection(requestURL);
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray) jsonObject.get("album");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            //Remove these souts
            System.out.println(track.get("strTrack"));
            System.out.println(track.get("strArtist"));
            System.out.println(track.get("strAlbum"));
            System.out.println(track.get("strMood"));
            System.out.println(track.get("intYearReleased"));
            System.out.println(track.get("strGenre"));

            String trackName = (String) track.get("strTrack");
            artistName = (String)track.get("strArtist");
            albumName = (String)track.get("strAlbum");
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

            System.out.println(trackName + " " + artistName + " " + albumName + " " + mood + " " + year + " " + genre);
            Song song = new Song(trackName, artistName, albumName, genre, mood, year, trackADBID, trackMBID);

            song.getArtist().setAdbid(artistADBID);
            song.getArtist().setMbid(artistMBID);
            song.getAlbum().setAdbid(albumADBID);
            song.getAlbum().setMbid(albumMBID);


            song.addSongtoDB(this.statement, this.statement1);
            this.addSong(song);
        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }

    }

//    public void addSongManually() {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter the following information seperated by just commas (no spaces): ");
//        System.out.println("Song, Artist, Album, Genre, and Mood, Year. \n" +
//                "(You can leave out any information except the song name.)");
//        String input = sc.nextLine();
//        String[] inputArray = input.split("[,]", 0);
//        for(String myStr: inputArray) {
//            System.out.println(myStr);
//        }
//
//        String songName = inputArray[0];
//        String artistName = inputArray[1];
//        String albumName = inputArray[2];
//        String genre = inputArray[3];
//        String mood = inputArray[4];
//        String year = inputArray[5];
//
//        if (artistName.equals("")) {
//            artistName = "Unknown";
//        }
//        if (albumName.equals("")) {
//            albumName = "Unknown";
//        }
//        if (genre.equals("")) {
//            genre = "Unknown";
//        }
//        if (mood.equals("")) {
//            mood = "Unknown";
//        }
//        if (year.equals("")) {
//            year = "0";
//        }
//
//        Song song = new Song(songName,artistName,albumName,genre,mood,year);
//        song.addSongtoDB(statement, statement1);
//    }

    /*
    1. Add a song:
            --
       Ok what do you have?
        1. Artist name and track name
        2. Just the artist name.
        3. Album name

     */


    public void userInput(String input) {
        Scanner sc = new Scanner(System.in);

        if (input.equals("exit")) {
            return;
        } else if (input.equals("1")) {
            userSearch();
        } else if (input.equals("2")) {
            viewSongs();
        }
    }

    public String getYearMood(String albumID) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/album.php?m=" + albumID;
        StringBuilder response = connection(requestURL);
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray) jsonObject.get("album");
            JSONObject track = (JSONObject) tracks.get(0); // get the list of all tracks returned.

            String year = (String) track.get("intYearReleased");
            String mood = (String) track.get("strMood");
            return year +"," + mood;

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public void userSearch() {
        Scanner sc = new Scanner(System.in);
        String input = "";

        System.out.println("What would you like to search with? \n" +
                "1. I'd like to explore an artist. \n" +
                "2. I already know the artist and song name. \n" +
                "3. I want to add an entire album!");

        input = sc.nextLine();
        if (input.equals("exit")) {
            return;
        } else if (input.equals("1")) {
            System.out.print("What is the artist's name? ");
            input = sc.nextLine();
            searchArtist(input);
        } else if (input.equals("2")) {
            System.out.print("What is the artist's name? ");
            String artistName = sc.nextLine();
            System.out.print("\nWhat is the song name? ");
            String songName = sc.nextLine();
            addSongTrackandArtist(songName, artistName);
        } else if (input.equals("3")) {
            System.out.print("What is the artist's name? ");
            String artistName = sc.nextLine();
            System.out.print("\nWhat is the album name? ");
            String albumName = sc.nextLine();
            addAnAlbum(artistName, albumName);
        }
    }





    public static void main (String[] args) {
        Library lib = new Library();


        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
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
                        "6. Statistics. \n" +
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



    public void writeAsXML(String filename) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("songs");

            for (Song s: songs) {
                Element song = doc.createElement("song");
                rootElement.appendChild(song);

                song.setAttribute("EntityID", String.valueOf(s.entityID));
                Element songName = doc.createElement("title");
                songName.setTextContent(s.getName());
                song.appendChild(songName);

                Element artistName = doc.createElement("artist");
                artistName.setTextContent(s.getArtist().getName());
                song.appendChild(artistName);

                Element albumName = doc.createElement("album");
                albumName.setTextContent(s.getAlbum().getName());
                song.appendChild(albumName);

                rootElement.appendChild(song);
            }

            doc.appendChild(rootElement);
            TransformerFactory tranFac = TransformerFactory.newInstance();
            Transformer transformer = tranFac.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            try (FileWriter file = new FileWriter(filename)) {
                file.write(sw.toString());
            } catch (IOException e) {
                System.out.println("IOException:" + e);
            }
        } catch (Exception e) {
            System.out.println("Parsing error.");
        }
    }

    public void writeAsJSON(String filename) {
        JSONObject root = new JSONObject();
        JSONArray songList = new JSONArray();

        for (Song s: songs) {
            JSONObject song  = new JSONObject();
            song.put("title", s.getName());
            song.put("artist", s.getArtist().getName());
            song.put("album", s.getAlbum().getName());
            songList.add(song);
        }
        root.put("songs", songList);
        try (FileWriter file = new FileWriter(filename)) {
            file.write(root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromXML(String filename) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));

            Element root = doc.getDocumentElement();
            System.out.println("Root: " + root);
            NodeList songList = root.getElementsByTagName("song");
            Node currentNode, subNode;

            Song currentSong;


            for (int i = 0; i < songList.getLength(); i++) {
                currentNode = songList.item(i);
//                System.out.println("Current Node: " + currentNode.getNodeName());
                NodeList children = currentNode.getChildNodes();
                currentSong = new Song();
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) currentNode;
//                    System.out.println(eElement.getAttribute("id"));
                    currentSong.setName(eElement.getElementsByTagName("title").item(0).getTextContent().trim());
                    Artist artist = new Artist(eElement.getElementsByTagName("artist").item(0).getTextContent().trim());
                    Album album = new Album(eElement.getElementsByTagName("album").item(0).getTextContent().trim());
                    currentSong.setArtist(artist);
                    currentSong.setAlbum(album);
                    songs.add(currentSong);
                }
            }
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
    }

}
