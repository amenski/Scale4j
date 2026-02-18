package com.scale4j.benchmarks;

import com.scale4j.ops.CropOperation;
import com.scale4j.ops.PadOperation;
import com.scale4j.ops.ResizeOperation;
import com.scale4j.ops.RotateOperation;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ChainedOperationsBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

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
    public BufferedImage resizeThenCrop() {
        BufferedImage resized = ResizeOperation.resize(sourceImage, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
        return CropOperation.crop(resized, 100, 100, 400, 300);
    }

    @Benchmark
    public BufferedImage cropThenResize() {
        BufferedImage cropped = CropOperation.crop(sourceImage, 100, 100, 400, 300);
        return ResizeOperation.resize(cropped, 800, 600, ResizeMode.EXACT, ResizeQuality.MEDIUM);
    }

    @Benchmark
    public BufferedImage rotateThenResize() {
        BufferedImage rotated = RotateOperation.rotate(sourceImage, 90);
        return ResizeOperation.resize(rotated, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
    }

    @Benchmark
    public BufferedImage resizeThenRotateThenPad() {
        BufferedImage resized = ResizeOperation.resize(sourceImage, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
        BufferedImage rotated = RotateOperation.rotate(resized, 90);
        return PadOperation.pad(rotated, 20, 20, 20, 20, Color.WHITE);
    }

    @Benchmark
    public BufferedImage fullPipeline() {
        BufferedImage cropped = CropOperation.crop(sourceImage, 50, 50, 
                sourceImage.getWidth() - 100, sourceImage.getHeight() - 100);
        BufferedImage resized = ResizeOperation.resize(cropped, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
        BufferedImage rotated = RotateOperation.rotate(resized, 45);
        return PadOperation.pad(rotated, 10, 10, 10, 10, Color.WHITE);
    }

    @Benchmark
    public BufferedImage multipleResize() {
        BufferedImage img1 = ResizeOperation.resize(sourceImage, 1600, 1200, ResizeMode.FIT, ResizeQuality.LOW);
        BufferedImage img2 = ResizeOperation.resize(img1, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
        return ResizeOperation.resize(img2, 400, 300, ResizeMode.FIT, ResizeQuality.HIGH);
    }
}
