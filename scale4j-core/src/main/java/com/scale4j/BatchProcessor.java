/*
 * Copyright 2024 Scale4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scale4j;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Batch processor for applying image processing operations to multiple images.
 * Supports parallel processing via executor service and non-blocking async workflows.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * List<BufferedImage> results = Scale4j.batch()
 *     .images(listOfImages)
 *     .resize(300, 200)
 *     .parallel(4)
 *     .execute();
 *
 * // Async version
 * List<CompletableFuture<BufferedImage>> futures = Scale4j.batch()
 *     .images(listOfImages)
 *     .resize(300, 200)
 *     .parallel(4)
 *     .executeAsync();
 * }</pre>
 *
 * @author Scale4j
 * @version 5.0.0
 */
public final class BatchProcessor {

    private final List<BufferedImage> images;
    private final Function<Scale4jBuilder, Scale4jBuilder> operationChain;
    private final ExecutorService executor;
    private final int parallelism;
    private final boolean preserveOrder;

    BatchProcessor(List<BufferedImage> images,
                   Function<Scale4jBuilder, Scale4jBuilder> operationChain,
                   ExecutorService executor,
                   int parallelism,
                   boolean preserveOrder) {
        this.images = Objects.requireNonNull(images, "Images list cannot be null");
        this.operationChain = operationChain;
        this.executor = executor;
        this.parallelism = parallelism > 0 ? parallelism : 1;
        this.preserveOrder = preserveOrder;
    }

    /**
     * Executes the batch processing synchronously.
     *
     * @return list of processed images
     */
    public List<BufferedImage> execute() {
        if (images.isEmpty()) {
            return Collections.emptyList();
        }

        if (executor != null) {
            return executeWithExecutor();
        } else if (parallelism > 1) {
            return executeWithParallelism();
        } else {
            return executeSequentially();
        }
    }

    /**
     * Executes the batch processing asynchronously.
     *
     * @return list of CompletableFuture containing processed images
     */
    public List<CompletableFuture<BufferedImage>> executeAsync() {
        if (images.isEmpty()) {
            return Collections.emptyList();
        }

        ExecutorService exec = executor != null ? executor : createDefaultExecutor();

        List<CompletableFuture<BufferedImage>> futures = images.stream()
                .map(image -> CompletableFuture.supplyAsync(() -> processImage(image), exec))
                .collect(Collectors.toList());

        if (preserveOrder) {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return futures;
    }

    /**
     * Executes the batch processing and returns a single CompletableFuture
     * that completes when all images are processed.
     *
     * @return CompletableFuture that completes with list of processed images
     */
    public CompletableFuture<List<BufferedImage>> executeAndJoin() {
        ExecutorService exec = executor != null ? executor : createDefaultExecutor();

        List<CompletableFuture<BufferedImage>> futures = images.stream()
                .map(image -> CompletableFuture.supplyAsync(() -> processImage(image), exec))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        return allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        ).whenComplete((result, ex) -> {
            if (executor == null) {
                exec.shutdown();
            }
        });
    }

    private List<BufferedImage> executeWithExecutor() {
        List<CompletableFuture<BufferedImage>> futures = images.stream()
                .map(image -> CompletableFuture.supplyAsync(() -> processImage(image), executor))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        if (preserveOrder) {
            allFutures.join();
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private List<BufferedImage> executeSequentially() {
        List<BufferedImage> results = new ArrayList<>(images.size());
        for (BufferedImage image : images) {
            results.add(processImage(image));
        }
        return results;
    }

    private List<BufferedImage> executeWithParallelism() {
        ExecutorService exec = Executors.newFixedThreadPool(parallelism);
        try {
            List<CompletableFuture<BufferedImage>> futures = images.stream()
                    .map(image -> CompletableFuture.supplyAsync(() -> processImage(image), exec))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            if (preserveOrder) {
                allFutures.join();
            }

            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } finally {
            exec.shutdown();
        }
    }

    private BufferedImage processImage(BufferedImage image) {
        Scale4jBuilder builder = operationChain.apply(new Scale4jBuilder(image));
        return builder.build();
    }

    private static ExecutorService createDefaultExecutor() {
        try {
            var executorClass = Class.forName("java.util.concurrent.Executors");
            var newVirtualThreadPerTaskExecutor = executorClass.getMethod("newVirtualThreadPerTaskExecutor");
            return (ExecutorService) newVirtualThreadPerTaskExecutor.invoke(null);
        } catch (Exception e) {
            return java.util.concurrent.Executors.newCachedThreadPool();
        }
    }

    /**
     * Returns the number of images in this batch.
     *
     * @return image count
     */
    public int getImageCount() {
        return images.size();
    }

    /**
     * Returns the configured parallelism level.
     *
     * @return parallelism
     */
    public int getParallelism() {
        return parallelism;
    }

    /**
     * Returns whether order preservation is enabled.
     *
     * @return true if order is preserved
     */
    public boolean isPreserveOrder() {
        return preserveOrder;
    }
}
