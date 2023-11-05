package org.mangorage.filehost.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Constants {
    public static final int PORT = 25565;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public record Config(int packetRate, String password) {}
    public static final Config config;

    static {
        Path cfg = Path.of("config.json");
        if (!cfg.toFile().exists()) {
            config = new Config(50,"12345!");
            var json = GSON.toJson(config);
            try {
                Files.writeString(cfg, json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                String json = Files.readString(cfg);
                config = GSON.fromJson(json, Config.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void init() {

    }
}
