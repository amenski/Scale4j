package com.scale4j.benchmarks;

import com.scale4j.ops.CropOperation;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class CropBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

    @Param({"0.25", "0.5", "0.75"})
    public String cropRatio;

    public BufferedImage sourceImage;
    public int cropX, cropY, cropWidth, cropHeight;

    @Setup
    public void setup() {
        String[] parts = imageSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = createTestImage(width, height);

        double ratio = Double.parseDouble(cropRatio);
        cropWidth = (int) (width * ratio);
        cropHeight = (int) (height * ratio);
        cropX = (width - cropWidth) / 2;
        cropY = (height - cropHeight) / 2;
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
    public BufferedImage crop() {
        return CropOperation.crop(sourceImage, cropX, cropY, cropWidth, cropHeight);
    }

    @Benchmark
    public BufferedImage cropCorner() {
        return CropOperation.crop(sourceImage, 0, 0, cropWidth, cropHeight);
    }

    @Benchmark
    public BufferedImage cropCenter() {
        return CropOperation.crop(sourceImage, cropX, cropY, cropWidth, cropHeight);
    }
}
