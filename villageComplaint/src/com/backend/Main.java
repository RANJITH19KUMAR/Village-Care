package com.backend;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) {

        try {

            int port = Integer.parseInt(
                    System.getenv().getOrDefault("PORT", "8080")
            );

            HttpServer server = HttpServer.create(
                    new InetSocketAddress("0.0.0.0", port),
                    0
            );

            server.createContext("/", new complaintHandler());

            server.start();

            System.out.println(
                    "VillageCare Server Started on port " + port
            );

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}