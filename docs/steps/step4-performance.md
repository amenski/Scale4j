# Step 4: Assess performance characteristics

- Resize algorithm uses `Image.getScaledInstance()` – known performance bottleneck
- Quality settings map to standard rendering hints (nearest-neighbor, bilinear, bicubic)
- Crop uses `getSubimage` (efficient)
- Rotate uses `AffineTransform` with optimized 90°/180°/270° paths
- Benchmark module empty; no JMH tests implemented
- Memory: each operation creates new `BufferedImage` instances (no pooling)