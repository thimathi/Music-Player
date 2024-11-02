import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Method to load songs from files using JFileChooser
    public static List<Song> loadSongs() {
        List<Song> songs = new ArrayList<>();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);  // Allow selecting multiple files
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                Song song = new Song(file.getName(), file.getAbsolutePath());
                songs.add(song);
            }
        }
        return songs;
    }
}
