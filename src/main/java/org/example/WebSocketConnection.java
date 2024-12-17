package org.example;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class WebSocketConnection {
    private WebSocketServer motionServer;
    private WebSocketServer touchServer;
    private MouseMovement mouseMovement;
    private Consumer<Boolean> connectionStatusListener;
    private String ipAddress;
    private int port;

    // Data fields for motion
    private double motionX, motionY, motionZ;

    // Data fields for touch
    private double stateID, moveX, moveY, x0, y0, dx, dy, vx, vy;
    private int numberActiveTouches;

    private boolean motionMessageReceived = false;
    private boolean touchMessageReceived = false;

    public WebSocketConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setMouseMovement(MouseMovement mouseMovement) {
        this.mouseMovement = mouseMovement;
    }

    public void startServer() {
        motionServer = new WebSocketServer(new InetSocketAddress(ipAddress, port)) {
            @Override
            public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
                ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
                String path = request.getResourceDescriptor();
                if (!"/motion".equals(path) && !"/touch".equals(path)) {
                    return null;
                }
                return builder;
            }

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                String path = conn.getResourceDescriptor();
                System.out.println("New " + path + " connection: " + conn.getRemoteSocketAddress());
                if ("/motion".equals(path)) {
                    notifyConnectionStatus(true);
                }
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                String path = conn.getResourceDescriptor();
                System.out.println("Closed " + path + " connection: " + conn.getRemoteSocketAddress());
                if ("/motion".equals(path) && !hasMotionConnections()) {
                    handleDisconnection();
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                String path = conn.getResourceDescriptor();
                System.out.println(path + " message from client: " + message);

                if ("/motion".equals(path)) {
                    handleMotionMessage(message);
                } else if ("/touch".equals(path)) {
                    handleTouchMessage(message);
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("WebSocket server started successfully");
            }
        };

        touchServer = new WebSocketServer(new InetSocketAddress(ipAddress, port + 2)) {
            @Override
            public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
                ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
                String path = request.getResourceDescriptor();
                if (!"/touch".equals(path)) {
                    return null;
                }
                return builder;
            }

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                String path = conn.getResourceDescriptor();
                System.out.println("New " + path + " connection: " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                String path = conn.getResourceDescriptor();
                System.out.println("Closed " + path + " connection: " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                String path = conn.getResourceDescriptor();
                String unescapedMessage = message.substring(1, message.length() - 1).replace("\\\"", "\"");

                System.out.println(path + " message from client: " + unescapedMessage);

                if ("/touch".equals(path)) {
                    handleTouchMessage(unescapedMessage);
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("Touch WebSocket server started successfully");
            }
        };

        motionServer.start();
        touchServer.start();
        System.out.println("WebSocket servers started on:");
        System.out.println("Motion: ws://" + ipAddress + ":" + port + "/motion");
        System.out.println("Touch: ws://" + ipAddress + ":" + (port + 2) + "/touch");
    }

    private void handleMotionMessage(String message) {
        try {
            String cleanJson = message.substring(1, message.length() - 1).replace("\\\"", "\"");
            JSONObject json = new JSONObject(cleanJson);
            motionX = json.getDouble("x");
            motionY = json.getDouble("y");
            motionZ = json.getDouble("z");
            motionMessageReceived = true;
            touchMessageReceived = false;
        } catch (Exception e) {
            System.err.println("Error processing motion message: " + e.getMessage());
        }
    }

    private void handleTouchMessage(String message) {
        try {
            String cleanJson = message.substring(1, message.length() - 1).replace("\\\"", "\"");
            JSONObject json = new JSONObject(cleanJson);
            stateID = json.getDouble("stateID");
            moveX = json.getDouble("moveX");
            moveY = json.getDouble("moveY");
            x0 = json.getDouble("x0");
            y0 = json.getDouble("y0");
            dx = json.getDouble("dx");
            dy = json.getDouble("dy");
            vx = json.getDouble("vx");
            vy = json.getDouble("vy");
            numberActiveTouches = json.getInt("numberActiveTouches");
            touchMessageReceived = true;
            motionMessageReceived = false;
        } catch (Exception e) {
            System.err.println("Error processing touch message: " + e.getMessage());
        }
    }

    private boolean hasMotionConnections() {
        return motionServer.getConnections().stream()
                .anyMatch(conn -> "/motion".equals(conn.getResourceDescriptor()));
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

    public void stopServer() throws InterruptedException {
        if (motionServer != null) {
            motionServer.stop();
            System.out.println("Motion WebSocket server stopped");
        }
        if (touchServer != null) {
            touchServer.stop();
            System.out.println("Touch WebSocket server stopped");
        }
    }

    public boolean isConnected() {
        return motionServer != null && hasMotionConnections();
    }

    public void setConnectionStatusListener(Consumer<Boolean> listener) {
        this.connectionStatusListener = listener;
    }

    private void notifyConnectionStatus(boolean isConnected) {
        if (connectionStatusListener != null) {
            connectionStatusListener.accept(isConnected);
        }
    }

    // Getter methods for motion data
    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    // Getter methods for touch data
    public double getStateID() {
        return stateID;
    }

    public double getMoveX() {
        return moveX;
    }

    public double getMoveY() {
        return moveY;
    }

    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public int getNumberActiveTouches() {
        return numberActiveTouches;
    }

    public boolean isMotionMessageReceived() {
        boolean wasReceived = motionMessageReceived;
        motionMessageReceived = false;
        return wasReceived;
    }

    public boolean isTouchMessageReceived() {
        boolean wasReceived = touchMessageReceived;
        touchMessageReceived = false;
        return wasReceived;
    }

    @Deprecated
    public boolean isMessageReceived() {
        return isMotionMessageReceived() || isTouchMessageReceived();
    }
}
