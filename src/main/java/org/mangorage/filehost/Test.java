package org.mangorage.filehost;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress("localhost", 1212);
        InetSocketAddress address1 = new InetSocketAddress("localhost", 1212);

        List<InetSocketAddress> LIST = List.of(address);
        System.out.println(address.equals(address1));
        System.out.println(LIST.contains(address1));
    }
}
