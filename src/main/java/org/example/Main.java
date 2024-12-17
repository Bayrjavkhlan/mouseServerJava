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
            mouseMovement = new MouseMovement();
            webSocketConnection.setMouseMovement(mouseMovement);
        } catch (AWTException e) {
            e.printStackTrace();
            return; // Exit if Robot initialization fails
        }

        // Keep the application running
        while (true) {
            try {
                if (webSocketConnection.isMotionMessageReceived()) {
                    mouseMovement.moveMotionMouse(
                        webSocketConnection.getMotionX(),
                        webSocketConnection.getMotionY(),
                        webSocketConnection.getMotionZ()
                    );
                }
                
                if (webSocketConnection.isTouchMessageReceived()) {
                    mouseMovement.moveTouchMouse(
                        webSocketConnection.getStateID(),
                        webSocketConnection.getMoveX(),
                        webSocketConnection.getMoveY(),
                        webSocketConnection.getX0(),
                        webSocketConnection.getY0(),
                        webSocketConnection.getDx(),
                        webSocketConnection.getDy(),
                        webSocketConnection.getVx(),
                        webSocketConnection.getVy()
                    );
                }
                
                Thread.sleep(100); // Prevent high CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
