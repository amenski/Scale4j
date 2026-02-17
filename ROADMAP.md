# Scale4j Development Roadmap

This file tracks the progress of the tasks identified in the technical review (`docs/identified-issues-tasklist.md`). It serves as a lightweight issue tracker for solo development.

## How to Use

- Each task is listed with its priority, status, and a checkbox.
- Update the status as you work on tasks.
- When a task is completed, check the box and update the status to **Done**.
- For more details, refer to the original issue description in `docs/identified-issues-tasklist.md`.

## Task List

| # | Priority | Issue | Status | Done |
|---|----------|-------|--------|------|
| 1 | P0 | **Resize Algorithm Performance & Quality** – Replace `Image.getScaledInstance()` with `AffineTransformOp`. | **Done** (already using `AffineTransformOp`) | ✅ |
| 2 | P0 | **Watermark Positioning Bug** – Fix Y‑coordinate calculation for top positions. | **Done** (verified in `WatermarkPosition.calculate()`) | ✅ |
| 3 | P1 | **Inadequate Test Coverage** – Expand test suite for all operations, async, watermarking, I/O, property‑based tests. | **In Progress** (basic tests exist; need more coverage) | ⬜ |
| 4 | P1 | **Missing Performance Benchmarks** – Implement JMH benchmarks for resize, crop, rotate, pad, async, memory. | **Not Started** (benchmark module empty) | ⬜ |
| 5 | P2 | **No EXIF Metadata Preservation** – Integrate metadata library (TwelveMonkeys / Apache Sanselan) to preserve orientation and tags. | **Not Started** | ⬜ |
| 6 | P2 | **Limited Image Format Support** – Complete WebP and AVIF extension modules with ImageIO plugins. | **Not Started** (placeholder modules) | ⬜ |
| 7 | P3 | **Error Handling & Logging** – Introduce dedicated exception hierarchy and optional SLF4J logging facade. | **Not Started** | ⬜ |
| 8 | P3 | **Memory Inefficiency** – Implement `BufferedImage` pooling / reuse for chained operations. | **Not Started** | ⬜ |
| 9 | P4 | **Missing Batch Processing API** – Add `Scale4j.batch()` method for parallel processing of image collections. | **Not Started** | ⬜ |
| 10 | P4 | **No Image Filters / Effects** – Create filter package (blur, sharpen, brightness, contrast, grayscale). | **Not Started** | ⬜ |
| 11 | P5 | **Missing Documentation for Extension Development** – Write `EXTENSIONS.md` guide for custom operations and formats. | **Not Started** | ⬜ |
| 12 | – | **CI/CD Does Not Run Benchmarks** – Add scheduled CI job to run JMH benchmarks and detect regressions. | **Not Started** | ⬜ |

## Milestones

### Milestone v1.1.0 (P0/P1)
- [x] Resize algorithm replacement
- [x] Watermark bug fix
- [ ] Expand test coverage (>80% line coverage)
- [ ] Implement JMH benchmarks
- **Target**: Establish a stable, performant core with measurable performance.

### Milestone v1.2.0 (P2/P3)
- [ ] EXIF metadata preservation
- [ ] WebP/AVIF extension modules
- [ ] Improved error handling & logging
- [ ] Memory pooling
- **Target**: Enhanced format support and production‑ready robustness.

### Milestone v1.3.0 (P4)
- [ ] Batch processing API
- [ ] Image filters / effects
- [ ] Extension development guide
- [ ] Benchmark CI integration
- **Target**: Advanced features and ecosystem growth.

## Notes

- The **Done** tasks have been verified against the current source code.
- The **In Progress** task (test coverage) requires adding property‑based tests and integration tests.
- To keep this file up‑to‑date, run the verification script (`scripts/verify‑progress.sh`) if available.

## Updating This File

1. Edit the status column (Not Started / In Progress / Done).
2. Check the checkbox (replace `⬜` with `✅`) when a task is fully completed.
3. Add new tasks as they are identified, following the same format.

---

*Last updated: 2026‑02‑17*  
*Based on `docs/identified-issues-tasklist.md` (review date 2026‑02‑14)*