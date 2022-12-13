package org.example;

public class SongInterval {
    private int length;

    public SongInterval() {

    }

    public SongInterval(int length) {
        this.length = length;
    }

    public String toString() {
        int minutes = this.length/60;
        int seconds = this.length%60;
        return String.format("%d:%d",minutes,seconds);
    }
}
