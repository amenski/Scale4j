package com.scale4j.benchmarks;

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
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

        sourceImage = TestImageFactory.createComplexTestImage(width, height);

        virtualThreadExecutor = Executors.newCachedThreadPool();
    }

    @TearDown
    public void tearDown() {
        if (virtualThreadExecutor != null) {
            virtualThreadExecutor.shutdown();
        }
    }


}
