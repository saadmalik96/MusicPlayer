# MusicPlayer

This is a music player that uses the AudioDB API in order to retrieve information on the user's music library.

It has the following features:
1. Adding songs to a database.
      -  Add a song with just an artist name.
      -  Add a song using the artist and track name
      -  Explore an artist's discography and add an entire album
2. View the song, artist and album libraries.
3. Generate playlists based on mood, genre or year.
4. Exception handling for invalid inputs and some quirks within the AudioDB API.


Some design decisions:
1. You can navigate back and forth between menus.
2. User input at most junctions is just picking from a list. 
   This minimizes incorrect input and makes it easier to add songs or albums with complicated names.
3. The AudioDB and MusicBrainz IDs are stored for all songs, artists and albums. 
   This will allow me to add more functionality to the music player in the future.
4. I have chosen to use the SQLite database for most things as the database will persist whereas 
   the songs arraylist will no longer exist whenever we exit the program

Room for improvement/potential future addons:
1. Use the AudioDB and/or MusicBrainz IDs to retrieve additional information on songs, albums and artists.
      Some examples
      - Biographical data on artists. For e.g. date and place of birth, aliases, genres, etc
      - Addtional production information on songs. Producers, Record labels, Collaborators and features on songs.
      - Album Descriptions
2. Incoropate data on song, artist or album popularity/rankings
3. Add lyrics for songs (wherever available)
4. A frontend (can also use available album art data to enhance this)
