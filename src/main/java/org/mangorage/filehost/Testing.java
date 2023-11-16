package org.mangorage.filehost;

public class Testing {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            try {
                System.out.println("Thread Started");
                Thread.sleep(10000);
                System.out.println("Hello!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        System.out.println("End of Main");
    }
}
