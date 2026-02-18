# Batch Processing API Examples

The Scale4j Batch Processing API allows you to apply the same operations to multiple images efficiently, with support for parallel processing.

## Basic Usage

```java
import com.scale4j.Scale4j;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

// Create test images
BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
BufferedImage image2 = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
BufferedImage image3 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
List<BufferedImage> images = Arrays.asList(image1, image2, image3);

// Process all images to 100x50
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .execute();

// results contains 3 images, all resized to 100x50
```

## Parallel Processing

```java
// Process images in parallel using 4 threads
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .parallel(4)  // Use 4 parallel threads
    .execute();

// Or use a custom executor
ExecutorService executor = Executors.newFixedThreadPool(4);
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .executor(executor)
    .resize(100, 50)
    .execute();
executor.shutdown();
```

## Async Processing

```java
// Process images asynchronously
List<CompletableFuture<BufferedImage>> futures = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .executeAsync();

// Wait for all to complete
List<BufferedImage> results = new ArrayList<>();
for (CompletableFuture<BufferedImage> future : futures) {
    results.add(future.join());
}

// Or use executeAndJoin for a single future
CompletableFuture<List<BufferedImage>> future = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .executeAndJoin();
    
List<BufferedImage> results = future.join();
```

## Chained Operations

```java
// Apply multiple operations to all images
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .rotate(90)
    .pad(10)
    .watermark("© 2024")
    .execute();
```

## Loading Images from Files

```java
import java.io.File;
import java.util.Arrays;
import java.util.List;

List<File> imageFiles = Arrays.asList(
    new File("image1.jpg"),
    new File("image2.jpg"),
    new File("image3.jpg")
);

List<BufferedImage> results = Scale4j.batch()
    .imagesFromFiles(imageFiles)
    .resize(100, 50)
    .execute();
```

## Order Preservation

By default, order is preserved (output images maintain the same order as input). You can disable this for potentially faster processing:

```java
// Process without preserving order (faster for large batches)
List<BufferedImage> results = Scale4j.batch()
    .images(images)
    .resize(100, 50)
    .preserveOrder(false)  // Images may complete in any order
    .execute();
```

## Available Operations

The BatchProcessorBuilder supports all operations from Scale4jBuilder:

- `resize(width, height)` - Resize all images
- `resize(width, height, mode, quality)` - Resize with mode and quality
- `scale(factor)` - Scale by factor
- `crop(x, y, width, height)` - Crop all images
- `rotate(degrees)` - Rotate all images
- `rotate(degrees, backgroundColor)` - Rotate with background color
- `pad(padding)` - Pad all sides equally
- `pad(padding, color)` - Pad with color
- `pad(top, right, bottom, left)` - Pad each side differently
- `pad(top, right, bottom, left, color)` - Pad with color
- `watermark(text)` - Add text watermark
- `watermark(text, position, opacity)` - Add text watermark with options
- `watermark(watermark)` - Add custom watermark
- `watermark(image, position, opacity)` - Add image watermark

## Performance Considerations

1. **Parallelism**: Use `.parallel(n)` for CPU-bound operations on multi-core systems
2. **Order Preservation**: Disable with `.preserveOrder(false)` for better performance
3. **Executor**: Provide a custom executor for fine-grained control
4. **Memory**: Large batches may require significant memory; consider processing in chunks
5. **Async**: Use async methods for non-blocking I/O operations

## Integration with AsyncScale4j

The batch API can be combined with AsyncScale4j for complex async workflows:

```java
AsyncScale4j async = Scale4j.async();

// Load images asynchronously, then process in batch
CompletableFuture<List<BufferedImage>> future = async.load(file1)
    .thenCombine(async.load(file2), (img1, img2) -> Arrays.asList(img1, img2))
    .thenCompose(images -> Scale4j.batch(images)
        .resize(100, 50)
        .executeAndJoin());
```

## Error Handling

Errors during batch processing are propagated:

```java
try {
    List<BufferedImage> results = Scale4j.batch()
        .images(images)
        .resize(100, 50)
        .execute();
} catch (Exception e) {
    // Handle processing errors
    // Note: If one image fails, the entire batch fails
}
```

For more robust error handling, consider processing images individually or implementing a custom error handler.