# Identified Issues & Actionable Tasks for Scale4j

This document catalogs the issues discovered during the technical review of Scale4j, categorized by type (bug, performance, feature gap, etc.). Each issue is accompanied by a concrete task that can be executed to address it.

---

## 1. Resize Algorithm Performance & Quality

**Issue:** `ResizeOperation.scaleImage()` uses `Image.getScaledInstance()`, which is known for poor performance, high memory consumption, and suboptimal visual quality. The rendering hints set on the `Graphics2D` context may not be respected by this legacy method.

**Impact:** Slow resize operations, especially on large images; lower output quality compared to modern interpolation techniques.

**Task:** Replace `getScaledInstance` with `AffineTransformOp` using configurable interpolation (nearest‑neighbor, bilinear, bicubic). Implement a dedicated high‑quality scaler that respects the `ResizeQuality` enum.

**Suggested Implementation:**
```java
private static BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight,
                                         ResizeQuality quality) {
    BufferedImage dest = new BufferedImage(targetWidth, targetHeight, source.getType());
    AffineTransform at = AffineTransform.getScaleInstance(
        (double) targetWidth / source.getWidth(),
        (double) targetHeight / source.getHeight()
    );
    RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
        getInterpolationHint(quality));
    AffineTransformOp op = new AffineTransformOp(at, hints);
    return op.filter(source, dest);
}
```

---

## 2. Inadequate Test Coverage

**Issue:** The project contains only one test class (`Scale4jTest`) with basic unit tests. Missing tests for edge cases, invalid parameters, async operations, watermarking, and integration scenarios.

**Impact:** Low confidence in correctness, risk of regression when refactoring, difficult to ensure compatibility with imgscalr.

**Task:** Expand test suite to cover:
- Each operation (`ResizeOperation`, `CropOperation`, `RotateOperation`, `PadOperation`) with boundary values.
- `Scale4jBuilder` chaining and error handling.
- `AsyncScale4j` with mocked executor.
- Watermark classes (text and image) with various positions and opacities.
- Image I/O (`ImageLoader`, `ImageSaver`) with unsupported formats.
- Property‑based testing (using JUnit‑Quickcheck) to validate invariants.

---

## 3. Missing Performance Benchmarks

**Issue:** The `scale4j-benchmarks` module exists but contains no JMH benchmarks. No performance baseline exists to compare against imgscalr or to measure improvements.

**Impact:** Inability to quantify performance impact of changes; cannot claim performance superiority over deprecated library.

**Task:** Implement JMH benchmarks for:
- Resize at different sizes and quality levels.
- Crop, rotate, pad operations.
- Chained operations (typical workflows).
- Async throughput with virtual threads vs. thread pool.
- Memory usage and GC pressure.

Store benchmark results in `docs/benchmarks/` and integrate with CI (run on schedule).

---

## 4. No EXIF Metadata Preservation

**Issue:** Scale4j does not preserve EXIF (or other) metadata when loading, processing, and saving images. This is a critical requirement for photography applications.

**Impact:** Loss of orientation, camera settings, geotags, etc., making the library unsuitable for many real‑world image pipelines.

**Task:** Integrate a lightweight metadata library (e.g., Apache Sanselan, TwelveMonkeys ImageIO) to read metadata from source images and re‑attach it to processed outputs. At a minimum, preserve orientation tags and apply automatic rotation.

---

## 5. Limited Image Format Support

**Issue:** Core module relies on ImageIO’s built‑in formats (JPEG, PNG, GIF, BMP). WebP and AVIF extensions are declared but not implemented.

**Impact:** Cannot process modern formats without additional manual setup; extensions are essentially placeholders.

**Task:** Complete the `scale4j-ext-webp` and `scale4j-ext-avif` modules by:
- Adding required ImageIO plugin dependencies (e.g., `com.github.jai-imageio:jai-imageio‑webp`, `com.github.romankh3:imageio‑avif`).
- Providing `ImageReader`/`ImageWriter` registrations.
- Documenting usage and fallback behavior.

---

## 6. Error Handling & Logging

**Issue:** Exceptions are generic (`IllegalArgumentException`, `RuntimeException`, `IOException`) with minimal context. No logging facade or configuration hooks.

**Impact:** Difficult to diagnose failures in production; cannot integrate with application monitoring.

**Task:** Introduce a dedicated exception hierarchy (`Scale4jException`, `ImageLoadException`, `ImageProcessException`, etc.) with detailed messages and optional cause chains. Provide a `Scale4jLogger` interface (SLF4J optional) for debug/trace output.

---

## 7. Memory Inefficiency

**Issue:** Each operation creates a new `BufferedImage` instance; intermediate images are not reused or cached. For chains of operations, this leads to unnecessary memory churn.

**Impact:** Higher memory footprint and GC pressure when processing multiple images or large batches.

**Task:** Implement a lightweight pooling mechanism for `BufferedImage` objects (e.g., using `SoftReference`). Allow re‑use of intermediate images when dimensions match. Add a `BufferReuse` option in `Scale4jBuilder`.

---

## 8. Missing Batch Processing API

**Issue:** The library processes single images only. No built‑in support for processing collections of images with parallel streams or bulk async operations.

**Impact:** Users must write boilerplate for batch operations, missing optimization opportunities.

**Task:** Add `Scale4j.batch()` method returning a `BatchProcessor` that can apply the same operation chain to multiple images, optionally parallelized. Integrate with `AsyncScale4j` for non‑blocking batch workflows.

---

## 9. No Image Filters / Effects

**Issue:** Only basic geometric operations (resize, crop, rotate, pad) and watermarks are supported. Common image filters (blur, sharpen, brightness, contrast, grayscale) are missing.

**Impact:** Library cannot replace more full‑featured image processing solutions; users need to combine multiple libraries.

**Task:** Create a `filter` package with operations based on `ConvolveOp`, `LookupOp`, and `RescaleOp`. Provide builder methods for common filters (`.blur(radius)`, `.sharpen()`, `.grayscale()`, `.brightness(factor)`).

---

## 10. Watermark Positioning Bug

**Issue:** `WatermarkPosition.calculate()` returns incorrect Y coordinates for `TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT` (adds `watermarkHeight` instead of 0). This causes watermarks to be placed lower than intended.

**Impact:** Text and image watermarks appear shifted downward, especially noticeable for top positions.

**Task:** Fix coordinate calculation in `WatermarkPosition`. For top positions, `y` should be `0 + margin` (or `watermarkHeight` for bottom?). Verify all nine positions with unit tests.

---

## 11. Missing Documentation for Extension Development

**Issue:** The plugin architecture is mentioned in the README but there is no guide on how to write custom operations or format extensions.

**Impact:** Third‑party developers cannot extend the library easily, limiting ecosystem growth.

**Task:** Write a `EXTENSIONS.md` guide in the `docs/` folder explaining:
- The `Operation` interface (if exists) or how to add custom `UnaryOperator<BufferedImage>`.
- Registering new image formats via `ImageIO`.
- Packaging extensions as separate modules.

---

## 12. CI/CD Does Not Run Benchmarks

**Issue:** The GitHub Actions workflow only runs unit tests; benchmarks are not executed regularly.

**Impact:** Performance regressions may go unnoticed.

**Task:** Add a scheduled CI job (nightly) that runs JMH benchmarks and compares results against a baseline, failing if significant degradation occurs. Store benchmark history as artifacts.

---

## Prioritization

| Priority | Issue | Effort | Impact | Status |
|----------|-------|--------|--------|--------|
| P0 | Resize Algorithm Performance | Medium | High | ✅ DONE |
| P0 | Watermark Positioning Bug | Low | Medium | ✅ DONE |
| P1 | Inadequate Test Coverage | High | High | ✅ DONE |
| P1 | Missing Performance Benchmarks | Medium | Medium | ✅ DONE |
| P2 | No EXIF Metadata Preservation | High | High | ✅ DONE |
| P2 | Limited Image Format Support | Medium | Medium | ✅ DONE |
| P3 | Error Handling & Logging | Low | Medium | ✅ DONE |
| P3 | Memory Inefficiency | Medium | Low | ✅ DONE |
| P4 | Missing Batch Processing API | Medium | Low | ✅ DONE |
| P4 | No Image Filters / Effects | High | Medium | |
| P5 | Documentation for Extensions | Low | Low | |

---

## Next Steps

1. Create GitHub issues for each task.
2. For each P0/P1 issue, assign a milestone (e.g., `v1.1.0`).
3. Begin implementation with the resize algorithm replacement, as it is the most critical performance bottleneck.
4. After fixing the watermark bug, add comprehensive watermark tests.
5. Expand test suite to reach >80% line coverage.
6. Implement JMH benchmarks and integrate into CI.

*This task list was generated from the technical review conducted on 2026‑02‑14.*