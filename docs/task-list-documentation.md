# Scale4j Task List Documentation

This document provides comprehensive, standardized documentation for every task list contained within the ROADMAP.md file. It is intended as a formal wiki/knowledge base entry, offering detailed explanations, dependencies, and acceptance criteria for each list.

## Table of Contents

- [1. Primary Development Tasks (Main Table)](#1-primary-development-tasks-main-table)
- [2. Milestone v1.1.0 (P0/P1)](#2-milestone-v110-p0p1)
- [3. Milestone v1.2.0 (P2/P3)](#3-milestone-v120-p2p3)
- [4. Milestone v1.3.0 (P4)](#4-milestone-v130-p4)
- [5. How to Use (Guidelines)](#5-how-to-use-guidelines)
- [6. Updating This File (Procedural Steps)](#6-updating-this-file-procedural-steps)
- [Summary](#summary)

## 1. Primary Development Tasks (Main Table)

**Title:** Primary Development Tasks  
**Purpose:** To track the progress of all identified technical issues and feature gaps in Scale4j, ensuring systematic improvement of the library's performance, robustness, and functionality.  
**Source:** ROADMAP.md “Task List” table (lines 14‑28) and detailed descriptions in `docs/identified-issues-tasklist.md`.

### Task Breakdown

#### Task 1: Resize Algorithm Performance & Quality
- **Priority:** P0
- **Status:** Done (already using `AffineTransformOp`)
- **Issue:** `ResizeOperation.scaleImage()` used `Image.getScaledInstance()`, which is known for poor performance, high memory consumption, and suboptimal visual quality.
- **Objective:** Replace `getScaledInstance` with `AffineTransformOp` using configurable interpolation (nearest‑neighbor, bilinear, bicubic) that respects the `ResizeQuality` enum.
- **Implementation Details:** A dedicated high‑quality scaler that applies affine transformation with appropriate rendering hints. The suggested implementation is already present in the codebase.
- **Dependencies:** None (implementation already complete).
- **Acceptance Criteria:**
    - Resize operations use `AffineTransformOp` exclusively.
    - All `ResizeQuality` values map to correct `RenderingHints.KEY_INTERPOLATION` values.
    - Unit tests verify performance and quality improvements.

#### Task 2: Watermark Positioning Bug
- **Priority:** P0
- **Status:** Done (verified in `WatermarkPosition.calculate()`)
- **Issue:** `WatermarkPosition.calculate()` returned incorrect Y coordinates for `TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT` (added `watermarkHeight` instead of 0), causing watermarks to be placed lower than intended.
- **Objective:** Fix coordinate calculation so that top‑aligned watermarks appear at the correct vertical position.
- **Implementation Details:** Adjust the Y‑coordinate formula for top positions to use `0 + margin`. Verify all nine positions with unit tests.
- **Dependencies:** None (fix already applied).
- **Acceptance Criteria:**
    - Watermarks placed at top positions appear flush with the top edge (plus configured margin).
    - Unit tests for each `WatermarkPosition` pass.
    - Visual verification with sample images.

#### Task 3: Inadequate Test Coverage
- **Priority:** P1
- **Status:** In Progress (basic tests exist; need more coverage)
- **Issue:** The project contains only one test class (`Scale4jTest`) with basic unit tests. Missing tests for edge cases, invalid parameters, async operations, watermarking, and integration scenarios.
- **Objective:** Expand the test suite to achieve >80% line coverage and ensure robustness against regressions.
- **Implementation Details:**
    - Add comprehensive unit tests for each operation (`ResizeOperation`, `CropOperation`, `RotateOperation`, `PadOperation`) with boundary values.
    - Test `Scale4jBuilder` chaining and error handling.
    - Test `AsyncScale4j` with mocked executor.
    - Test watermark classes (text and image) with various positions and opacities.
    - Test Image I/O (`ImageLoader`, `ImageSaver`) with unsupported formats.
    - Introduce property‑based testing (using JUnit‑Quickcheck) to validate invariants.
- **Dependencies:** Completion of Tasks 1 and 2 to ensure stable behavior.
- **Acceptance Criteria:**
    - Line coverage exceeds 80% (measured by JaCoCo).
    - All new tests pass.
    - Property‑based tests generate at least 100 random inputs per invariant.

#### Task 4: Missing Performance Benchmarks
- **Priority:** P1
- **Status:** Not Started (benchmark module empty)
- **Issue:** The `scale4j‑benchmarks` module exists but contains no JMH benchmarks. No performance baseline exists to compare against imgscalr or to measure improvements.
- **Objective:** Implement JMH benchmarks that quantify the performance of core operations and provide a baseline for future optimizations.
- **Implementation Details:**
    - Create JMH benchmarks for resize at different sizes and quality levels.
    - Benchmarks for crop, rotate, pad operations.
    - Benchmarks for chained operations (typical workflows).
    - Benchmarks for async throughput with virtual threads vs. thread pool.
    - Benchmarks for memory usage and GC pressure.
    - Store benchmark results in `docs/benchmarks/` and integrate with CI (run on schedule).
- **Dependencies:** Task 3 (test coverage) should be advanced to ensure stable code for benchmarking.
- **Acceptance Criteria:**
    - All JMH benchmarks run without errors.
    - Benchmark results are stored as JSON/CSV files.
    - CI job can execute benchmarks on a scheduled basis.

#### Task 5: No EXIF Metadata Preservation
- **Priority:** P2
- **Status:** Not Started
- **Issue:** Scale4j does not preserve EXIF (or other) metadata when loading, processing, and saving images. This is a critical requirement for photography applications.
- **Objective:** Integrate a lightweight metadata library to read metadata from source images and re‑attach it to processed outputs, preserving orientation tags and applying automatic rotation.
- **Implementation Details:**
    - Evaluate and select a metadata library (Apache Sanselan, TwelveMonkeys ImageIO).
    - Add dependency to `pom.xml`.
    - Implement metadata extraction and re‑injection in `ImageLoader` and `ImageSaver`.
    - Ensure orientation tags are applied automatically.
- **Dependencies:** None, but should be coordinated with Task 6 (format support) as metadata handling may differ per format.
- **Acceptance Criteria:**
    - EXIF orientation is correctly applied to rotated images.
    - All other metadata fields (camera settings, geotags, etc.) are preserved.
    - Unit tests verify metadata preservation for JPEG, PNG, and other supported formats.

#### Task 6: Limited Image Format Support
- **Priority:** P2
- **Status:** Not Started (placeholder modules)
- **Issue:** Core module relies on ImageIO’s built‑in formats (JPEG, PNG, GIF, BMP). WebP and AVIF extensions are declared but not implemented.
- **Objective:** Complete the `scale4j‑ext‑webp` and `scale4j‑ext‑avif` modules, providing full support for modern image formats.
- **Implementation Details:**
    - Add required ImageIO plugin dependencies (e.g., `com.github.jai‑imageio:jai‑imageio‑webp`, `com.github.romankh3:imageio‑avif`).
    - Provide `ImageReader`/`ImageWriter` registrations.
    - Document usage and fallback behavior.
    - Ensure the extensions integrate seamlessly with the core `ImageLoader`/`ImageSaver`.
- **Dependencies:** Task 5 (metadata preservation) may need adaptation for WebP/AVIF metadata.
- **Acceptance Criteria:**
    - WebP and AVIF images can be loaded, processed, and saved using Scale4j.
    - Unit tests verify round‑trip conversion for each format.
    - Documentation includes installation and usage examples.

#### Task 7: Error Handling & Logging
- **Priority:** P3
- **Status:** Not Started
- **Issue:** Exceptions are generic (`IllegalArgumentException`, `RuntimeException`, `IOException`) with minimal context. No logging facade or configuration hooks.
- **Objective:** Introduce a dedicated exception hierarchy and optional SLF4J logging facade to improve debuggability and production integration.
- **Implementation Details:**
    - Define exception classes: `Scale4jException`, `ImageLoadException`, `ImageProcessException`, etc.
    - Replace generic exceptions with specific ones, preserving cause chains.
    - Provide a `Scale4jLogger` interface that delegates to SLF4J (optional).
    - Add configuration for log levels and output.
- **Dependencies:** None.
- **Acceptance Criteria:**
    - All thrown exceptions are instances of the new hierarchy.
    - Logging can be enabled/disabled without affecting functionality.
    - Existing unit tests continue to pass (exception types may need adjustment).

#### Task 8: Memory Inefficiency
- **Priority:** P3
- **Status:** Not Started
- **Issue:** Each operation creates a new `BufferedImage` instance; intermediate images are not reused or cached. For chains of operations, this leads to unnecessary memory churn.
- **Objective:** Implement a lightweight pooling mechanism for `BufferedImage` objects to reduce memory footprint and GC pressure.
- **Implementation Details:**
    - Create a `BufferedImagePool` using `SoftReference` or similar.
    - Allow re‑use of intermediate images when dimensions match.
    - Add a `BufferReuse` option in `Scale4jBuilder` to enable/disable pooling.
- **Dependencies:** Task 3 (test coverage) should include tests for pooling behavior.
- **Acceptance Criteria:**
    - Pooling reduces memory allocations for chained operations (verified with benchmarks).
    - No visual artifacts or data corruption introduced.
    - Pool can be disabled for debugging.

#### Task 9: Missing Batch Processing API
- **Priority:** P4
- **Status:** Not Started
- **Issue:** The library processes single images only. No built‑in support for processing collections of images with parallel streams or bulk async operations.
- **Objective:** Add a `Scale4j.batch()` method returning a `BatchProcessor` that can apply the same operation chain to multiple images, optionally parallelized.
- **Implementation Details:**
    - Design `BatchProcessor` interface with methods for synchronous and asynchronous execution.
    - Integrate with `AsyncScale4j` for non‑blocking batch workflows.
    - Provide examples of parallel processing using Java streams.
- **Dependencies:** Task 3 (test coverage) should include batch processing tests.
- **Acceptance Criteria:**
    - Batch API can process a collection of images with the same operation chain.
    - Parallel execution yields throughput improvement.
    - Unit tests verify correctness of batch results.

#### Task 10: No Image Filters / Effects
- **Priority:** P4
- **Status:** Not Started
- **Issue:** Only basic geometric operations (resize, crop, rotate, pad) and watermarks are supported. Common image filters (blur, sharpen, brightness, contrast, grayscale) are missing.
- **Objective:** Create a `filter` package with operations based on `ConvolveOp`, `LookupOp`, and `RescaleOp`, providing builder methods for common filters.
- **Implementation Details:**
    - Implement `BlurOperation`, `SharpenOperation`, `BrightnessOperation`, `ContrastOperation`, `GrayscaleOperation`.
    - Expose them via `Scale4jBuilder` methods (e.g., `.blur(radius)`, `.sharpen()`).
    - Ensure filters can be chained with geometric operations.
- **Dependencies:** Task 3 (test coverage) should include filter tests.
- **Acceptance Criteria:**
    - Each filter produces visually correct results.
    - Filters are composable with existing operations.
    - Unit tests verify filter output against reference implementations.

#### Task 11: Missing Documentation for Extension Development
- **Priority:** P5
- **Status:** Not Started
- **Issue:** The plugin architecture is mentioned in the README but there is no guide on how to write custom operations or format extensions.
- **Objective:** Write an `EXTENSIONS.md` guide explaining how to extend Scale4j with custom operations and image formats.
- **Implementation Details:**
    - Document the `Operation` interface (if exists) or how to add custom `UnaryOperator<BufferedImage>`.
    - Explain registering new image formats via `ImageIO`.
    - Provide step‑by‑step instructions for packaging extensions as separate modules.
- **Dependencies:** None.
- **Acceptance Criteria:**
    - `EXTENSIONS.md` is placed in the project root or `docs/` folder.
    - The guide is clear enough for a third‑party developer to create a simple extension.
    - Examples are included.

#### Task 12: CI/CD Does Not Run Benchmarks
- **Priority:** –
- **Status:** Not Started
- **Issue:** The GitHub Actions workflow only runs unit tests; benchmarks are not executed regularly, so performance regressions may go unnoticed.
- **Objective:** Add a scheduled CI job (nightly) that runs JMH benchmarks and compares results against a baseline, failing if significant degradation occurs.
- **Implementation Details:**
    - Create a new GitHub Actions workflow (`.github/workflows/benchmarks.yml`).
    - Configure it to run on schedule and on demand.
    - Store benchmark history as artifacts.
    - Implement a simple regression detection script.
- **Dependencies:** Task 4 (JMH benchmarks) must be completed first.
- **Acceptance Criteria:**
    - Nightly benchmark job runs without manual intervention.
    - Job fails if performance degrades beyond a configurable threshold.
    - Benchmark artifacts are accessible via GitHub Actions UI.

### Dependencies & Prerequisites
- Tasks 1 and 2 are foundational and already completed; they should be verified before proceeding with other tasks.
- Task 3 (test coverage) is a prerequisite for reliable implementation of Tasks 4‑12.
- Task 4 (benchmarks) depends on Task 3 for stable code.
- Tasks 5 and 6 (metadata and format support) are independent but may share some infrastructure.
- Tasks 7‑12 are feature enhancements that can be developed in parallel after the core is stable.

### Suggested Acceptance Criteria for the Overall List
- All P0 and P1 tasks are completed and validated.
- Test coverage exceeds 80% (line coverage).
- JMH benchmarks are running in CI and provide a performance baseline.
- The library supports WebP and AVIF formats.
- EXIF metadata is preserved for JPEG images.
- Error handling and logging are production‑ready.
- Memory pooling and batch processing APIs are available as optional features.
- Extension documentation is published.

---

## 2. Milestone v1.1.0 (P0/P1)

**Title:** Milestone v1.1.0 – Stable, Performant Core  
**Purpose:** To establish a stable, performant core with measurable performance, addressing the highest‑priority issues.  
**Source:** ROADMAP.md “Milestone v1.1.0” checklist (lines 31‑36).

### Checklist Items

#### Item 1: Resize algorithm replacement
- **Status:** Completed (✅)
- **Description:** Replace `Image.getScaledInstance()` with `AffineTransformOp`. Already done as per Task 1.
- **Verification:** Code inspection and existing unit tests.

#### Item 2: Watermark bug fix
- **Status:** Completed (✅)
- **Description:** Fix Y‑coordinate calculation for top positions. Already done as per Task 2.
- **Verification:** Unit tests in `WatermarkPositionTest`.

#### Item 3: Expand test coverage (>80% line coverage)
- **Status:** In Progress (⬜)
- **Description:** Extend test suite to cover all operations, async, watermarking, I/O, and property‑based tests (Task 3).
- **Dependencies:** Completion of Task 3.
- **Acceptance Criteria:** JaCoCo report shows line coverage ≥80%.

#### Item 4: Implement JMH benchmarks
- **Status:** Not Started (⬜)
- **Description:** Create JMH benchmarks for resize, crop, rotate, pad, async, memory (Task 4).
- **Dependencies:** Completion of Task 4.
- **Acceptance Criteria:** Benchmarks run successfully and produce results.

### Dependencies
- Items 1 and 2 are already satisfied.
- Item 3 is a prerequisite for item 4 because benchmarks need a stable codebase.

### Overall Acceptance Criteria for Milestone v1.1.0
- All four checklist items are marked as completed.
- The library passes all existing and new unit tests.
- Performance benchmarks are established and documented.
- The milestone can be tagged as `v1.1.0` and released.

---

## 3. Milestone v1.2.0 (P2/P3)

**Title:** Milestone v1.2.0 – Enhanced Format Support & Production‑Ready Robustness  
**Purpose:** To add support for modern image formats, improve error handling, and introduce memory optimizations, making the library suitable for production use.  
**Source:** ROADMAP.md “Milestone v1.2.0” checklist (lines 38‑43).

### Checklist Items

#### Item 1: EXIF metadata preservation
- **Status:** Not Started (⬜)
- **Description:** Integrate metadata library to preserve orientation and tags (Task 5).
- **Dependencies:** None.
- **Acceptance Criteria:** EXIF orientation applied automatically; all metadata preserved.

#### Item 2: WebP/AVIF extension modules
- **Status:** Not Started (⬜)
- **Description:** Complete WebP and AVIF extension modules with ImageIO plugins (Task 6).
- **Dependencies:** May rely on metadata library for format‑specific metadata.
- **Acceptance Criteria:** WebP and AVIF images can be loaded, processed, and saved.

#### Item 3: Improved error handling & logging
- **Status:** Not Started (⬜)
- **Description:** Introduce dedicated exception hierarchy and optional SLF4J logging facade (Task 7).
- **Dependencies:** None.
- **Acceptance Criteria:** Exceptions are specific and loggable.

#### Item 4: Memory pooling
- **Status:** Not Started (⬜)
- **Description:** Implement `BufferedImage` pooling / reuse for chained operations (Task 8).
- **Dependencies:** None.
- **Acceptance Criteria:** Pooling reduces memory allocations without affecting correctness.

### Dependencies
- Items 1 and 2 are independent but may share metadata handling.
- Items 3 and 4 are independent of each other.

### Overall Acceptance Criteria for Milestone v1.2.0
- All four checklist items are completed.
- The library supports WebP and AVIF formats with metadata preservation.
- Error handling is consistent and logs are configurable.
- Memory pooling is available as an optional feature.
- The milestone can be tagged as `v1.2.0` and released.

---

## 4. Milestone v1.3.0 (P4)

**Title:** Milestone v1.3.0 – Advanced Features & Ecosystem Growth  
**Purpose:** To deliver advanced features (batch processing, image filters) and improve the ecosystem (extension guide, benchmark CI).  
**Source:** ROADMAP.md “Milestone v1.3.0” checklist (lines 45‑50).

### Checklist Items

#### Item 1: Batch processing API
- **Status:** Not Started (⬜)
- **Description:** Add `Scale4j.batch()` method for parallel processing of image collections (Task 9).
- **Dependencies:** None.
- **Acceptance Criteria:** Batch API works synchronously and asynchronously.

#### Item 2: Image filters / effects
- **Status:** Not Started (⬜)
- **Description:** Create filter package (blur, sharpen, brightness, contrast, grayscale) (Task 10).
- **Dependencies:** None.
- **Acceptance Criteria:** Filters produce expected visual effects.

#### Item 3: Extension development guide
- **Status:** Not Started (⬜)
- **Description:** Write `EXTENSIONS.md` guide for custom operations and formats (Task 11).
- **Dependencies:** None.
- **Acceptance Criteria:** Guide is comprehensive and includes examples.

#### Item 4: Benchmark CI integration
- **Status:** Not Started (⬜)
- **Description:** Add scheduled CI job to run JMH benchmarks and detect regressions (Task 12).
- **Dependencies:** Task 4 (JMH benchmarks) must be completed.
- **Acceptance Criteria:** Nightly benchmark job runs and reports regressions.

### Dependencies
- Item 4 depends on Task 4 (benchmarks) which is part of v1.1.0.
- Items 1‑3 are independent.

### Overall Acceptance Criteria for Milestone v1.3.0
- All four checklist items are completed.
- Batch processing and image filters are available.
- Extension guide is published.
- Benchmark CI is operational.
- The milestone can be tagged as `v1.3.0` and released.

---

## 5. How to Use (Guidelines)

**Title:** How to Use
**Purpose:** To provide users and maintainers with clear instructions on how to interpret and update the ROADMAP.md file.
**Source:** ROADMAP.md “How to Use” bullet list (lines 7‑10).

### Breakdown

- **Each task is listed with its priority, status, and a checkbox.**
  - Explanation: The table columns include priority (P0‑P5), status (Not Started / In Progress / Done), and a visual checkbox (✅ or ⬜) indicating completion.
  - Purpose: Allows quick assessment of progress and priority.

- **Update the status as you work on tasks.**
  - Explanation: As development progresses, the status column should reflect the current state (e.g., change from “Not Started” to “In Progress”).
  - Purpose: Keeps the roadmap current and provides transparency.

- **When a task is completed, check the box and update the status to Done.**
  - Explanation: The checkbox column should be marked with a checkmark (✅) and the status column set to **Done**.
  - Purpose: Clearly indicates which tasks are finished.

- **For more details, refer to the original issue description in `docs/identified‑issues‑tasklist.md`.**
  - Explanation: Each task corresponds to a detailed issue description in the referenced document.
  - Purpose: Ensures that developers have access to full context and implementation guidance.

### Dependencies & Prerequisites
- None; these are meta‑instructions for using the roadmap.

### Acceptance Criteria
- The instructions are clear to a new contributor.
- The roadmap is updated according to these guidelines.

---

## 6. Updating This File (Procedural Steps)

**Title:** Updating This File
**Purpose:** To define a repeatable process for maintaining the ROADMAP.md file as tasks evolve.
**Source:** ROADMAP.md “Updating This File” numbered list (lines 60‑62).

### Breakdown

1. **Edit the status column (Not Started / In Progress / Done).**
   - Explanation: Modify the “Status” column of the task table to reflect the current state.
   - Purpose: Ensures the status is accurate.

2. **Check the checkbox (replace `⬜` with `✅`) when a task is fully completed.**
   - Explanation: Once a task is verified as complete, replace the empty checkbox (`⬜`) with a checked one (`✅`).
   - Purpose: Visual indication of completion.

3. **Add new tasks as they are identified, following the same format.**
   - Explanation: When new issues are discovered, they should be appended to the table with the same column structure (priority, issue description, status, checkbox).
   - Purpose: Keeps the roadmap comprehensive and up‑to‑date.

### Dependencies & Prerequisites
- The roadmap file must be writable by the maintainer.
- New tasks should be documented in `docs/identified‑issues‑tasklist.md` before being added.

### Acceptance Criteria
- The update steps are followed consistently.
- The roadmap remains machine‑readable (table format preserved).

---

## Summary

This documentation provides a detailed, standardized view of each task list present in ROADMAP.md. It can serve as a living reference for developers, product managers, and contributors, enabling clear tracking of progress and understanding of each task's scope, dependencies, and success criteria.

*Document generated from ROADMAP.md and `docs/identified-issues-tasklist.md` on 2026‑02‑17.*