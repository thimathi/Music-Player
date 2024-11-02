import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private List<Song> songs;

    public Playlist() {
        songs = new ArrayList<>();
    }

    public void addSongs(List<Song> newSongs) {
        songs.addAll(newSongs);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public Song getSong(int index) {
        if (index >= 0 && index < songs.size()) {
            return songs.get(index);
        }
        return null;
    }

    public void shuffle() {
        Collections.shuffle(songs);
    }
}
