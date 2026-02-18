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

        sourceImage = TestImageFactory.createSolidTestImage(width, height);
        quality = ResizeQuality.valueOf(qualityStr);
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
