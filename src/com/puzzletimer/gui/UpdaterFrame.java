package com.puzzletimer.gui;

import com.puzzletimer.state.UpdateManager;

import javax.print.URIException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public class UpdaterFrame {
    String updateUrl;
    JProgressBar progress;
    JLabel progressLabel;
    String version;

    public UpdaterFrame(String url, String version) {
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        this.version = version;
        updateUrl = url;
        frame.setPreferredSize(new Dimension(300, 80));
        frame.setSize(new Dimension(300, 80));
        frame.setTitle("Updater");
        progressLabel = new JLabel("Progress: ");
        //frame.add(progressLabel);
        progressLabel.setVisible(true);
        progress = new JProgressBar(0, 100);
        progress.setValue(0);
        progress.setStringPainted(true);
        progress.setVisible(true);
        frame.add(progress);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();
    }

    public void downloadLatestVersion() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    url = new URL(updateUrl);
                    HttpURLConnection hConnection = (HttpURLConnection) url
                            .openConnection();
                    HttpURLConnection.setFollowRedirects(true);
                    if (HttpURLConnection.HTTP_OK == hConnection.getResponseCode()) {
                        try (InputStream in = hConnection.getInputStream(); BufferedOutputStream out = new BufferedOutputStream(
                                new FileOutputStream(new File(UpdaterFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                                ));) {
                            int filesize = hConnection.getContentLength();
                            progress.setMaximum(filesize);
                            byte[] buffer = new byte[4096];
                            int numRead;
                            long numWritten = 0;
                            while ((numRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, numRead);
                                numWritten += numRead;
                                System.out.println((double) numWritten / (double) filesize);
                                progress.setValue((int) numWritten);
                            }
                            if (filesize != numWritten)
                                System.out.println("Wrote " + numWritten + " bytes, should have been " + filesize);
                            else
                                System.out.println("Downloaded successfully.");
                            out.close();
                            in.close();
                            try {
                                Process update = Runtime.getRuntime().exec("java -jar " + new File(UpdaterFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }
                    }

                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

}