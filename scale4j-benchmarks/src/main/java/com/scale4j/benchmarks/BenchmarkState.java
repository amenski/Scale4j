package com.scale4j.benchmarks;

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@State(Scope.Thread)
public class BenchmarkState {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

    public BufferedImage sourceImage;

    public static final int[] TARGET_WIDTHS = {100, 300, 800, 1600};
    public static final ResizeQuality[] QUALITIES = {
        ResizeQuality.LOW, ResizeQuality.MEDIUM, ResizeQuality.HIGH
    };

    public static final ResizeMode DEFAULT_MODE = ResizeMode.FIT;

    public ExecutorService virtualThreadExecutor;

    @Setup
    public void setup() {
        String[] parts = imageSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = createTestImage(width, height);

        try {
            virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        } catch (Exception e) {
            virtualThreadExecutor = Executors.newCachedThreadPool();
        }
    }

    @TearDown
    public void tearDown() {
        if (virtualThreadExecutor != null) {
            virtualThreadExecutor.shutdown();
        }
    }

    private BufferedImage createTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(new Color(100, 150, 200));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = 20 + (int) (Math.random() * 80);
            g2d.fillOval(x, y, size, size);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Test Image", width / 4, height / 2);

        g2d.dispose();
        return image;
    }
}
