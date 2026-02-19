# Scale4j Development Roadmap

This file tracks the progress of the tasks identified in the technical review (`docs/identified-issues-tasklist.md`). It serves as a lightweight issue tracker for solo development.

## How to Use

- Each task is listed with its priority, status, and a checkbox.
- Update the status as you work on tasks.
- When a task is completed, check the box and update the status to **Done**.
- For more details, refer to the original issue description in `docs/identified-issues-tasklist.md`.

## Progress Summary

**Overall:** 8/12 tasks completed (66.7%)

| Milestone | Progress | Target |
|-----------|----------|--------|
| v1.1.0 (P0/P1) | 3/4 (75%) | Stable, performant core |
| v1.2.0 (P2/P3) | 2/4 (50%) | Enhanced format support & production‑ready robustness |
| v1.3.0 (P4) | 3/4 (75%) | Advanced features & ecosystem growth |

**Current Release:** `v1.1.0-alpha` tagged at `4a1cc10`

## Task List

| # | Milestone | Priority | Issue | Status | Done |
|---|-----------|----------|-------|--------|------|
| 1 | v1.1.0 | P0 | **Resize Algorithm Performance & Quality** – Replace `Image.getScaledInstance()` with `AffineTransformOp`. | **Done** (already using `AffineTransformOp`) | ✅ |
| 2 | v1.1.0 | P0 | **Watermark Positioning Bug** – Fix Y‑coordinate calculation for top positions. | **Done** (verified in `WatermarkPosition.calculate()`) | ✅ |
| 3 | v1.1.0 | P1 | **Inadequate Test Coverage** – Expand test suite for all operations, async, watermarking, I/O, property‑based tests. | **In Progress** (basic tests exist; need more coverage) | ⬜ |
| 4 | v1.1.0 | P1 | **Missing Performance Benchmarks** – Implement JMH benchmarks for resize, crop, rotate, pad, async, memory. | **Done** (JMH benchmarks implemented) | ✅ |
| 5 | v1.2.0 | P2 | **No EXIF Metadata Preservation** – Integrate metadata library (TwelveMonkeys / Apache Sanselan) to preserve orientation and tags. | **Done** (ExifMetadata, auto‑rotation implemented) | ✅ |
| 6 | v1.2.0 | P2 | **Limited Image Format Support** – Complete WebP and AVIF extension modules with ImageIO plugins. | **Not Started** (placeholder modules) | ⬜ |
| 7 | v1.2.0 | P3 | **Error Handling & Logging** – Introduce dedicated exception hierarchy and optional SLF4J logging facade. | **Done** (exception hierarchy implemented) | ✅ |
| 8 | v1.2.0 | P3 | **Memory Inefficiency** – Implement `BufferedImage` pooling / reuse for chained operations. | **Not Started** | ⬜ |
| 9 | v1.3.0 | P4 | **Missing Batch Processing API** – Add `Scale4j.batch()` method for parallel processing of image collections. | **Done** (BatchProcessor implemented) | ✅ |
| 10 | v1.3.0 | P4 | **No Image Filters / Effects** – Create filter package (blur, sharpen, brightness, contrast, grayscale). | **Done** (FilterOperation implemented) | ✅ |
| 11 | v1.3.0 | P5 | **Missing Documentation for Extension Development** – Write `EXTENSIONS.md` guide for custom operations and formats. | **Done** (EXTENSIONS.md exists) | ✅ |
| 12 | v1.3.0 | – | **CI/CD Does Not Run Benchmarks** – Add scheduled CI job to run JMH benchmarks and detect regressions. | **Not Started** | ⬜ |

## Milestones

### Milestone v1.1.0 (P0/P1)
**Progress:** 3/4 (75%) – **Blocked by:** Test coverage

- [x] Resize algorithm replacement (Task 1)
- [x] Watermark bug fix (Task 2)
- [ ] Expand test coverage (>80% line coverage) (Task 3)
- [x] Implement JMH benchmarks (Task 4)

**Target**: Establish a stable, performant core with measurable performance.
**Release:** `v1.1.0-alpha` tagged, full release pending test coverage completion.

### Milestone v1.2.0 (P2/P3)
**Progress:** 2/4 (50%)

- [x] EXIF metadata preservation (Task 5)
- [ ] WebP/AVIF extension modules (Task 6)
- [x] Improved error handling & logging (Task 7)
- [ ] Memory pooling (Task 8)

**Target**: Enhanced format support and production‑ready robustness.
**Next:** Complete format extensions and memory optimization for production readiness.

### Milestone v1.3.0 (P4)
**Progress:** 3/4 (75%)

- [x] Batch processing API (Task 9)
- [x] Image filters / effects (Task 10)
- [x] Extension development guide (Task 11)
- [ ] Benchmark CI integration (Task 12)

**Target**: Advanced features and ecosystem growth.
**Next:** Set up automated benchmark regression detection in CI.

## Notes

- The **Done** tasks have been verified against the current source code.
- The **In Progress** task (test coverage) requires adding property‑based tests and integration tests.
- `v1.1.0-alpha` tag created at `4a1cc10` marking current progress.

## Updating This File

1. Edit the status column (Not Started / In Progress / Done).
2. Check the checkbox (replace `⬜` with `✅`) when a task is fully completed.
3. Add new tasks as they are identified, following the same format.

---

*Last updated: 2026‑02‑19*  
*Based on `docs/identified-issues-tasklist.md` (review date 2026‑02‑14)*