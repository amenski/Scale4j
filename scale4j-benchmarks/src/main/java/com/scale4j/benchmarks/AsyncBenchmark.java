package com.scale4j.benchmarks;

import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class AsyncBenchmark {

    @Param({"640x480", "1920x1080", "3840x2160"})
    public String imageSize;

    @Param({"4", "16", "64"})
    public int concurrency;

    public BufferedImage sourceImage;
    public ExecutorService executor;

    @Setup
    public void setup() {
        String[] parts = imageSize.split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        sourceImage = createTestImage(width, height);

        try {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } catch (Exception e) {
            executor = Executors.newCachedThreadPool();
        }
    }

    @TearDown
    public void tearDown() {
        if (executor != null) {
            executor.shutdown();
        }
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
    public List<BufferedImage> asyncResizeMultiple() throws Exception {
        List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            CompletableFuture<BufferedImage> future = CompletableFuture.supplyAsync(() -> {
                return com.scale4j.ops.ResizeOperation.resize(
                        sourceImage, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
            }, executor);
            futures.add(future);
        }

        List<BufferedImage> results = new ArrayList<>();
        for (CompletableFuture<BufferedImage> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    @Benchmark
    public int asyncResizeThroughput() throws Exception {
        List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            CompletableFuture<BufferedImage> future = CompletableFuture.supplyAsync(() -> {
                return com.scale4j.ops.ResizeOperation.resize(
                        sourceImage, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        return futures.size();
    }

    @Benchmark
    public List<BufferedImage> asyncChainedOperations() throws Exception {
        List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            CompletableFuture<BufferedImage> future = CompletableFuture.supplyAsync(() -> {
                BufferedImage resized = com.scale4j.ops.ResizeOperation.resize(
                        sourceImage, 800, 600, ResizeMode.FIT, ResizeQuality.MEDIUM);
                BufferedImage rotated = com.scale4j.ops.RotateOperation.rotate(resized, 90);
                return com.scale4j.ops.PadOperation.pad(rotated, 20, 20, 20, 20, Color.WHITE);
            }, executor);
            futures.add(future);
        }

        List<BufferedImage> results = new ArrayList<>();
        for (CompletableFuture<BufferedImage> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    @Benchmark
    public List<BufferedImage> asyncPipeline() throws Exception {
        List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            CompletableFuture<BufferedImage> future = CompletableFuture.supplyAsync(() -> {
                return com.scale4j.ops.ResizeOperation.resize(sourceImage, 800, 600, 
                        ResizeMode.FIT, ResizeQuality.MEDIUM);
            }, executor)
            .thenApply(img -> com.scale4j.ops.RotateOperation.rotate(img, 45))
            .thenApply(img -> com.scale4j.ops.PadOperation.pad(img, 10, 10, 10, 10, Color.WHITE));
            futures.add(future);
        }

        List<BufferedImage> results = new ArrayList<>();
        for (CompletableFuture<BufferedImage> future : futures) {
            results.add(future.get());
        }
        return results;
    }
}
