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
package com.scale4j.examples;

import static com.scale4j.watermark.WatermarkPosition.BOTTOM_RIGHT;

import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.ImageWatermark;
import com.scale4j.watermark.TextWatermark;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 * Generates example images for the README using Scale4j itself.
 *
 * <p>Run from the project root:
 *
 * <pre>{@code
 * mvn exec:java -pl scale4j-examples \
 *   -Dexec.mainClass=com.scale4j.examples.ReadmeImageGenerator \
 *   -Dexec.args="../test-image.jpg ../do-not-copy-watermark.png ../docs/images"
 * }</pre>
 */
public final class ReadmeImageGenerator {

    private static final int JPEG_QUALITY = 85;

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: ReadmeImageGenerator <source-image> <watermark-image> <output-dir>");
            System.exit(1);
        }

        Path sourcePath = Paths.get(args[0]);
        Path watermarkPath = Paths.get(args[1]);
        Path outputDir = Paths.get(args[2]);

        if (!Files.exists(sourcePath)) {
            System.err.println("Source image not found: " + sourcePath.toAbsolutePath());
            System.exit(1);
        }
        if (!Files.exists(watermarkPath)) {
            System.err.println("Watermark image not found: " + watermarkPath.toAbsolutePath());
            System.exit(1);
        }

        Files.createDirectories(outputDir);

        BufferedImage original = ImageIO.read(sourcePath.toFile());
        if (original == null) {
            System.err.println("Failed to load source image (unsupported format?): " + sourcePath.toAbsolutePath());
            System.exit(1);
        }
        System.out.printf("Loaded source: %dx%d%n", original.getWidth(), original.getHeight());

        BufferedImage watermarkImg = ImageIO.read(watermarkPath.toFile());
        System.out.printf("Loaded watermark: %dx%d%n", watermarkImg.getWidth(), watermarkImg.getHeight());

        save(original, outputDir, "original.jpg");

        process(original, outputDir, "resize-fit.jpg", b -> Scale4j.load(b)
                .resize(200, 150, ResizeMode.FIT)
                .quality(ResizeQuality.ULTRA)
                .build());

        process(original, outputDir, "resize-fill.jpg", b -> Scale4j.load(b)
                .resize(200, 200, ResizeMode.FILL)
                .quality(ResizeQuality.ULTRA)
                .build());

        process(original, outputDir, "crop.jpg", b -> Scale4j.load(b)
                .crop(100, 75, 200, 150)
                .build());

        process(original, outputDir, "rotate-90.jpg", b -> Scale4j.load(b)
                .rotate(90)
                .build());

        process(original, outputDir, "rotate-45.jpg", b -> Scale4j.load(b)
                .rotate(45, Color.WHITE)
                .build());

        process(original, outputDir, "pad.jpg", b -> Scale4j.load(b)
                .pad(20, Color.WHITE)
                .build());

        process(original, outputDir, "watermark-text.jpg", b -> Scale4j.load(b)
                .watermark(TextWatermark.builder()
                        .text("Scale4j")
                        .font("Arial", Font.BOLD, 36)
                        .color(Color.WHITE)
                        .opacity(0.7f)
                        .position(BOTTOM_RIGHT)
                        .backgroundColor(new Color(0, 0, 0, 80))
                        .margin(10)
                        .build())
                .build());

        process(original, outputDir, "watermark-image.jpg", b -> Scale4j.load(b)
                .watermark(ImageWatermark.builder()
                        .image(watermarkImg)
                        .opacity(0.5f)
                        .scale(0.15f)
                        .position(BOTTOM_RIGHT)
                        .build())
                .build());

        process(original, outputDir, "grayscale.jpg", b -> Scale4j.load(b)
                .grayscale()
                .build());

        process(original, outputDir, "sepia.jpg", b -> Scale4j.load(b)
                .sepia()
                .build());

        process(original, outputDir, "blur.jpg", b -> Scale4j.load(b)
                .blur(5f)
                .build());

        process(original, outputDir, "sharpen.jpg", b -> Scale4j.load(b)
                .sharpen()
                .build());

        process(original, outputDir, "edge-detect.jpg", b -> Scale4j.load(b)
                .edgeDetect()
                .build());

        process(original, outputDir, "vignette.jpg", b -> Scale4j.load(b)
                .vignette(0.7f)
                .build());

        process(original, outputDir, "invert.jpg", b -> Scale4j.load(b)
                .invert()
                .build());

        process(original, outputDir, "brightness.jpg", b -> Scale4j.load(b)
                .brightness(1.5f)
                .build());

        process(original, outputDir, "contrast.jpg", b -> Scale4j.load(b)
                .contrast(1.5f)
                .build());

        process(original, outputDir, "flip.jpg", b -> Scale4j.load(b)
                .flip()
                .build());

        process(original, outputDir, "flop.jpg", b -> Scale4j.load(b)
                .flop()
                .build());

        process(original, outputDir, "chained.jpg", b -> Scale4j.load(b)
                .resize(300, 225, ResizeMode.FIT)
                .crop(25, 25, 250, 175)
                .rotate(90)
                .pad(15, Color.WHITE)
                .watermark(TextWatermark.builder()
                        .text("Scale4j")
                        .font("Arial", Font.BOLD, 28)
                        .color(Color.WHITE)
                        .opacity(0.7f)
                        .position(BOTTOM_RIGHT)
                        .build())
                .build());

        System.out.println("All images generated in " + outputDir.toAbsolutePath());
    }

    private static void save(BufferedImage image, Path outputDir, String filename) throws Exception {
        Path file = outputDir.resolve(filename);
        Scale4j.load(image)
                .toFile(file);
        System.out.printf("  %s (%dx%d)%n", filename, image.getWidth(), image.getHeight());
    }

    @FunctionalInterface
    private interface ImageProcessor {
        BufferedImage process(BufferedImage source) throws Exception;
    }

    private static void process(BufferedImage source, Path outputDir, String filename,
                                ImageProcessor processor) throws Exception {
        BufferedImage result = processor.process(source);
        Scale4j.load(result)
                .toFile(outputDir.resolve(filename));
        System.out.printf("  %s (%dx%d)%n", filename, result.getWidth(), result.getHeight());
    }
}
