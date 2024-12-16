package org.example;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class WebSocketConnection {

    private WebSocketServer server;
    private double x, y, z;  // Store the received motion values
    private Consumer<Boolean> connectionStatusListener;
    private String ipAddress;
    private int port;

    public WebSocketConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    // Set a listener for connection status changes
    public void setConnectionStatusListener(Consumer<Boolean> listener) {
        this.connectionStatusListener = listener;
    }

    // Notify connection status
    private void notifyConnectionStatus(boolean isConnected) {
        if (connectionStatusListener != null) {
            connectionStatusListener.accept(isConnected);
        }
    }

    public void startServer() {
        server = new WebSocketServer(new InetSocketAddress(ipAddress, port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("New connection: " + conn.getRemoteSocketAddress());
                notifyConnectionStatus(true);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
                if (server.getConnections().isEmpty()) {
                    handleDisconnection();
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("Message from client: " + message);
                try {
                    JSONObject json = new JSONObject(message);
                    String type = json.getString("type");

                    if ("motion".equals(type)) {
                        JSONObject data = json.getJSONObject("data");
                        x = data.getDouble("x");
                        y = data.getDouble("y");
                        z = data.getDouble("z");
                        processMotionData(x, y, z);
                    } else if ("restart".equals(type)) {
                        System.out.println("Restart command received.");
                        handleDisconnection();
                    }
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
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

        server.start();
        System.out.println("WebSocket server started on ws://" + ipAddress + ":" + port);
    }

    private void handleDisconnection() {
        notifyConnectionStatus(false);
        restartServer();
    }

    private void restartServer() {
        try {
            stopServer();
            startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processMotionData(double x, double y, double z) {
        System.out.println("Received motion data: X=" + x + ", Y=" + y + ", Z=" + z);
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            server.stop();
            System.out.println("WebSocket server stopped");
        }
    }

    public boolean isConnected() {
        return server != null && !server.getConnections().isEmpty();
    }

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
