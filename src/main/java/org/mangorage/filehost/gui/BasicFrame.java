package org.mangorage.filehost.gui;

import org.mangorage.filehost.core.VideoProcessor;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class BasicFrame extends JPanel {
    public static final BasicFrame INSTANCE = new BasicFrame();
    public final Queue<byte[]> frameDataQueue = new LinkedList<>();
    public final Queue<short[]> audioDataQueue = new LinkedList<>();

    public final Queue<byte[]> frameDataQueue2 = new LinkedList<>();

    private static SourceDataLine sourceDataLine;

    public static void openAudioLine(AudioFormat audioFormat) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
    }

    public static void closeAudioLine() {
        sourceDataLine.drain();
        sourceDataLine.close();
    }

    public static void playSound(byte[] audioData) {
        sourceDataLine.write(audioData, 0, audioData.length);
    }


    public BasicFrame() {
        super();
        try {
            openAudioLine(new AudioFormat(44100, 16, 2, true, false));
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        Timer timer = new Timer(1000 / 30, e -> {
            repaint();
        });
        timer.setRepeats(true);
        timer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        if (frameDataQueue.size() > 5) {
            byte[] imageData = frameDataQueue.poll();

            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                BufferedImage image = ImageIO.read(bis);
                g.drawImage(image, 0, 0, 1280, 720, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawString("Showing Incoming Video Stream", 10, 10);
        }

    }
}
