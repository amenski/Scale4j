package com.scale4j.benchmarks;

import com.scale4j.ops.RotateOperation;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RotateBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

    @Param({"90", "180", "270", "45"})
    public int degrees;

    public BufferedImage sourceImage;

    @Setup
    public void setup() {
        String[] parts = imageSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = createTestImage(width, height);
    }

    private BufferedImage createTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(100, 150, 200));
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    @Benchmark
    public BufferedImage rotateDefault() {
        return RotateOperation.rotate(sourceImage, degrees);
    }

    @Benchmark
    public BufferedImage rotateWhiteBackground() {
        return RotateOperation.rotate(sourceImage, degrees, Color.WHITE);
    }

    @Benchmark
    public BufferedImage rotateTransparentBackground() {
        return RotateOperation.rotate(sourceImage, degrees, new Color(0, 0, 0, 0));
    }
}
