package org.example;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.InetAddress;

public class IpQrGenerator {
    private JFrame qrFrame;
    private WebSocketConnection webSocketConnection;

    public IpQrGenerator(WebSocketConnection webSocketConnection) {
        this.webSocketConnection = webSocketConnection;

        // Set a listener for connection status changes
        this.webSocketConnection.setConnectionStatusListener(this::onConnectionStatusChanged);

        // Initially display the QR code if not connected
        if (!webSocketConnection.isConnected()) {
            generateAndDisplayIpQr();
        }
    }

    public void generateAndDisplayIpQr() {
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            BufferedImage qrCodeImage = generateQrCode(ipAddress);
            displayQrCode(qrCodeImage, "Device's IP address: " + ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onConnectionStatusChanged(boolean isConnected) {
        if (isConnected) {
            closeQrCodeWindow();
        } else {
            generateAndDisplayIpQr();
        }
    }

    private BufferedImage generateQrCode(String text) throws WriterException {
        int size = 300;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void displayQrCode(BufferedImage qrCodeImage, String title) {
        if (qrFrame == null || !qrFrame.isVisible()) {
            qrFrame = new JFrame(title);
            qrFrame.setSize(400, 400);
            qrFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel qrLabel = new JLabel(new ImageIcon(qrCodeImage));
            qrFrame.add(qrLabel);
            qrFrame.setLocationRelativeTo(null);
            qrFrame.setVisible(true);
        }
    }

    private void closeQrCodeWindow() {
        if (qrFrame != null) {
            qrFrame.dispose();
            qrFrame = null;
        }
    }
}
