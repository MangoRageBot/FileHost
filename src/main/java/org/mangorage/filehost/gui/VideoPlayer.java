package org.mangorage.filehost.gui;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import javafx.scene.layout.VBox;

public class VideoPlayer extends JFXPanel {
    private MediaPlayer mediaPlayer;
    private String video;

    public VideoPlayer(String video) {
        this.video = video;
    }

    public String getVideo() {
        return video;
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(500);
            play("video2.mp4");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void play(String video) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting Video");
            JFrame frame = new JFrame("Swing Video Player");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280 + 16, 720 + 80);
            frame.getContentPane().setBackground(Color.BLACK);

            VideoPlayer fxPanel = new VideoPlayer(video);
            fxPanel.setBackground(Color.BLACK);
            fxPanel.repaint();

            frame.add(fxPanel);

            frame.setVisible(true);
            changeVideo(fxPanel, video);
        });
    }

    public static void changeVideo(VideoPlayer videoPlayer, String video) {
        Platform.runLater(() -> {
            try {
                if (videoPlayer.mediaPlayer != null) {
                    videoPlayer.mediaPlayer.stop();
                }

                System.out.println("Loading FX");
                initFX(videoPlayer, video);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void initFX(VideoPlayer videoPlayer, String video) throws MalformedURLException {
        File videoFile = new File(video); // Replace with your video file path
        String videoFilePath = videoFile.toURI().toURL().toExternalForm(); // Convert to URL

        Media media = new Media(videoFilePath);
        videoPlayer.mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(videoPlayer.mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);

        MediaControl mediaControl = new MediaControl(videoPlayer.mediaPlayer, videoPlayer); // Create a MediaControl instance
        mediaControl.setMinSize(1280, 720); // Set the size of the media control

        VBox container = new VBox(); // Create a container for the media view and controls
        container.getChildren().addAll(mediaView, mediaControl);

        Scene scene = new Scene(container, 1280, 720);
        videoPlayer.setScene(scene);

        videoPlayer.mediaPlayer.play();
        System.out.println("Video Started");
    }
}
