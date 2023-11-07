package org.mangorage.filehost.client.gui;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaControl extends HBox {
    private final MediaPlayer mp;
    private final VideoPlayer player;


    public MediaControl(final MediaPlayer mp, final VideoPlayer player) {
        this.mp = mp;
        this.player = player;
        setStyle("-fx-background-color: #000000;"); // Set background color to black

        Button playButton = new Button(">");
        playButton.setOnAction(e -> {
            MediaPlayer.Status status = mp.getStatus();
            System.out.println(status);
            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                VideoPlayer.changeVideo(player, player.getVideo());
                return;
            }
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
                mp.play();
            } else {
                mp.pause();
            }
        });

        Button rewindButton = new Button("<<");
        rewindButton.setOnAction(e -> {
            mp.seek(mp.getCurrentTime().subtract(Duration.seconds(10)));
        });

        Button fastForwardButton = new Button(">>");
        fastForwardButton.setOnAction(e -> {
            mp.seek(mp.getCurrentTime().add(Duration.seconds(10)));
        });

        Button replayButton = new Button("Fix/Replay"); // Add a "Replay" button
        // Add an additional action to force playing the video
        replayButton.setOnAction(e -> {
            MediaPlayer.Status status = mp.getStatus();
            System.out.println(status);
            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                VideoPlayer.changeVideo(player, player.getVideo());
            } else {
                mp.stop();
                mp.play();
            }
        });

        Slider volumeSlider = new Slider(0, 1, 1); // Volume slider
        volumeSlider.valueProperty().bindBidirectional(mp.volumeProperty());

        getChildren().addAll(rewindButton, playButton, fastForwardButton, volumeSlider, replayButton);
    }
}
