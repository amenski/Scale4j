package com.scale4j.examples.springboot;

import com.scale4j.ext.springboot.Scale4jTemplate;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpg", "jpeg", "bmp", "gif");

    private final Scale4jTemplate scale4j;

    public ImageController(Scale4jTemplate scale4j) {
        this.scale4j = scale4j;
    }

    @PostMapping("/resize")
    public ResponseEntity<byte[]> resize(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height) throws IOException {

        String format = extractFormat(file.getOriginalFilename());
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            return ResponseEntity.badRequest().build();
        }

        BufferedImage result = scale4j.load(original)
                .resize(width, height)
                .build();

        return toResponse(result, format);
    }

    @PostMapping("/grayscale")
    public ResponseEntity<byte[]> grayscale(
            @RequestParam("file") MultipartFile file) throws IOException {

        String format = extractFormat(file.getOriginalFilename());
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            return ResponseEntity.badRequest().build();
        }

        BufferedImage result = scale4j.load(original)
                .grayscale()
                .build();

        return toResponse(result, format);
    }

    @PostMapping("/sepia")
    public ResponseEntity<byte[]> sepia(
            @RequestParam("file") MultipartFile file) throws IOException {

        String format = extractFormat(file.getOriginalFilename());
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            return ResponseEntity.badRequest().build();
        }

        BufferedImage result = scale4j.load(original)
                .sepia()
                .build();

        return toResponse(result, format);
    }

    @PostMapping("/rotate")
    public ResponseEntity<byte[]> rotate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("degrees") double degrees) throws IOException {

        String format = extractFormat(file.getOriginalFilename());
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            return ResponseEntity.badRequest().build();
        }

        BufferedImage result = scale4j.load(original)
                .rotate(degrees)
                .build();

        return toResponse(result, format);
    }

    @GetMapping("/config")
    public ResponseEntity<String> config() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("""
                        Default quality: %s
                        Default mode: %s
                        Cache enabled: %s
                        Async threads: %d
                        """.formatted(
                                scale4j.getProperties().getDefaultQuality(),
                                scale4j.getProperties().getDefaultMode(),
                                scale4j.getProperties().getCache().isEnabled(),
                                scale4j.getProperties().getAsync().getThreads()));
    }

    private static String extractFormat(String filename) {
        if (filename == null) return "png";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "png";
        String ext = filename.substring(dot + 1).toLowerCase();
        return SUPPORTED_FORMATS.contains(ext) ? ext : "png";
    }

    private static ResponseEntity<byte[]> toResponse(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, format, out);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/" + format.replace("jpg", "jpeg")))
                .body(out.toByteArray());
    }
}
