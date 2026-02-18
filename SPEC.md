# Scale4j Specification

## Overview
Scale4j is a high-performance Java image processing library that provides a fluent API for common image operations like resize, crop, rotate, and watermark. The library uses Java's built-in `java.awt.image` APIs for maximum compatibility and zero external runtime dependencies.

## Current Status
The library is production-ready with the following features:
- **Resize**: Multiple modes (FIT, FILL, STRETCH, AUTOMATIC) with quality options
- **Crop**: Rectangle-based cropping
- **Rotate**: Free rotation with background color support
- **Pad**: Padding with color customization
- **Watermark**: Text and image watermarks with positioning and opacity
- **Format Support**: PNG, JPEG, GIF, BMP, TIFF, WebP (via TwelveMonkeys)
- **Async**: Asynchronous processing support
- **Metadata**: EXIF metadata preservation and auto-rotation

## Phase 1: Error Handling & Logging (P3)

### Issue
Exceptions are generic (IllegalArgumentException, RuntimeException, IOException) with minimal context. No logging facade or configuration hooks.

### Impact
Difficult to diagnose failures in production; cannot integrate with application monitoring.

### Task
Introduce a dedicated exception hierarchy (Scale4jException, ImageLoadException, ImageProcessException, etc.) with detailed messages and optional cause chains. Provide a Scale4jLogger interface (SLF4J optional) for debug/trace output.

### Implementation Details
1. Create `com.scale4j.exception` package
2. Create base `Scale4jException` class
3. Create specific exceptions: `ImageLoadException`, `ImageProcessException`, `ImageSaveException`
4. Create `Scale4jLogger` interface
5. Add SLF4J support (optional dependency)
6. Update existing code to throw specific exceptions
7. Add logging calls throughout

### Verification Checklist
- [ ] Scale4jException base class created
- [ ] ImageLoadException, ImageProcessException, ImageSaveException created
- [ ] Scale4jLogger interface created
- [ ] SLF4J support added (optional)
- [ ] Existing code updated to use new exceptions
- [ ] Logging added to key operations
- [ ] Compilation passes
- [ ] Tests pass
