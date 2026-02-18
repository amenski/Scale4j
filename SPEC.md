## Issue
Scale4j does not preserve EXIF (or other) metadata when loading, processing, and saving images. This is a critical requirement for photography applications.

## Impact
Loss of orientation, camera settings, geotags, etc., making the library unsuitable for many real‑world image pipelines.

## Task
Integrate a lightweight metadata library (e.g., Apache Sanselan, TwelveMonkeys ImageIO) to read metadata from source images and re‑attach it to processed outputs. At a minimum, preserve orientation tags and apply automatic rotation.

## Implementation Details
1. Add TwelveMonkeys ImageIO dependency to scale4j-core/pom.xml
2. Create `com.scale4j.metadata` package with:
   - `ExifMetadata` class to read/write EXIF data
   - `ExifOrientation` enum for orientation values
3. Update `ImageLoader` to read and store metadata
4. Update `ImageSaver` to write metadata to output
5. Add automatic rotation based on orientation tag
6. Add unit tests for metadata preservation

## Verification Checklist
- [ ] TwelveMonkeys ImageIO dependency added to scale4j-core
- [ ] ExifMetadata class created with read/write methods
- [ ] ExifOrientation enum with all 8 orientation values
- [ ] ImageLoader preserves metadata when loading
- [ ] ImageSaver writes metadata to output
- [ ] Automatic rotation applied based on orientation
- [ ] Unit tests for metadata preservation
- [ ] Unit tests for automatic rotation
- [ ] Compilation passes (mvn compile)
- [ ] Tests pass (mvn test)