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

        // Print the local IP address for debugging
        System.out.println("Local IP Address: " + localIp);

        // Create an instance of WebSocketConnection
        WebSocketConnection webSocketConnection = new WebSocketConnection();

        // Start the WebSocket server on the retrieved IP address and port
        webSocketConnection.startServer(localIp, 8080);

        // Create an instance of MouseMovement
        MouseMovement mouseMovement;
        try {
            mouseMovement = new MouseMovement(webSocketConnection);
        } catch (AWTException e) {
            e.printStackTrace();
            return; // Exit if Robot initialization fails
        }

        // Simulate waiting for some time before checking the values (waiting for the client to send data)
        try {
            Thread.sleep(5000); // Wait for 5 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Move the mouse based on the x and y values
        mouseMovement.moveMouse(); // Move the mouse to the coordinates

        // Optionally, simulate a mouse click
        mouseMovement.clickMouse(); // You can call this if you want to simulate a click after moving
    }
}
