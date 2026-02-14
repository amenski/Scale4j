# Scale4j

Scale4j is a modern, actively maintained image scaling and processing library for Java.

## Motivation

Scale4j was inspired by the [imgscalr](https://github.com/thebuzzmedia/imgscalr) library. While imgscalr was an excellent library for its time, it has not been actively maintained for several years with open PRs going unanswered.

Scale4j is a **complete rewrite** that keeps the core **concepts and patterns** from imgscalr while bringing:

- **Modern Java** - Built for Java 17+ with latest language features
- **Active Development** - Regular updates and maintenance
- **Clean Architecture** - No legacy code, no backward compatibility constraints
- **Enhanced Features** - Better APIs, async support, and extensibility

If you're looking for an imgscalr alternative, Scale4j is the solution.

## Features

- **Fluent API** - Chain operations with a readable, intuitive builder pattern
- **Async Support** - CompletableFuture-based async processing with virtual threads
- **Modern Java** - Built for Java 17+ with latest language features
- **Zero Dependencies** - Pure Java AWT, no external libraries required
- **Extensible** - Plugin architecture for custom operations and format support

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.scale4j</groupId>
    <artifactId>scale4j-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;

BufferedImage result = Scale4j.load(image)
    .resize(300, 200)
    .mode(ResizeMode.FIT)
    .build();
```

### Chaining Operations

```java
BufferedImage result = Scale4j.load(image)
    .resize(800, 600, ResizeMode.FIT)
    .crop(100, 100, 600, 400)
    .rotate(90)
    .pad(10, Color.WHITE)
    .watermark("© 2024")
    .build();
```

### Async Processing

```java
CompletableFuture<BufferedImage> future = Scale4j.async()
    .load(image)
    .resize(300, 200)
    .mode(ResizeMode.AUTOMATIC)
    .quality(ResizeQuality.HIGH)
    .apply();

BufferedImage result = future.join();
```

### Watermarks

```java
// Text watermark
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

## Modules

| Module | Description |
|--------|-------------|
| `scale4j-core` | Core library with resize, crop, rotate, pad, and watermark operations |
| `scale4j-ext-webp` | WebP format support |
| `scale4j-ext-avif` | AVIF format support (Java 21+) |
| `scale4j-ext-spring-boot` | Spring Boot auto-configuration |
| `scale4j-examples` | Example projects |
| `scale4j-benchmarks` | JMH performance benchmarks |

## Requirements

- Java 17 or higher
- Maven 3.6+

## Building

```bash
# Compile
mvn clean compile

# Run tests
mvn clean test

# Build JAR
mvn clean package

# Format code
mvn com.coveo:fmt-maven-plugin:check
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests and formatting checks
5. Submit a pull request

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

## Acknowledgments

Scale4j is inspired by the imgscalr library, providing a modern, actively maintained alternative.
