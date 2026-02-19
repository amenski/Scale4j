# Scale4j Extensions Guide

This guide explains how to extend Scale4j with custom operations, image formats, and integration modules.

## Table of Contents

1. [Custom Operations](#custom-operations)
2. [Image Format Extensions](#image-format-extensions)
3. [Packaging as Separate Modules](#packaging-as-separate-modules)
4. [Spring Boot Integration](#spring-boot-integration)

---

## Custom Operations

Scale4j uses Java's `UnaryOperator<BufferedImage>` functional interface for operations. This means any function that takes a `BufferedImage` and returns a `BufferedImage` can be used as an operation.

### Using Lambda Expressions

The simplest way to add a custom operation is using a lambda expression:

```java
import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;

// Create a custom operation using a lambda
UnaryOperator<BufferedImage> myCustomFilter = image -> {
    // Your custom image processing logic here
    BufferedImage result = new BufferedImage(
        image.getWidth(), 
        image.getHeight(), 
        image.getType()
    );
    
    // Example: Invert colors
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int rgb = image.getRGB(x, y);
            int r = 255 - ((rgb >> 16) & 0xFF);
            int g = 255 - ((rgb >> 8) & 0xFF);
            int b = 255 - (rgb & 0xFF);
            result.setRGB(x, y, (r << 16) | (g << 8) | b);
        }
    }
    
    return result;
};

// Use it with Scale4j
BufferedImage result = Scale4j.load(inputFile)
    .resize(800, 600)
    .build(); // Returns a Scale4jBuilder that accepts custom operations

// To add custom operations, use the operations list directly
Scale4jBuilder builder = Scale4j.load(inputFile);
builder.resize(800, 600);
// Add custom operation after built-in operations
UnaryOperator<BufferedImage> finalOp = myCustomFilter;
BufferedImage processed = finalOp.apply(builder.build());
```

### Using the Operations List Directly

For more control, you can work directly with the operations list in `Scale4jBuilder`:

```java
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.function.UnaryOperator;

// Create a box blur operation
public static UnaryOperator<BufferedImage> boxBlur(int radius) {
    return image -> {
        float[] kernel = new float[radius * radius];
        float value = 1.0f / (radius * radius);
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] = value;
        }
        
        Kernel k = new Kernel(radius, radius, kernel);
        ConvolveOp op = new ConvolveOp(k, ConvolveOp.EDGE_ZERO_FILL, null);
        return op.filter(image, null);
    };
}

// Apply custom operation
BufferedImage blurred = Scale4j.load(input)
    .resize(800, 600)
    .build();

blurred = boxBlur(5).apply(blurred);
```

### Creating Reusable Operation Classes

For complex operations, create a dedicated class:

```java
package com.myproject.ops;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.function.UnaryOperator;

/**
 * Custom Gaussian blur operation for Scale4j.
 */
public class GaussianBlurOperation implements UnaryOperator<BufferedImage> {
    
    private final float radius;
    
    public GaussianBlurOperation(float radius) {
        this.radius = radius;
    }
    
    @Override
    public BufferedImage apply(BufferedImage source) {
        // Create Gaussian kernel
        int size = (int) (radius * 2 + 1);
        if (size < 3) size = 3;
        
        float[] kernel = createGaussianKernel(size, radius);
        Kernel k = new Kernel(size, size, kernel);
        
        BufferedImageOp op = new ConvolveOp(k, ConvolveOp.EDGE_ZERO_FILL, null);
        return op.filter(source, null);
    }
    
    private float[] createGaussianKernel(int size, float radius) {
        float[] kernel = new float[size * size];
        float sigma = radius / 3.0f;
        float sum = 0;
        
        int half = size / 2;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int idx = y * size + x;
                float dx = x - half;
                float dy = y - half;
                float value = (float) Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                kernel[idx] = value;
                sum += value;
            }
        }
        
        // Normalize
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }
        
        return kernel;
    }
}
```

### Applying Custom Operations in Batch Processing

```java
import java.util.List;
import java.util.function.UnaryOperator;

// Define custom operation
UnaryOperator<BufferedImage> watermark = image -> {
    // Add watermark logic
    return watermarked;
};

// Use with BatchProcessor
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .parallel(4)
    .execute();

// Apply custom operation to each result
List<BufferedImage> processed = results.stream()
    .map(watermark)
    .collect(Collectors.toList());
```

---

## Image Format Extensions

Scale4j uses Java's ImageIO for reading and writing images. To add support for new formats like WebP or AVIF, you need to:

1. Add the ImageIO plugin dependency
2. Register the reader/writer with ImageIO

### Adding WebP Support (Planned)

Support for WebP format is planned for a future release. The extension module will be added once a stable, well‑maintained ImageIO plugin is available.

### Registering Image Readers and Writers

Create a service provider implementation:

```java
package com.scale4j.ext.webp;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.util.Iterator;

/**
 * Auto-registration of WebP support with ImageIO.
 * This class is loaded via Java ServiceLoader.
 */
public class WebpImageIOPlugin {
    
    public static void register() {
        // The WebP codec jar contains META-INF/services entries
        // that automatically register with ImageIO when present on classpath
        
        // Verify registration
        Iterator<javax.imageio.ImageReader> readers = 
            ImageIO.getImageReadersBySuffix("webp");
        if (readers.hasNext()) {
            System.out.println("WebP reader registered: " + readers.next().getClass().getName());
        }
        
        Iterator<javax.imageio.ImageWriter> writers = 
            ImageIO.getImageWritersBySuffix("webp");
        if (writers.hasNext()) {
            System.out.println("WebP writer registered: " + writers.next().getClass().getName());
        }
    }
}
```

### Creating META-INF Services (Optional)

If the ImageIO plugin doesn't auto-register, create service provider files:

```xml
<!-- src/main/resources/META-INF/services/javax.imageio.spi.ImageReaderSpi -->
<!-- com.scale4j.ext.webp.WebPImageReaderSpi -->

<!-- src/main/resources/META-INF/services/javax.imageio.spi.ImageWriterSpi -->
<!-- com.scale4j.ext.webp.WebPImageWriterSpi -->
```

### Using Custom Format Support

```java
// After adding the extension to your classpath, WebP is automatically supported
BufferedImage image = Scale4j.load("image.webp");

// Save as WebP
Scale4j.load(image)
    .resize(800, 600)
    .toFile(Path.of("output.webp"));

// Check format support
if (Scale4j.isSupportedFormat("webp")) {
    // WebP is available
}
```

### Adding AVIF Support (Planned)

Support for AVIF format is planned for a future release. Native AVIF support is available in Java 21+ via the built‑in ImageIO plugin; for earlier Java versions, a stable third‑party ImageIO plugin will be required.

---

## Packaging as Separate Modules

Scale4j extensions should be packaged as separate Maven modules. Here's the recommended structure:

### Module Structure

```
scale4j/
├── pom.xml                    # Parent POM
├── scale4j-core/              # Core library
├── scale4j-ext-spring-boot/   # Spring Boot starter
│   ├── pom.xml
│   └── src/main/java/...
├── scale4j-bom/               # Bill of Materials
│   └── pom.xml
├── scale4j-examples/          # Example projects
│   └── src/main/java/...
└── scale4j-benchmarks/        # JMH benchmarks
    └── src/main/java/...
```

### Extension Module POM Template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.scale4j</groupId>
        <artifactId>scale4j</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>scale4j-ext-YOURFORMAT</artifactId>
    <packaging>jar</packaging>

    <name>Scale4j YourFormat Extension</name>
    <description>Description of what this extension provides</description>

    <dependencies>
        <dependency>
            <groupId>com.scale4j</groupId>
            <artifactId>scale4j-core</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <!-- Add format-specific dependencies here -->
    </dependencies>
</project>
```

### Best Practices for Extensions

1. **Minimal Dependencies**: Keep extension dependencies minimal
2. **Service Loading**: Use Java ServiceLoader for auto-registration
3. **Graceful Degradation**: Handle missing codecs gracefully
4. **Version Compatibility**: Specify minimum Java version if needed
5. **Documentation**: Include usage examples in the extension module

### Testing Extensions

```java
package com.scale4j.ext.webp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WebpExtensionTest {
    
    @Test
    void shouldSupportWebPFormat() {
        assertTrue(Scale4j.isSupportedFormat("webp"));
    }
    
    @Test
    void shouldLoadWebPImage() throws IOException {
        BufferedImage image = Scale4j.load(testFileWebP);
        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
    }
    
    @Test
    void shouldSaveWebPImage() throws IOException {
        BufferedImage source = Scale4j.load(testFilePng);
        
        Path output = Files.createTempFile("test", ".webp");
        Scale4j.load(source)
            .toFile(output);
        
        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
    }
}
```

---

## Spring Boot Integration

The `scale4j-ext-spring-boot` module provides auto-configuration for Scale4j in Spring Boot applications.

### Adding the Dependency

```xml
<dependency>
    <groupId>com.scale4j</groupId>
    <artifactId>scale4j-ext-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Configuration Properties

```yaml
scale4j:
  default-quality: HIGH
  default-resize-mode: FIT
  max-batch-size: 100
  watermark:
    default-text: "© My Company"
    default-position: BOTTOM_RIGHT
    default-opacity: 0.5
```

### Auto-Configuration

The Spring Boot starter provides:

- `Scale4jProperties` for configuration
- `Scale4jService` bean for programmatic access
- Automatic format detection based on classpath

### Usage Example

```java
@Service
public class ImageService {
    
    @Autowired
    private Scale4j scale4j;
    
    public BufferedImage processUpload(MultipartFile file) throws IOException {
        return scale4j.load(file.getInputStream())
            .resize(800, 600)
            .watermark("Uploaded by User")
            .build();
    }
}
```

---

## Summary

Scale4j is designed to be extensible:

| Extension Type | How to Extend |
|----------------|---------------|
| **Custom Operations** | Use `UnaryOperator<BufferedImage>` lambdas or create dedicated operation classes |
| **Image Formats** | Add ImageIO plugins and let ServiceLoader auto-register |
| **Integrations** | Create separate modules with proper Maven POM and Spring Boot auto-configuration |

For more information, see:
- [Scale4j GitHub](https://github.com/scale4j/scale4j)
- [Java ImageIO Documentation](https://docs.oracle.com/javase/8/docs/api/javax/imageio/package-summary.html)
