package com.scale4j.benchmarks;

import com.scale4j.ops.PadOperation;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PadBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

    @Param({"10", "50", "100"})
    public int padding;

    public BufferedImage sourceImage;

    @Setup
    public void setup() {
        String[] parts = imageSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = TestImageFactory.createSolidTestImage(width, height);
    }



    @Benchmark
    public BufferedImage padUniformWhite() {
        return PadOperation.pad(sourceImage, padding, padding, padding, padding, Color.WHITE);
    }

    @Benchmark
    public BufferedImage padUniformBlack() {
        return PadOperation.pad(sourceImage, padding, padding, padding, padding, Color.BLACK);
    }

    @Benchmark
    public BufferedImage padAsymmetric() {
        return PadOperation.pad(sourceImage, padding, padding * 2, padding, padding * 2, Color.WHITE);
    }

    @Benchmark
    public BufferedImage padTransparent() {
        return PadOperation.pad(sourceImage, padding, padding, padding, padding, new Color(0, 0, 0, 0));
    }
}
