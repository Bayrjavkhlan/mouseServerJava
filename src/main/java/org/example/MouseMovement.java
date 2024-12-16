package org.example;

import java.awt.*;

public class MouseMovement {
    private Robot robot;

    public MouseMovement(WebSocketConnection webSocketConnection) throws AWTException {
        this.robot = new Robot();
    }

    public void moveMouse(double x, double y) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        int newX = (int) (location.getX() + x);
        int newY = (int) (location.getY() + y);
        robot.mouseMove(newX, newY);
    }
}
