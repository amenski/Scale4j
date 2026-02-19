# Identified Issues & Actionable Tasks for Scale4j

This document catalogs the issues discovered during the technical review of Scale4j, categorized by type (bug, performance, feature gap, etc.). Each issue is accompanied by a concrete task that can be executed to address it.

## Current Status

| # | Issue | Status | ROADMAP |
|---|-------|--------|---------|
| 1 | Resize Algorithm Performance & Quality | ✅ Done | [Task 1](#1-resize-algorithm-performance--quality) |
| 2 | Inadequate Test Coverage | 🔄 In Progress | [Task 3](#2-inadequate-test-coverage) |
| 3 | Missing Performance Benchmarks | ✅ Done | [Task 4](#3-missing-performance-benchmarks) |
| 4 | No EXIF Metadata Preservation | ✅ Done | [Task 5](#4-no-exif-metadata-preservation) |
| 5 | Limited Image Format Support (modules removed) | ⏸️ Postponed | [Task 6](#5-limited-image-format-support) |
| 6 | Error Handling & Logging | ✅ Done | [Task 7](#6-error-handling--logging) |
| 7 | Memory Inefficiency | ✅ Done | [Task 8](#7-memory-inefficiency) |
| 8 | Missing Batch Processing API | ✅ Done | [Task 9](#8-missing-batch-processing-api) |
| 9 | No Image Filters / Effects | ✅ Done | [Task 10](#9-no-image-filters--effects) |
| 10 | Watermark Positioning Bug | ✅ Done | [Task 2](#10-watermark-positioning-bug) |
| 11 | Missing Documentation for Extension Development | ✅ Done | [Task 11](#11-missing-documentation-for-extension-development) |
| 12 | CI/CD Does Not Run Benchmarks | ⬜ Not Started | [Task 12](#12-cicd-does-not-run-benchmarks) |

*Note: Section numbers correspond to this document. ROADMAP task numbers show the corresponding entry in ROADMAP.md.*

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

**Issue:** Core module relies on ImageIO’s built‑in formats (JPEG, PNG, GIF, BMP). WebP and AVIF extensions were placeholders but have been removed due to lack of maintained ImageIO plugins for Java 17.

**Impact:** Cannot process WebP or AVIF formats. Users must convert images to supported formats before processing.

**Task:** Postponed. Future releases may reintroduce format‑support modules when stable, well‑maintained ImageIO plugins become available.

---

## 6. Error Handling & Logging

**Issue:** Exceptions are generic (`IllegalArgumentException`, `RuntimeException`, `IOException`) with minimal context. No logging facade or configuration hooks.

**Impact:** Difficult to diagnose failures in production; cannot integrate with application monitoring.

**Task:** Introduce a dedicated exception hierarchy (`Scale4jException`, `ImageLoadException`, `ImageProcessException`, etc.) with detailed messages and optional cause chains. Provide a `Scale4jLogger` interface (SLF4J optional) for debug/trace output.

---

## 7. Memory Inefficiency

**Issue:** Each operation creates a new `BufferedImage` instance; intermediate images are not reused or cached. For chains of operations, this leads to unnecessary memory churn.

**Impact:** Higher memory footprint and GC pressure when processing multiple images or large batches.

**Task:** Implement scratch buffer optimization to reuse a single transient `BufferedImage` buffer within each builder chain when dimensions match. This eliminates unnecessary allocations for chained operations.

**Status:** ✅ Implemented via scratch buffer technique (see README).

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
| P1 | Inadequate Test Coverage | High | High | 🔄 IN PROGRESS |
| P1 | Missing Performance Benchmarks | Medium | Medium | ✅ DONE |
| P2 | No EXIF Metadata Preservation | High | High | ✅ DONE |
| P2 | Limited Image Format Support | Medium | Medium | ⬜ NOT STARTED |
| P3 | Error Handling & Logging | Low | Medium | ✅ DONE |
| P3 | Memory Inefficiency | Medium | Low | ✅ DONE |
| P4 | Missing Batch Processing API | Medium | Low | ✅ DONE |
| P4 | No Image Filters / Effects | High | Medium | ✅ DONE |
| P5 | Documentation for Extensions | Low | Low | ✅ DONE |

---

## Next Steps

1. **Improve test coverage** – Expand test suite to >80% line coverage, add property‑based and integration tests.
2. **Implement WebP/AVIF format support** – Complete the placeholder extension modules with actual format support.
3. **Set up CI benchmark runs** – Create GitHub Actions workflow to run JMH benchmarks on schedule.
4. **Push v1.1.0‑alpha tag to remote** – Share the milestone with collaborators.

*This task list was generated from the technical review conducted on 2026‑02‑14.*  
*Updated based on current progress as of 2026‑02‑19.*