# Choosing a Java image library — Scale4j vs Thumbnailator vs imgscalr vs plain Java 2D

> Draft for publication (docs page + basis for the launch post). Facts about
> Scale4j are verified against the 1.0.0 codebase. Facts about other libraries
> marked ⚠ should be re-verified against their repos on the day of publishing —
> maintenance status and feature sets change.

Honest summary up front: **all four options below can resize an image
correctly.** They differ in how much of the surrounding work — quality traps,
EXIF orientation, metadata, batching, framework integration — they take off
your plate, and in whether they're still maintained.

## The landscape

| | Plain Java 2D | imgscalr | Thumbnailator | Scale4j |
|---|---|---|---|---|
| Java baseline | any | 5+ ⚠ | 6+ ⚠ | **17+** |
| Actively maintained | n/a (JDK) | ⚠ dormant for years | ⚠ check latest release | yes (1.0.0, 2026) |
| Dependencies | none | none | none | slf4j-api + TwelveMonkeys ImageIO (better JPEG/TIFF handling) |
| High-quality downscale (progressive) | manual | yes (ULTRA_QUALITY) | yes | yes (quality ladder LOW→ULTRA) |
| Resize modes (fit/fill/exact) | manual | fit-based | yes | yes (`AUTOMATIC`, `FIT`, `FILL`, `EXACT`) |
| Crop / rotate / pad | manual | rotate 90° steps only ⚠ | crop, rotate | yes (arbitrary-angle rotate, per-side pad) |
| Filters (grayscale, sepia, blur, sharpen, edge, vignette…) | manual | few ops | limited ⚠ | yes |
| Text + image watermarks | manual | no | image watermark ⚠ | both, with position/opacity/margin builders |
| EXIF auto-orientation | manual | no | thumbnails only ⚠ | yes (`loadWithMetadata().autoRotate()`, all 8 orientations, tag reset) |
| Metadata preservation on save | manual | no | no ⚠ | yes (`toFileWithMetadata`, keeps camera/GPS data) |
| Batch + parallel processing | manual | no | bulk via `Thumbnails.of(...)` | yes (`Scale4j.batch().parallel(n)`) |
| Async API | manual | no | no | yes (`Scale4j.async()`, CompletableFuture) |
| Spring Boot starter | — | no | no | yes (`scale4j-ext-spring-boot`, `Scale4jTemplate`, `scale4j.*` properties) |
| License | — | Apache-2.0 | MIT | Apache-2.0 |

## When to choose which (honestly)

- **Plain Java 2D** — you need exactly one resize, zero dependencies, and
  you're willing to handle the three classic traps yourself: interpolation
  hints, progressive downscaling for >2x reductions, and image-type/alpha
  mismatches.
- **imgscalr** — historically the answer, and its API is pleasantly small.
  But it predates Java 8 and hasn't seen meaningful releases in years ⚠;
  fine for legacy code that already uses it, hard to recommend for new code.
- **Thumbnailator** — the safe, mature choice if you're on Java 8/11 or you
  only need thumbnails. Well-documented, widely deployed.
- **Scale4j** — you're on Java 17+, and you want the surrounding jobs handled:
  EXIF orientation done correctly (all 8 cases, tag reset, metadata kept),
  watermarking, filters, parallel batches, or Spring Boot auto-configuration.

## Performance

Numbers below are from Scale4j's own JMH suite (`scale4j-benchmarks`), run on
an Apple-silicon MacBook, JDK openjdk version "17.0.8" 2023-07-18 LTS (Amazon Corretto-17.0.8.7.1), quick JMH profile
(1 fork, short iterations) — treat as indicative, not lab-grade.

```
Benchmark                           (quality)  (sourceSize)  (targetSize)   Mode  Cnt     Score   Units
ResizeBenchmark.resizeFit             LOW       640x480            300  thrpt    2   17.722          ops/ms
ResizeBenchmark.resizeFit             LOW       640x480            800  thrpt    2    3.409          ops/ms
ResizeBenchmark.resizeFit             LOW       640x480           1600  thrpt    2    0.832          ops/ms
ResizeBenchmark.resizeFit             LOW     1920x1080            100  thrpt    2  109.486          ops/ms
ResizeBenchmark.resizeFit             LOW     1920x1080            300  thrpt    2   27.043          ops/ms
ResizeBenchmark.resizeFit             LOW     1920x1080            800  thrpt    2    4.120          ops/ms
ResizeBenchmark.resizeFit             LOW     1920x1080           1600  thrpt    2    1.103          ops/ms
ResizeBenchmark.resizeFit             LOW     3840x2160            100  thrpt    2   91.622          ops/ms
ResizeBenchmark.resizeFit             LOW     3840x2160            300  thrpt    2   21.892          ops/ms
ResizeBenchmark.resizeFit             LOW     3840x2160            800  thrpt    2    3.995          ops/ms
ResizeBenchmark.resizeFit             LOW     3840x2160           1600  thrpt    2    1.014          ops/ms
ResizeBenchmark.resizeFit          MEDIUM       640x480            100  thrpt    2   25.098          ops/ms
ResizeBenchmark.resizeFit          MEDIUM       640x480            300  thrpt    2    3.558          ops/ms
ResizeBenchmark.resizeFit          MEDIUM       640x480            800  thrpt    2    0.469          ops/ms
ResizeBenchmark.resizeFit          MEDIUM       640x480           1600  thrpt    2    0.133          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     1920x1080            100  thrpt    2   28.552          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     1920x1080            300  thrpt    2    4.653          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     1920x1080            800  thrpt    2    0.661          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     1920x1080           1600  thrpt    2    0.178          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     3840x2160            100  thrpt    2   27.679          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     3840x2160            300  thrpt    2    3.165          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     3840x2160            800  thrpt    2    0.541          ops/ms
ResizeBenchmark.resizeFit          MEDIUM     3840x2160           1600  thrpt    2    0.152          ops/ms
ResizeBenchmark.resizeFit            HIGH       640x480            100  thrpt    2    7.035          ops/ms
ResizeBenchmark.resizeFit            HIGH       640x480            300  thrpt    2    0.769          ops/ms
ResizeBenchmark.resizeFit            HIGH       640x480            800  thrpt    2    0.156          ops/ms
ResizeBenchmark.resizeFit            HIGH       640x480           1600  thrpt    2    0.039          ops/ms
ResizeBenchmark.resizeFit            HIGH     1920x1080            100  thrpt    2   11.429          ops/ms
ResizeBenchmark.resizeFit            HIGH     1920x1080            300  thrpt    2    0.996          ops/ms
ResizeBenchmark.resizeFit            HIGH     1920x1080            800  thrpt    2    0.211          ops/ms
ResizeBenchmark.resizeFit            HIGH     1920x1080           1600  thrpt    2    0.054          ops/ms
ResizeBenchmark.resizeFit            HIGH     3840x2160            100  thrpt    2   10.462          ops/ms
ResizeBenchmark.resizeFit            HIGH     3840x2160            300  thrpt    2    1.225          ops/ms
ResizeBenchmark.resizeFit            HIGH     3840x2160            800  thrpt    2    0.188          ops/ms
ResizeBenchmark.resizeFit            HIGH     3840x2160           1600  thrpt    2    0.052          ops/ms
```

Interpretation guide:
- Moving from LOW to MEDIUM costs roughly 3–4x throughput; stepping from MEDIUM to HIGH costs another 3–6x. For a 4K source downscaled to a 100 px thumbnail, LOW delivers ~91 ops/ms, MEDIUM ~28 ops/ms, and HIGH ~10 ops/ms — choose the quality level that matches your latency budget.
- Output size dominates cost more than source resolution: going from a 100 px to a 1600 px target at LOW quality on a 4K source drops throughput from ~91 to ~1 ops/ms (roughly 90×).

**Cross-library benchmark (planned):** this suite does not yet run
Thumbnailator/imgscalr on the same harness. A comparison module is on the
roadmap; until then this page makes no performance claims about other
libraries.

## Reproduce

```bash
cd scale4j-benchmarks
mvn clean package
java -jar target/scale4j-benchmarks-1.0.0.jar ResizeBenchmark
```
