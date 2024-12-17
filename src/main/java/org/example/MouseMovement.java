package org.example;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseMovement {
    private Robot robot;
    private boolean isDragging = false;
    private long lastTapTime = 0;
    private static final long DOUBLE_CLICK_TIME = 500; // milliseconds
    private static final double VELOCITY_THRESHOLD = 0.5; // Threshold for drag detection
    private static final double SCROLL_THRESHOLD = 1.0; // Threshold for scroll detection

    public MouseMovement() throws AWTException {
        this.robot = new Robot();
    }

    public void moveMotionMouse(double x, double y, double z) {
        // Use motion data to move the mouse
        Point location = MouseInfo.getPointerInfo().getLocation();
        int newX = (int) (location.getX() + (x * 5)); // Adjust multiplier as needed
        int newY = (int) (location.getY() + (y * 5));
        robot.mouseMove(newX, newY);
    }

    public void moveTouchMouse(double stateID, double moveX, double moveY, double x0, double y0, 
                             double dx, double dy, double vx, double vy) {
        // Get current mouse position
        Point location = MouseInfo.getPointerInfo().getLocation();

        // Move mouse based on dx and dy
        int newX = (int) (location.getX() + dx);
        int newY = (int) (location.getY() + dy);
        robot.mouseMove(newX, newY);

        // Handle different gestures based on velocity and movement
        if (Math.abs(vx) < VELOCITY_THRESHOLD && Math.abs(vy) < VELOCITY_THRESHOLD) {
            // Low velocity might indicate a tap or release of drag
            if (isDragging) {
                // End drag operation
                isDragging = false;
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else if (dx == 0 && dy == 0) {
                // Handle tap/click
                handleClick();
            }
        } else if (Math.abs(vy) > SCROLL_THRESHOLD && Math.abs(vx) < VELOCITY_THRESHOLD) {
            // Vertical movement with high velocity - handle as scroll
            int scrollAmount = (int) Math.signum(vy);
            robot.mouseWheel(scrollAmount);
        } else {
            // Handle drag operation
            if (!isDragging) {
                isDragging = true;
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            }
        }
    }

    private void handleClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTapTime < DOUBLE_CLICK_TIME) {
            // Double click
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            lastTapTime = 0; // Reset tap time
        } else {
            // Single click
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            lastTapTime = currentTime;
        }
    }
}
