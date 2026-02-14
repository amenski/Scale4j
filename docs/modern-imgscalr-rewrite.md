# Modern imgscalr Rewrite Plan

## Overview

Create a modern, actively maintained fork of imgscalr with updated Java versions, modern APIs, and enhanced features while maintaining backward compatibility patterns.

## Current State Analysis

### Existing Architecture
- **Core**: `Scalr.java` - Main image manipulation library (resize, crop, pad, rotate, apply)
- **Builder**: `ScalrBuilder.java` - Fluent API for chained operations
- **Async**: `AsyncScalr.java` - Parallel image processing
- **Watermark**: `WaterMark.java` - Text and image watermarks with 9 position options
- **Java Target**: Java 8
- **Dependencies**: Minimal (JUnit 4.10 only)
- **License**: Apache License 2.0

### Pain Points to Address
1. No modern Java features (var, records, sealed classes)
2. Outdated build configuration
3. Limited async API (no CompletableFuture)
4. Basic watermarking (no rotation, gradients, shadows)
5. No modern image format support (WebP, AVIF)
6. No streaming API for large images
7. Limited extensibility

---

## Proposed Modern Architecture

### Module Structure - Scale4j

```
scale4j/
├── pom.xml                           # Parent POM (version management)
├── LICENSE                           # Apache 2.0
├── README.md                         # Project overview
├── .gitignore
├── .github/
│   └── workflows/
│       ├── ci.yml                    # CI pipeline
│       └── release.yml               # Release pipeline
├── .editorconfig                      # IDE consistency
├── checkstyle.xml                     # Code style rules
├── spotless/
│   └── license-header.txt             # License header for files
├── scale4j-core/                      # Core library module
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/scale4j/
│   │   │   │   ├── Scale4j.java           # Main facade class
│   │   │   │   ├── Scale4jBuilder.java    # Fluent builder API
│   │   │   │   ├── AsyncScale4j.java      # CompletableFuture async
│   │   │   │   ├── ImageLoader.java       # Load images (Files, URLs, streams)
│   │   │   │   ├── ImageSaver.java        # Save images (Files, streams)
│   │   │   │   ├── Operation.java         # Operation interface
│   │   │   │   ├── config/
│   │   │   │   │   ├── Scale4jConfig.java       # Global config (records)
│   │   │   │   │   ├── ResizeConfig.java
│   │   │   │   │   ├── CropConfig.java
│   │   │   │   │   ├── RotateConfig.java
│   │   │   │   │   ├── PadConfig.java
│   │   │   │   │   └── ApplyConfig.java
│   │   │   │   ├── ops/                      # Operation implementations
│   │   │   │   │   ├── ResizeOperation.java
│   │   │   │   │   ├── CropOperation.java
│   │   │   │   │   ├── PadOperation.java
│   │   │   │   │   ├── RotateOperation.java
│   │   │   │   │   ├── ApplyOperation.java
│   │   │   │   │   └── GrayscaleOperation.java
│   │   │   │   ├── watermark/                # Enhanced watermarks
│   │   │   │   │   ├── Watermark.java       # Base watermark
│   │   │   │   │   ├── TextWatermark.java   # Text watermarks
│   │   │   │   │   ├── ImageWatermark.java # Image watermarks
│   │   │   │   │   ├── WatermarkBuilder.java
│   │   │   │   │   ├── WatermarkPosition.java    # Position enum (9 positions)
│   │   │   │   │   ├── WatermarkOptions.java    # Common options record
│   │   │   │   │   ├── TextWatermarkOptions.java
│   │   │   │   │   └── effects/                  # Watermark effects
│   │   │   │   │       ├── ShadowEffect.java
│   │   │   │   │       ├── GradientEffect.java
│   │   │   │   │       └── RotationEffect.java
│   │   │   │   ├── buffer/                  # Streaming API
│   │   │   │   │   ├── ScalrInputStream.java
│   │   │   │   │   ├── ScalrOutputStream.java
│   │   │   │   │   └── StreamingConfig.java
│   │   │   │   ├── plugin/                  # Plugin architecture
│   │   │   │   │   ├── Scale4jPlugin.java        # Plugin interface
│   │   │   │   │   ├── PluginRegistry.java
│   │   │   │   │   ├── BuiltinPlugins.java
│   │   │   │   │   └── PluginDescriptor.java
│   │   │   │   ├── types/                   # Type definitions
│   │   │   │   │   ├── ResizeMode.java     # AUTOMATIC, FIT, FILL, EXACT
│   │   │   │   │   ├── ResizeQuality.java  # LOW, MEDIUM, HIGH, ULTRA
│   │   │   │   │   ├── Alignment.java      # TOP_LEFT, CENTER, etc.
│   │   │   │   │   └── ImageType.java       # RGB, ARGB, Grayscale
│   │   │   │   ├── util/                    # Utilities
│   │   │   │   │   ├── ImageUtils.java
│   │   │   │   │   ├── MemoryUtils.java
│   │   │   │   │   └── ValidationUtils.java
│   │   │   │   └── exception/               # Exception handling
│   │   │   │       ├── Scale4jException.java
│   │   │   │       ├── ImageLoadException.java
│   │   │   │       ├── ImageSaveException.java
│   │   │   │       └── OperationException.java
│   │   │   └── resources/
│   │   │       └── META-INF/
│   │   │           └── services/
│   │   │               └── com.scale4j.plugin.Scale4jPlugin
│   │   └── test/
│   │       ├── java/com/scale4j/
│   │       │   ├── Scale4jTest.java
│   │       │   ├── Scale4jBuilderTest.java
│   │       │   ├── AsyncScale4jTest.java
│   │       │   ├── watermark/
│   │       │   │   ├── TextWatermarkTest.java
│   │       │   │   └── ImageWatermarkTest.java
│   │       │   └── ops/
│   │       │       ├── ResizeOperationTest.java
│   │       │       └── CropOperationTest.java
│   │       └── resources/
│   │           ├── test-images/             # Test images
│   │           └── expected-output/        # Expected test outputs
│   └── src/
├── scale4j-ext-webp/               # Optional WebP extension
│   ├── pom.xml
│   └── src/
├── scale4j-ext-avif/               # Optional AVIF extension
│   ├── pom.xml
│   └── src/
├── scale4j-ext-svg/                # Optional SVG extension
│   ├── pom.xml
│   └── src/
├── scale4j-bom/                    # Bill of Materials (for Maven Central)
│   ├── pom.xml
│   └── src/
├── scale4j-examples/               # Example projects
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/scale4j/examples/
│       │   │   ├── BasicUsage.java
│       │   │   ├── WatermarkExample.java
│       │   │   ├── AsyncExample.java
│       │   │   └── StreamingExample.java
│       │   └── resources/
│       └── test/
├── scale4j-benchmarks/            # JMH benchmarks
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   └── java/com/scale4j/benchmarks/
│       │       ├── ResizeBenchmark.java
│       │       ├── WatermarkBenchmark.java
│       │       └── AsyncBenchmark.java
│       └── test/
└── docs/                           # Documentation
    ├── index.md
    ├── getting-started.md
    ├── api/
    │   ├── Scale4j.md
    │   ├── Builder.md
    │   ├── Watermark.md
    │   └── Async.md
    ├── migration-guide.md          # From imgscalr
    └── configuration.md
```
```

---

## Key Improvements

### 1. Java Version Upgrade
- **Target**: Java 17 LTS minimum, Java 21 LTS recommended
- **Modern Features**:
  - Records for immutable data (Config classes)
  - Sealed classes for type-safe operations
  - Pattern matching in switch expressions
  - Text blocks for documentation

### 2. Enhanced Builder Pattern
```java
// Current (imgscalr-style)
Scalr.resize(img, Scalr.Mode.AUTOMATIC, 300, 200);

// Modern approach
ImageProcessor processor = Scalr.builder()
    .resize(300, 200)
    .mode(ResizeMode.FIT)
    .quality(0.9f)
    .crop(50, 50, 200, 200)
    .pad(10, Color.WHITE)
    .rotate(90)
    .watermark(TextWatermark.of("© 2024")
        .position(CENTER)
        .opacity(0.5f)
        .font("Arial", 24))
    .build();

BufferedImage result = processor.apply(image);
```

### 3. CompletableFuture Async API
```java
// Modern async with CompletableFuture
CompletableFuture<BufferedImage> future = Scalr.async()
    .load(inputFile)
    .thenResize(800, 600)
    .thenApply(GRAYSCALE)
    .thenWatermark(textWatermark)
    .toFile(outputFile);

// With explicit executor
ExecutorService executor = Executors.newFixedThreadPool(4);
CompletableFuture<BufferedImage> future = Scalr.async(executor)
    .load(image)
    .thenResize(300, 200)
    .process();
```

### 4. Enhanced Watermark Features
```java
// New watermark capabilities
TextWatermark watermark = TextWatermark.builder()
    .text("© 2024 MyApp")
    .font("Arial", Font.BOLD, 24)
    .color(Color.WHITE)
    .background(new Color(0, 0, 0, 128))  // Semi-transparent background
    .shadow(new Shadow(5, 5, Color.BLACK, 0.5f))  // Drop shadow
    .rotation(-15)  // Text rotation
    .gradient(new GradientPaint(0, 0, Color.RED, 100, 0, Color.BLUE))  // Gradient fill
    .tiled(true)  // Tile across image
    .position(CENTER)
    .margin(10)
    .build();

// Image watermark with rotation
ImageWatermark imageWatermark = ImageWatermark.builder()
    .image(logoImage)
    .opacity(0.3f)
    .rotation(-10)
    .scale(0.5f)  // Scale relative to base image
    .position(BOTTOM_RIGHT)
    .margin(20)
    .build();
```

### 5. Modern Image Format Support
```java
// WebP support (with optional dependency)
Scalr.registerFormat("webp", "image/webp", WebpImageReader.class);

// AVIF support (Java 21+ with imageio-avif)
Scalr.registerFormat("avif", "image/avif", AvifImageReader.class);

// Usage
BufferedImage img = Scalr.load("photo.webp");
Scalr.save(img).asWebp("output.webp").withQuality(0.95f);
```

### 6. Memory-Efficient Streaming API
```java
// Process large images without loading fully into memory
try (ScalrInputStream in = new ScalrInputStream(new FileInputStream("large.jpg"));
     ScalrOutputStream out = new ScalrOutputStream(new FileOutputStream("output.jpg"))) {
    
    in.process(img -> 
        Scalr.stream()
            .resize(1920, 1080)
            .mode(ResizeMode.FIT)
            .apply(img, out)
    );
}
```

### 7. Plugin Architecture
```java
// Register custom operations as plugins
Scalr.registerPlugin(new CustomFilterPlugin());

// Plugin interface
public interface ScalrPlugin {
    String getName();
    String getVersion();
    void register(Scalr scalr);
    void deregister(Scalr scalr);
}

// Built-in plugin system for filters
public class FilterPlugin implements ScalrPlugin {
    public void register(Scalr scalr) {
        scalr.registerOperation("blur", new BlurOperation());
        scalr.registerOperation("sharpen", new SharpenOperation());
        scalr.registerOperation("edge", new EdgeDetection());
    }
}
```

### Key Classes and Interfaces Design

#### 1. Main Facade - Scale4j.java
```java
package com.scale4j;

/**
 * Main entry point for the Scale4j image processing library.
 * Provides a fluent API for image manipulation operations.
 */
public final class Scale4j {
    
    private Scale4j() {
        // Utility class - prevent instantiation
    }
    
    // Static factory methods
    public static Scale4jBuilder load(BufferedImage image) { ... }
    public static Scale4jBuilder load(File file) throws IOException { ... }
    public static Scale4jBuilder load(InputStream stream) throws IOException { ... }
    public static Scale4jBuilder load(Path path) throws IOException { ... }
    public static Scale4jBuilder load(URL url) throws IOException { ... }
    
    // Async entry point
    public static AsyncScale4j async() { ... }
    public static AsyncScale4j async(ExecutorService executor) { ... }
    
    // Utility methods
    public static boolean isSupportedFormat(String format) { ... }
    public static Set<String> getSupportedFormats() { ... }
    public static void registerFormat(String extension, String mimeType, ...) { ... }
}
```

#### 2. Fluent Builder - Scale4jBuilder.java
```java
package com.scale4j;

/**
 * Fluent builder for composing image processing operations.
 * All methods return the builder for method chaining.
 */
public final class Scale4jBuilder {
    
    private final BufferedImage sourceImage;
    private final List<Operation> operations = new ArrayList<>();
    private ResizeConfig pendingResize;
    private CropConfig pendingCrop;
    
    // Resize operations
    public Scale4jBuilder resize(int targetWidth, int targetHeight) { ... }
    public Scale4jBuilder resize(int targetWidth, int targetHeight, ResizeMode mode) { ... }
    public Scale4jBuilder resize(int targetWidth, int targetHeight, ResizeQuality quality) { ... }
    public Scale4jBuilder scale(double factor) { ... }
    public Scale4jBuilder fit(int maxWidth, int maxHeight) { ... }
    public Scale4jBuilder fill(int width, int height) { ... }
    
    // Crop operations
    public Scale4jBuilder crop(int x, int y, int width, int height) { ... }
    public Scale4jBuilder crop(Rectangle2D region) { ... }
    public Scale4jBuilder cropToAspectRatio(double aspectRatio) { ... }
    
    // Rotate operations
    public Scale4jBuilder rotate(double degrees) { ... }
    public Scale4jBuilder rotate(double degrees, Color background) { ... }
    public Scale4jBuilder rotate90Clockwise() { ... }
    public Scale4jBuilder rotate90CounterClockwise() { ... }
    public Scale4jBuilder rotate180() { ... }
    
    // Pad operations
    public Scale4jBuilder pad(int padding) { ... }
    public Scale4jBuilder pad(int padding, Color color) { ... }
    public Scale4jBuilder pad(int top, int right, int bottom, int left, Color color) { ... }
    
    // Watermark operations
    public Scale4jBuilder watermark(TextWatermark watermark) { ... }
    public Scale4jBuilder watermark(ImageWatermark watermark) { ... }
    public Scale4jBuilder watermark(Watermark watermark) { ... }
    
    // Color operations
    public Scale4jBuilder grayscale() { ... }
    public Scale4jBuilder sepia() { ... }
    public Scale4jBuilder adjustBrightness(float factor) { ... }
    public Scale4jBuilder adjustContrast(float factor) { ... }
    public Scale4jBuilder adjustSaturation(float factor) { ... }
    public Scale4jBuilder invertColors() { ... }
    
    // Blur/Sharpen operations
    public Scale4jBuilder blur() { ... }
    public Scale4jBuilder blur(int radius) { ... }
    public Scale4jBuilder sharpen() { ... }
    public Scale4jBuilder sharpen(float amount) { ... }
    
    // Output operations
    public BufferedImage build() { ... }
    public void toFile(File output) throws IOException { ... }
    public void toFile(String path) throws IOException { ... }
    public void toOutputStream(OutputStream output, String format) throws IOException { ... }
    public byte[] toByteArray(String format) throws IOException { ... }
}
```

#### 3. Async API - AsyncScale4j.java
```java
package com.scale4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Asynchronous image processing using CompletableFuture.
 */
public final class AsyncScale4j {
    
    private final ExecutorService executor;
    private final List<CompletableFuture<BufferedImage>> pipeline = new ArrayList<>();
    
    private AsyncScale4j(ExecutorService executor) {
        this.executor = executor;
    }
    
    // Factory methods
    public static AsyncScale4j async() {
        return new AsyncScale4j(Executors.newVirtualThreadPerTaskExecutor());
    }
    
    public static AsyncScale4j async(ExecutorService executor) {
        return new AsyncScale4j(executor);
    }
    
    // Load operations - starts the pipeline
    public CompletableFuture<BufferedImage> load(BufferedImage image) {
        return CompletableFuture.completedFuture(image);
    }
    
    // Operation chain methods
    public AsyncScale4j thenResize(int width, int height) {
        pipeline.add(lastFuture().thenApplyAsync(img -> ops.resize(img, width, height), executor));
        return this;
    }
    
    public AsyncScale4j thenResize(int width, int height, ResizeMode mode) { ... }
    public AsyncScale4j thenCrop(int x, int y, int w, int h) { ... }
    public AsyncScale4j thenRotate(double degrees) { ... }
    public AsyncScale4j thenWatermark(TextWatermark watermark) { ... }
    public AsyncScale4j thenWatermark(ImageWatermark watermark) { ... }
    public AsyncScale4j thenGrayscale() { ... }
    
    // Terminal operations
    public CompletableFuture<BufferedImage> process() { ... }
    public CompletableFuture<Void> toFile(File output) { ... }
    public CompletableFuture<Void> toFile(Path path) { ... }
    public CompletableFuture<byte[]> toByteArray(String format) { ... }
    
    private CompletableFuture<BufferedImage> lastFuture() {
        return pipeline.isEmpty() 
            ? CompletableFuture.completedFuture(null) 
            : pipeline.get(pipeline.size() - 1);
    }
}
```

#### 4. Watermark Classes - Enhanced Design
```java
package com.scale4j.watermark;

// Base watermark interface
public interface Watermark {
    Position getPosition();
    float getOpacity();
    void apply(BufferedImage target);
}

// Text watermark with full options
public record TextWatermark(
    String text,
    Font font,
    Color color,
    WatermarkPosition position,
    float opacity,
    Color background,
    ShadowEffect shadow,
    GradientEffect gradient,
    RotationEffect rotation,
    int margin,
    boolean tiled
) implements Watermark {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String text = "";
        private Font font = new Font("Arial", Font.PLAIN, 24);
        private Color color = Color.WHITE;
        private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;
        private float opacity = 0.5f;
        private Color background = null;
        private ShadowEffect shadow = null;
        private GradientEffect gradient = null;
        private RotationEffect rotation = null;
        private int margin = 10;
        private boolean tiled = false;
        
        public Builder text(String text) { ... }
        public Builder font(Font font) { ... }
        public Builder font(String family, int style, int size) { ... }
        public Builder color(Color color) { ... }
        public Builder position(WatermarkPosition position) { ... }
        public Builder opacity(float opacity) { ... }
        public Builder background(Color background) { ... }
        public Builder shadow(ShadowEffect shadow) { ... }
        public Builder gradient(GradientEffect gradient) { ... }
        public Builder rotation(RotationEffect rotation) { ... }
        public Builder margin(int margin) { ... }
        public Builder tiled(boolean tiled) { ... }
        public TextWatermark build() { ... }
    }
}

// Shadow effect for watermarks
public record ShadowEffect(
    int offsetX,
    int offsetY,
    float blurRadius,
    Color color,
    float opacity
) {
    public static ShadowEffect create(int x, int y, Color color) {
        return new ShadowEffect(x, y, 3, color, 0.5f);
    }
}

// Gradient text effect
public record GradientEffect(
    Paint paint,
    boolean cyclic,
    boolean useFractionalMetrics
) {
    public static GradientEffect horizontal(Color c1, Color c2) {
        return new GradientEffect(
            new LinearGradientPaint(0, 0, 100, 0, 
                new float[]{0, 1},
                new Color[]{c1, c2}),
            false, false);
    }
    
    public static GradientEffect vertical(Color c1, Color c2) { ... }
    
    public static GradientEffect diagonal(Color c1, Color c2) { ... }
}
```

---

## Detailed Implementation Tasks

### Phase 1: Foundation
1. Create new Maven project structure
2. Set up Java 17+ build configuration
3. Configure code formatting (Spotless/Google Java Format)
4. Set up CI/CD (GitHub Actions)
5. Migrate core Scalr.java functionality

### Phase 2: Core API Enhancement
1. Redesign ScalrBuilder with method chaining
2. Add operation enums/records for type safety
3. Implement Config classes using records
4. Add comprehensive Javadoc
5. Create migration guide from imgscalr

### Phase 3: Watermark Improvements
1. Enhance WaterMark class with new features
2. Add WatermarkBuilder with fluent API
3. Support gradient text, shadows, rotation
4. Add tiled watermarks
5. Multiple watermark support

### Phase 4: Async & Performance
1. Implement CompletableFuture-based AsyncScalr
2. Add configurable thread pool
3. Create streaming API for large images
4. Add memory-efficient modes
5. Create benchmarks

### Phase 5: Extensions
1. Add WebP support (optional dependency)
2. Add AVIF support (Java 21+)
3. Create extension mechanism
4. Document extension development

### Phase 6: Testing & Quality
1. Migrate to JUnit 5
2. Add property-based testing
3. Create performance benchmarks
4. Set up code coverage (JaCoCo)
5. Add mutation testing (PIT)

### Phase 7: Distribution
1. Configure Maven Central publishing
2. Set up GPG signing
3. Create Maven BOM for version management
4. Document migration from imgscalr
5. Create example projects

---

## Build Configuration (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.modern-imgscalr</groupId>
    <artifactId>imgscalr-core</artifactId>
    <version>5.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <name>Modern imgscalr</name>
    <description>Modern, actively maintained image scaling library</description>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.0</junit.version>
    </properties>
    
    <dependencies>
        <!-- Core - No dependencies required -->
        
        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Optional: WebP support -->
        <dependency>
            <groupId>org.sejda</groupId>
            <artifactId>libwebp</artifactId>
            <version>1.1.0</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.30</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
            
            <plugin>
                <groupId>com.coveo</groupId>
                <artifactId>fmt-maven-plugin</artifactId>
                <version>2.9</version>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.6.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## GitHub Actions CI/CD

```yaml
name: CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java: [17, 21]
    steps:
      - uses: actions/checkout@v3
      - name: Set up Java ${{ matrix.java }}
        uses: actions/setup-java@v3
        with:
          java-version: ${{ matrix.java }}
          distribution: temurin
      - name: Build with Maven
        run: mvn clean verify
      - name: Code formatting check
        run: mvn com.coveo:fmt-maven-plugin:check
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

---

## Migration Guide Plan

1. Side-by-side API comparison
2. Automated migration script suggestions
3. Deprecation warnings for old patterns
4. Feature parity checklist
5. Performance comparison benchmarks

---

## Next Steps

1. **Approve this plan** - Review and confirm architectural decisions
2. **Start Phase 1** - Create new project structure
3. **Set up CI/CD** - Configure GitHub Actions
4. **Migrate core API** - Begin with Scalr.java modernization
