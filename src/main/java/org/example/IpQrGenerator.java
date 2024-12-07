package org.example;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class IpQrGenerator {

    private JFrame qrFrame;  // To hold the QR code window

    // Public method to generate and display the QR code
    public void generateAndDisplayIpQr() {
        try {
            // Get local IP address
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            System.out.println("Your IP address: " + ipAddress);

            // Generate QR code for the IP address
            BufferedImage qrCodeImage = generateQrCode(ipAddress);

            // Display the QR code
            displayQrCode(qrCodeImage, "QR Code for IP: " + ipAddress);

            // Simulate waiting for a successful connection
            while (!isConnected()) {
                // Wait for a connection to be established (could replace with actual check)
                Thread.sleep(1000);
            }

            // Once connected, close the QR window
            closeQrCodeWindow();

        } catch (UnknownHostException e) {
            System.err.println("Unable to determine IP address: " + e.getMessage());
        } catch (WriterException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private BufferedImage generateQrCode(String text) throws WriterException {
        int size = 300;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void displayQrCode(BufferedImage qrCodeImage, String title) {
        // Create a JFrame to display the QR code
        qrFrame = new JFrame(title);
        qrFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        qrFrame.setSize(400, 400);

        // Center the QR code image on the screen
        qrFrame.setLocationRelativeTo(null);

        // Add the QR code to the frame
        JLabel qrLabel = new JLabel(new ImageIcon(qrCodeImage));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setVerticalAlignment(SwingConstants.CENTER);
        qrFrame.add(qrLabel);

        // Display the frame
        qrFrame.setVisible(true);
    }

    private void closeQrCodeWindow() {
        // Close the QR code window once the connection is established
        if (qrFrame != null) {
            qrFrame.dispose();
        }
    }

    // Simulate a connection check (replace this with actual connection logic)
    private boolean isConnected() {
        // For example, you can use a WebSocket client, server ping, etc.
        // Here we simulate a connection after 5 seconds
        return false;  // Replace with actual connection status logic
    }
}