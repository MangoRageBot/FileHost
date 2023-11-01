package org.mangorage.filehost;

import org.mangorage.filehost.core.VideoProcessor;
import org.mangorage.filehost.gui.Window;

import java.net.SocketException;

public class zMain {
    public static void main(String[] args) throws SocketException {
        Window.create();
        VideoProcessor.processWithAudio("video4.mp4", a -> {}, b -> {});
    }
}
