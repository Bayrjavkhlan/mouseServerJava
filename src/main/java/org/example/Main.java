package org.example;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        // Get the local IP address
        String localIp;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return; // Exit if unable to get the IP address
        }

        // Start WebSocket Server
        WebSocketConnection webSocketConnection = new WebSocketConnection(localIp, 8080);
        webSocketConnection.startServer();

        // Generate and display QR Code
        IpQrGenerator ipQrGenerator = new IpQrGenerator(webSocketConnection);
        ipQrGenerator.generateAndDisplayIpQr();

        // Create Mouse Movement instance
        MouseMovement mouseMovement;
        try {
            mouseMovement = new MouseMovement(webSocketConnection);
        } catch (AWTException e) {
            e.printStackTrace();
            return; // Exit if Robot initialization fails
        }

        // Process WebSocket data for mouse movement
        while (true) {
            mouseMovement.moveMouse(webSocketConnection.getX(), webSocketConnection.getY());
            try {
                Thread.sleep(100); // Adjust delay as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
