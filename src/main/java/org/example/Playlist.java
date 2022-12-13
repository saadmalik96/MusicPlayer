package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Playlist {
    private ArrayList<Song> songs;
    public Playlist() {
        songs = new ArrayList<Song>();
    }

    public void addSong(Song s) {
        songs.add(s);
    }

    public void deleteSong(Song s) {
        if (songs.contains(s)) {
            songs.remove(s);
        } else {
            System.out.printf("%s is not in the playlist.\n",s.toString());
        }
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void shuffle () {
        Collections.shuffle(songs);
    }

    public void print() {
        for (Song song: this.songs) {
            System.out.println("Song: " + song.getName() + " Artist: " + song.artist.getName()
            + " Album: " + song.album.getName());
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
}
