## Issue
Operations create many intermediate BufferedImage objects, causing high memory pressure. No pooling or reuse of image buffers.

## Impact
High GC overhead, poor performance on large images or batch processing, memory spikes.

## Task
Introduce a simple image pool (SoftReference-based) for common sizes/color models. Add configurable pooling strategy (none, soft, thread-local). Provide memory usage monitoring hooks.

## Implementation Details
1. Create `ImagePool` interface
2. Create `SoftReferenceImagePool` implementation
3. Create `ImagePoolManager` singleton
4. Update operations to use pool for intermediate images
5. Add configuration options
6. Add memory monitoring

## Verification Checklist
- [ ] ImagePool interface created
- [ ] SoftReferenceImagePool implementation
- [ ] ImagePoolManager singleton
- [ ] Operations updated to use pool
- [ ] Configuration options added
- [ ] Memory monitoring hooks
- [ ] Compilation passes
- [ ] Tests pass