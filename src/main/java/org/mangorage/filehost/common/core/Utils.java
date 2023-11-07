package org.mangorage.filehost.common.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Utils {
    public static byte[] loadClassBytes(Class<?> clazz) {
        String classFilePath = clazz.getName().replace(".", "/") + ".class";
        try (InputStream is = Objects.requireNonNull(Utils.class.getClassLoader().getResourceAsStream(classFilePath))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToHexString(byte b) {
        return String.format("%02X", b);
    }

    public static byte stringToByte(String hex) {
        return (byte) Integer.parseInt(hex, 16);
    }

    public static String bytesToString(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte aByte : bytes)
            result.append(byteToHexString(aByte));


        return result.toString();
    }

    public static byte[] stringToBytes(String string) {
        byte[] bytes = new byte[string.length() / 2];
        String[] stringBytes = string.split("(?<=\\G..)");
        int count = 0;
        for (String stringByte : stringBytes)
            bytes[count++] = stringToByte(stringByte);
        return bytes;
    }

}
