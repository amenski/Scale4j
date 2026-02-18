package com.scale4j.benchmarks;

import com.scale4j.ops.ResizeOperation;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.openjdk.jmh.annotations.*;

import java.awt.image.BufferedImage;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ResizeBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String sourceSize;

    @Param({"100", "300", "800", "1600"})
    public int targetWidth;

    @Param({"LOW", "MEDIUM", "HIGH"})
    public String qualityStr;

    public BufferedImage sourceImage;
    public ResizeQuality quality;
    public static final ResizeMode MODE = ResizeMode.FIT;

    @Setup
    public void setup() {
        String[] parts = sourceSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = createTestImage(width, height);
        quality = ResizeQuality.valueOf(qualityStr);
    }

    private BufferedImage createTestImage(int width, int height) {
        java.awt.Graphics2D g2d = null;
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2d = image.createGraphics();
            g2d.setColor(new java.awt.Color(100, 150, 200));
            g2d.fillRect(0, 0, width, height);
            return image;
        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
        }
    }

    @Benchmark
    public BufferedImage resizeExact() {
        int targetHeight = (int) ((double) sourceImage.getHeight() * targetWidth / sourceImage.getWidth());
        return ResizeOperation.resize(sourceImage, targetWidth, targetHeight, ResizeMode.EXACT, quality);
    }

    @Benchmark
    public BufferedImage resizeFit() {
        int targetHeight = (int) ((double) sourceImage.getHeight() * targetWidth / sourceImage.getWidth());
        return ResizeOperation.resize(sourceImage, targetWidth, targetHeight, MODE, quality);
    }

    @Benchmark
    public BufferedImage resizeFill() {
        int targetHeight = (int) ((double) sourceImage.getHeight() * targetWidth / sourceImage.getWidth());
        return ResizeOperation.resize(sourceImage, targetWidth, targetHeight, ResizeMode.FILL, quality);
    }
}
