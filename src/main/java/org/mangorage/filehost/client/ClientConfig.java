package org.mangorage.filehost.client;

import org.mangorage.filehost.misc.DataHandler;

public record ClientConfig(String serverIP, String port, String serverPassword, String username) {
    public static final DataHandler<ClientConfig> CONFIG = DataHandler.create(
            a -> {},
            ClientConfig.class,
            "data",
            DataHandler.Properties.create()
                    .setFileName("clientConfig.json")
                    .useDefaultFileNamePredicate()
    );

    public static void main(String[] args) {
        var a = CONFIG.loadOrCreate(() -> new ClientConfig("a", "a", "a", "a"));
        System.out.println(a.port);
    }
}
