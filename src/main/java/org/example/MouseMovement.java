package org.example;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseMovement {

    private Robot robot;
    private WebSocketConnection webSocketConnection;

    public MouseMovement(WebSocketConnection webSocketConnection) throws AWTException {
        this.robot = new Robot();
        this.webSocketConnection = webSocketConnection;
    }

    // Method to move the mouse based on the x and y values from the WebSocketConnection
    public void moveMouse() {
        // Get the x and y values from the WebSocketConnection
        double deltaX = webSocketConnection.getX();
        double deltaY = webSocketConnection.getY();

        try {
            // Get the current mouse location
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point currentLocation = pointerInfo.getLocation();
            int currentX = (int) currentLocation.getX();
            int currentY = (int) currentLocation.getY();

            // Calculate the new position by adding the deltas
            int newX = currentX + (int) deltaX;
            int newY = currentY + (int) deltaY;

            // Ensure the new position doesn't go off-screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            newX = Math.max(0, Math.min(screenSize.width - 1, newX));
            newY = Math.max(0, Math.min(screenSize.height - 1, newY));

            // Move the mouse to the new position
            robot.mouseMove(newX, newY);
            System.out.println("Mouse moved to: " + newX + ", " + newY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Optional: Method to simulate a mouse click at the new position
    public void clickMouse() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    // Example of moving and clicking the mouse in a loop
    public void moveAndClick() {
        while (true) {
            moveMouse();  // Move the mouse based on x and y
            clickMouse();  // Optionally simulate a click
            try {
                Thread.sleep(100);  // Adjust delay as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
