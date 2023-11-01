package org.mangorage.filehost.core;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.mangorage.filehost.gui.BasicFrame;
import org.mangorage.filehost.networking.packets.AudioFramePacket;
import org.mangorage.filehost.networking.packets.VideoFramePacket;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;

// Handle sending video to client...
public class VideoProcessor {
    public record Result(byte[] data, float quality) {}
    public static byte[] imageToByteArray(Image image, String format, float compressionQuality) throws Exception {
        // Convert the Image to a BufferedImage
        BufferedImage bufferedImage = toBufferedImage(image);

        // Create a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Write the BufferedImage to the ByteArrayOutputStream with specified compression quality
        ImageIO.write(bufferedImage, format, baos);

        // Get the resulting byte array
        byte[] imageBytes = baos.toByteArray();

        return imageBytes;
    }

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }


    public static byte[] getCompressedData(RenderedImage originalImage, float compressionQuality) throws Exception {
        // Set compression quality (adjust as needed)
        // Create a ByteArrayOutputStream to store the compressed image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create an ImageOutputStream from the ByteArrayOutputStream
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);

        // Create an ImageWriter
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

        // Set the compression quality
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compressionQuality);

        // Write the image with compression to the ByteArrayOutputStream
        writer.setOutput(ios);
        writer.write(null, new IIOImage(originalImage, null, null), param);
        writer.dispose();
        ios.close();

        // The compressed image is now in the ByteArrayOutputStream

        return baos.toByteArray();
    }

    public static byte[] toByteArray(short[] audioData) {
        byte[] audioBytes = new byte[audioData.length * 2];
        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData);
        return audioBytes;
    }


    public static void processWithAudio(String video, Consumer<VideoFramePacket> videoFramePacketConsumer, Consumer<AudioFramePacket> audioFramePacketConsumer) {
        // Replace with the path to your video file
        // Replace with the path to your video file

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video);
        try {
            grabber.start();

            Frame frame;
            Java2DFrameConverter frameConverter = new Java2DFrameConverter();


            while ((frame = grabber.grabFrame()) != null) {
                byte[] image = null;
                short[] audio = null;
                if (frame.image != null) {
                    // Process the video frame as needed
                    BufferedImage bufferedImage = frameConverter.convert(frame);

                    image = getCompressedData(bufferedImage, 0.005f);
                }
                if (frame.samples != null) {
                    // Process the audio frame as needed
                    Buffer[] audioBuffers = frame.samples;
                    for (Buffer audioBuffer : audioBuffers) {
                        ShortBuffer shortBuffer = (ShortBuffer) audioBuffer;
                        short[] array = new short[shortBuffer.capacity()];
                        shortBuffer.get(array);
                        audio = array;
                    }
                }

                if (image != null) {
                    //System.out.println("Sending image: %s".formatted(image.length));
                    //videoFramePacketConsumer.accept(new VideoFramePacket(image));
                }

                if (audio != null) {
                    System.out.println("Sending audio: %s".formatted(audio.length));
                    audioFramePacketConsumer.accept(new AudioFramePacket(toByteArray(audio)));
                }

                video = null;
                audio = null;
            }

            // Now you have audioOutputStream containing the audio data as a byte array
            // And you can access the imageBytes for video frames.

            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
