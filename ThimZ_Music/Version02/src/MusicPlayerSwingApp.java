import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayerSwingApp extends JFrame {

    private Playlist playlist;
    private MusicQueue musicQueue;
    private JList<String> playlistView;
    private JProgressBar songProgressBar;
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    private JButton playButton, nextButton, stopButton, shuffleButton, addButton;
    private JLabel nowPlayingLabel;
    private JLabel albumArtLabel; // To display album art
    private MP3Player mp3Player;
    private Thread playThread;  // To manage playback in a separate thread
    private String currentSongPath;  // To store the current song path for album art

    public MusicPlayerSwingApp() {
        setTitle("Music Player");
        setSize(800, 400);  // Increased width to accommodate album art
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        playlist = new Playlist();
        musicQueue = new MusicQueue(playlist);

        // Initialize UI components
        playlistView = new JList<>(new DefaultListModel<>());
        JScrollPane playlistScroll = new JScrollPane(playlistView);
        nowPlayingLabel = new JLabel("Now Playing: None");
        songProgressBar = new JProgressBar(0, 100);
        volumeSlider = new JSlider(0, 100, 50);
        volumeLabel = new JLabel("Volume: 50");  // Display current volume level

        // Album art placeholder
        albumArtLabel = new JLabel("Album Art", SwingConstants.CENTER);
        albumArtLabel.setPreferredSize(new Dimension(200, 200));
        albumArtLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Initialize buttons
        playButton = new JButton("Play");
        nextButton = new JButton("Next");
        stopButton = new JButton("Stop");
        shuffleButton = new JButton("Shuffle");
        addButton = new JButton("Add Songs");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(shuffleButton);
        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel("Volume:"));
        buttonPanel.add(volumeSlider);
        buttonPanel.add(volumeLabel);  // Added volume display

        add(playlistScroll, BorderLayout.CENTER);
        add(nowPlayingLabel, BorderLayout.NORTH);
        add(songProgressBar, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);
        add(albumArtLabel, BorderLayout.EAST);  // Added album art label

        // Add button listeners
        addButton.addActionListener(e -> addSongs());
        playButton.addActionListener(e -> playSelectedSong());
        stopButton.addActionListener(e -> stopPlaying());
        nextButton.addActionListener(e -> playNextSong());
        shuffleButton.addActionListener(e -> shufflePlaylist());

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                adjustVolume(volumeSlider.getValue());
            }
        });

        setVisible(true);
    }

    private void addSongs() {
        List<Song> songs = FileHandler.loadSongs();
        playlist.addSongs(songs);
        updatePlaylistView();
    }

    private void updatePlaylistView() {
        DefaultListModel<String> listModel = (DefaultListModel<String>) playlistView.getModel();
        listModel.clear();
        for (Song song : playlist.getSongs()) {
            listModel.addElement(song.getTitle());
        }
    }

    private void playSelectedSong() {
        stopPlaying();  // Stop any previous song
        int selectedIndex = playlistView.getSelectedIndex();
        if (selectedIndex >= 0) {
            Song selectedSong = playlist.getSong(selectedIndex);
            currentSongPath = selectedSong.getFilePath(); // Save current song path for album art
            mp3Player = new MP3Player(selectedSong);
            nowPlayingLabel.setText("Now Playing: " + selectedSong.getTitle());
            loadAlbumArt(currentSongPath);  // Load album art

            // Start the playback in a new thread
            playThread = new Thread(() -> mp3Player.play());
            playThread.start();

            startProgressBar();
        }
    }

    private void stopPlaying() {
        if (mp3Player != null) {
            mp3Player.stop();
        }
        stopProgressBar();
    }

    private void playNextSong() {
        stopPlaying();
        Song nextSong = musicQueue.getNextSong();
        if (nextSong != null) {
            playlistView.setSelectedIndex(playlist.getSongs().indexOf(nextSong));
            playSelectedSong();
        }
    }

    private void shufflePlaylist() {
        playlist.shuffle();
        updatePlaylistView();
    }

    private void adjustVolume(int volume) {
        if (mp3Player != null) {
            mp3Player.setVolume(volume);
        }
        volumeLabel.setText("Volume: " + volume);  // Update volume label
    }

    private void startProgressBar() {
        songProgressBar.setValue(0);
        Timer progressTimer = new Timer(1000, e -> {
            int value = songProgressBar.getValue();
            if (value < 100) {
                songProgressBar.setValue(value + 1);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        progressTimer.start();
    }

    private void stopProgressBar() {
        songProgressBar.setValue(0);
    }

    private void loadAlbumArt(String filePath) {
        // Load the album art based on the MP3 file name
        String albumArtPath = filePath.replace(".mp3", ".jpg");
        File albumArtFile = new File(albumArtPath);
        if (albumArtFile.exists()) {
            try {
                Image albumArt = ImageIO.read(albumArtFile).getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                albumArtLabel.setIcon(new ImageIcon(albumArt));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            albumArtLabel.setText("No Album Art");
            albumArtLabel.setIcon(null);
        }
    }

    // MP3 Player inner class
    class MP3Player {
        private AdvancedPlayer player;
        private String filePath;
        private FileInputStream fis;

        public MP3Player(Song song) {
            this.filePath = song.getFilePath();
        }

        public void play() {
            try {
                fis = new FileInputStream(filePath);
                player = new AdvancedPlayer(fis);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            if (player != null) {
                player.close();  // Close the player
            }
        }

        public void setVolume(int volume) {
            // Placeholder for real-time volume control; you may need another library for better control
            System.out.println("Volume set to: " + volume);
        }
    }


}
