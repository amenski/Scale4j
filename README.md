# Scale4j

## Description

Scale4j is a modern image scaling and processing library for Java, designed as a high‑performance, maintainable solution for common image manipulation tasks. It provides a fluent, intuitive API for resize, crop, rotate, pad, and watermark operations, with minimal external dependencies and built‑in support for asynchronous processing using Java's `CompletableFuture`.

The library is built for Java 17+ and leverages modern language features (records, sealed classes, pattern matching) to deliver a clean, maintainable codebase. Its modular architecture allows optional extensions for WebP and AVIF formats, Spring Boot auto‑configuration, and benchmark utilities.

## Features

* **Fluent Builder API**: Chain operations with a readable, type‑safe syntax.
* **Asynchronous Processing**: `CompletableFuture`‑based async operations with configurable executors, including virtual‑thread support.
* **Minimal Dependencies**: Uses SLF4J API for logging and TwelveMonkeys ImageIO for extended format support (JPEG, TIFF, PNG metadata); no image processing libraries required for core functionality.
* **Comprehensive Operations**: Resize (with multiple modes and quality settings), crop, rotate, pad, and watermark (text and image).
* **Enhanced Watermarking**: Text and image watermarks with configurable position, opacity, font, color, shadow, gradient, and tiling.
* **Modern Image Formats**: Optional modules for WebP and AVIF encoding/decoding (requires native libraries).
* **Spring Boot Integration**: Auto‑configuration and starter module for seamless use in Spring applications.
* **Streaming‑Friendly**: Memory‑efficient processing of large images via pluggable streaming API.
* **Extensible Plugin System**: Add custom operations or format support through a simple SPI.
* **Comprehensive Testing**: Full JUnit 5 test suite, property‑based tests, and JMH benchmarks.

## Internal Optimizations

Scale4j uses a **scratch buffer** technique to reduce garbage collection pressure during chained operations. When multiple operations produce images of the same dimensions and type, Scale4j reuses a single transient `BufferedImage` buffer within each builder chain. This eliminates unnecessary allocations for common scenarios like repeated resizes to the same size.

The scratch buffer is:

- **Per‑builder**: Each `Scale4jBuilder` instance maintains its own buffer (not shared across builders or threads).
- **Automatic**: No configuration required—the optimization is always enabled.
- **Safe**: Buffers are cleared before reuse when necessary (e.g., padding operations).

## Installation

### Prerequisites

* Java 17 or higher
* Maven 3.6+ (or Gradle) for building

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.scale4j</groupId>
    <artifactId>scale4j-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Optional extension modules:

```xml
<!-- Spring Boot auto‑configuration -->
<dependency>
    <groupId>com.scale4j</groupId>
    <artifactId>scale4j-ext-spring-boot</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Building from Source

```bash
# Clone the repository
git clone https://github.com/${github-username}/scale4j.git
cd scale4j

# Compile and run tests
mvn clean verify

# Build the JAR files
mvn clean package

# Format code (optional)
mvn com.coveo:fmt-maven-plugin:check
```

## Usage

### Basic Example

```java
import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;

BufferedImage result = Scale4j.load(image)
    .resize(300, 200)
    .mode(ResizeMode.FIT)
    .quality(ResizeQuality.HIGH)
    .build();
```

### Chaining Multiple Operations

```java
BufferedImage result = Scale4j.load(image)
    .resize(800, 600, ResizeMode.FIT)
    .crop(100, 100, 600, 400)
    .rotate(90)
    .pad(10, Color.WHITE)
    .watermark("© 2024")
    .build();
```

### Asynchronous Processing

```java
CompletableFuture<BufferedImage> future = Scale4j.async()
    .load(image)
    .resize(300, 200)
    .mode(ResizeMode.AUTOMATIC)
    .quality(ResizeQuality.HIGH)
    .apply();

BufferedImage result = future.join();
```

### Watermarking

```java
// Text watermark with advanced options
Scale4j.load(image)
    .watermark(TextWatermark.builder()
        .text("© 2024")
        .font("Arial", Font.BOLD, 24)
        .color(Color.WHITE)
        .opacity(0.7f)
        .position(WatermarkPosition.BOTTOM_RIGHT)
        .build())
    .build();

// Image watermark
Scale4j.load(image)
    .watermark(ImageWatermark.builder()
        .image(logoImage)
        .opacity(0.5f)
        .scale(0.25f)
        .position(WatermarkPosition.TOP_LEFT)
        .build())
    .build();
```

### Saving the Processed Image

```java
Scale4j.load(image)
    .resize(800, 600)
    .toFile(Paths.get("output.jpg"), "jpg");
```

### Batch Processing

Scale4j provides a Batch Processing API for applying the same operations to multiple images efficiently with parallel execution support.

#### Basic Usage

```java
import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;
import java.util.Arrays;
import java.util.List;

// Create or load multiple images
List<BufferedImage> images = Arrays.asList(image1, image2, image3);

// Process all images: resize to 100x50 with FIT mode
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .mode(ResizeMode.FIT)
    .execute();
```

#### Parallel Processing

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Process images in parallel using 4 threads
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .parallel(4)
    .execute();

// Or use a custom executor
ExecutorService executor = Executors.newFixedThreadPool(4);
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .executor(executor)
    .execute();
executor.shutdown();
```

#### Async Processing

```java
import java.util.concurrent.CompletableFuture;

// Process images asynchronously - returns list of futures
List<CompletableFuture<BufferedImage>> futures = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .executeAsync();

// Wait for all to complete
List<BufferedImage> results = futures.stream()
    .map(CompletableFuture::join)
    .toList();

// Or use executeAndJoin for a single combined future
CompletableFuture<List<BufferedImage>> future = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .executeAndJoin();
    
List<BufferedImage> results = future.join();
```

#### Chained Operations

```java
// Apply multiple operations to all images
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(800, 600, ResizeMode.FIT)
    .quality(ResizeQuality.HIGH)
    .rotate(90)
    .pad(10)
    .watermark("© 2024")
    .execute();

// With advanced watermark options
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(800, 600)
    .watermark(TextWatermark.builder()
        .text("© 2024 Company")
        .font("Arial", Font.BOLD, 24)
        .color(Color.WHITE)
        .opacity(0.7f)
        .position(WatermarkPosition.BOTTOM_RIGHT)
        .build())
    .execute();
```

#### Loading from Files

```java
import java.io.File;
import java.nio.file.Paths;

List<File> imageFiles = Arrays.asList(
    new File("photos/image1.jpg"),
    new File("photos/image2.png"),
    new File("photos/image3.webp")
);

// Load and process in one step
List<BufferedImage> results = Scale4j.batch()
    .imagesFromFiles(imageFiles)
    .resize(100, 50)
    .execute();

// Save results directly
Scale4j.batch()
    .imagesFromFiles(imageFiles)
    .resize(100, 50)
    .toFiles(path -> Paths.get("output/" + path), "jpg");
```

#### Order Preservation

By default, output order matches input order. Disable for better performance on large batches:

```java
// preserveOrder(false) may return results out of order but is faster
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .preserveOrder(false)
    .execute();
```

#### Error Handling

Errors propagate to the caller. Wrap in try-catch for handling:

```java
try {
    List<BufferedImage> results = Scale4j.batch()
        .images(images)
        .resize(100, 50)
        .execute();
} catch (CompletionException e) {
    // Handle processing error
    // Note: if one image fails, entire batch fails
}
```

#### Performance Tips

| Technique | When to Use |
|-----------|-------------|
| `.parallel(n)` | CPU-bound operations on multi-core systems |
| `.preserveOrder(false)` | Large batches where order doesn't matter |
| Custom executor | Fine-grained control over thread pools |
| `.executeAsync()` | Non-blocking I/O, UI applications |
| Process in chunks | Very large batches to limit memory usage |

The BatchProcessorBuilder supports all Scale4jBuilder operations: `resize`, `scale`, `crop`, `rotate`, `pad`, `watermark` (text and image), with full support for quality settings and modes.

## Configuration

Scale4j is designed to be configured primarily through its fluent API. The library uses sensible defaults; all customization is done programmatically via builder methods.

### Programmatic Configuration

All resize, crop, rotate, pad, and watermark operations are configured via the builder’s methods:

```java
Scale4j.load(image)
    .resize(800, 600, ResizeMode.FIT)
    .quality(ResizeQuality.HIGH)
    .watermark(TextWatermark.builder()
        .opacity(0.7f)
        .build())
    .build();
```

### System Properties (Optional)

Scale4j recognizes the following optional system properties:

| Property | Default | Description |
|----------|---------|-------------|
| `scale4j.cache.enabled` | `true` | Enable in‑memory caching of intermediate image results (experimental). |
| `scale4j.async.executor` | `virtual` | Executor type for async operations (`virtual`, `fixed`, `cached`). |
| `scale4j.async.threads` | `Runtime.getRuntime().availableProcessors()` | Number of threads for fixed‑thread‑pool executor. |

### Spring Boot Auto‑Configuration

When using the `scale4j-ext-spring-boot` module, you can customize behavior via Spring Boot’s `application.properties` (or `application.yml`). Supported properties include:

```properties
# Example Spring Boot configuration
scale4j.resize.quality=HIGH
scale4j.watermark.default-opacity=0.7
scale4j.async.executor=fixed
scale4j.async.threads=4
```

### Format Extensions

Support for modern formats like WebP and AVIF is planned for future releases. Currently, Scale4j supports standard JPEG, PNG, GIF, and BMP formats via Java's built‑in ImageIO.

## Contributing

Contributions are welcome. Please follow these steps:

1. **Fork the repository** on GitHub.
2. **Create a feature branch** (`git checkout -b feature/your-feature`).
3. **Make your changes**, ensuring code style matches the project’s formatting (run `mvn com.coveo:fmt-maven-plugin:format`).
4. **Write or update tests** for your changes.
5. **Run the full test suite** (`mvn clean verify`) to verify no regressions.
6. **Commit your changes** (`git commit -m 'Add some feature'`).
7. **Push to the branch** (`git push origin feature/your-feature`).
8. **Open a Pull Request** with a clear description of the changes.

Please adhere to the [Code of Conduct](CODE_OF_CONDUCT.md) and ensure your contributions are licensed under the Apache License 2.0.

## License

Scale4j is licensed under the **Apache License, Version 2.0**.  
See the [LICENSE](LICENSE) file for the full license text.

---

## Acknowledgments

Scale4j is inspired by the [imgscalr](https://github.com/rkalla/imgscalr) library, which provided a solid foundation for image scaling in Java. This project aims to continue its spirit with a modern, maintainable implementation.
