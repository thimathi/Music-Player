import java.util.LinkedList;
import java.util.Queue;

public class MusicQueue {
    private Queue<Song> queue;
    private Playlist playlist;

    public MusicQueue(Playlist playlist) {
        this.playlist = playlist;
        this.queue = new LinkedList<>(playlist.getSongs());
    }

    public Song getNextSong() {
        if (queue.isEmpty()) {
            queue.addAll(playlist.getSongs()); // Reset queue
        }
        return queue.poll();
    }
}
