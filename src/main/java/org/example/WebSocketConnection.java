package org.example;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class WebSocketConnection {

    private WebSocketServer server;
    private double x, y, z;  // Store the received values

    // Start the WebSocket server and handle connections
    public void startServer(String ipAddress, int port) {
        server = new WebSocketServer(new InetSocketAddress(ipAddress, port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("New connection: " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("Message from client: " + message);

                try {
                    // Parse the incoming message as a JSON object
                    JSONObject json = new JSONObject(message);

                    // Extract the x, y, and z values from the JSON object
                    x = json.getDouble("x");
                    y = json.getDouble("y");
                    z = json.getDouble("z");

                    // Process the received values
                    System.out.println("Received values: X=" + x + ", Y=" + y + ", Z=" + z);

                    // Send back the received values as a response
//                    String response = "Processed values: X=" + x + ", Y=" + y + ", Z=" + z;
//                    conn.send(response);  // Send response back to the client
                } catch (Exception e) {
                    e.printStackTrace();
                    conn.send("Error processing message.");
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("Server started successfully");
            }
        };

        // Start the server
        server.start();
        System.out.println("WebSocket server started on ws://" + ipAddress + ":" + port);
    }

    // Stop the WebSocket server
//    public void stopServer() {
//        if (server != null) {
//            server.stop();
//            System.out.println("WebSocket server stopped");
//        }
//    }

    // Check if the server has active connections
    public boolean isConnected() {
        return server != null && server.getConnections().size() > 0;
    }

    // Getter methods to return the x, y, and z values
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
