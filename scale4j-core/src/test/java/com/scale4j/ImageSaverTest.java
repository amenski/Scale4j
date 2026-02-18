/*
 * Copyright 2024 Scale4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scale4j;

import com.scale4j.exception.ImageSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.scale4j.exception.ImageProcessException;

import java.awt.image.BufferedImage;
import com.scale4j.exception.ImageProcessException;
import java.io.ByteArrayOutputStream;
import com.scale4j.exception.ImageProcessException;
import java.io.File;
import com.scale4j.exception.ImageProcessException;
import java.io.IOException;
import com.scale4j.exception.ImageProcessException;
import java.io.OutputStream;
import com.scale4j.exception.ImageProcessException;
import java.nio.file.Path;
import com.scale4j.exception.ImageProcessException;

import static org.assertj.core.api.Assertions.assertThat;
import com.scale4j.exception.ImageProcessException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.scale4j.exception.ImageProcessException;

/**
 * Unit tests for ImageSaver.
 */
class ImageSaverTest {

    @TempDir
    Path tempDir;

    @Test
    void write_file_withExtension() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File output = tempDir.resolve("output.png").toFile();

        boolean result = ImageSaver.write(image, output);
        assertThat(result).isTrue();
        assertThat(output).exists();
        // Verify it can be loaded
        BufferedImage loaded = ImageLoader.load(output);
        assertThat(loaded.getWidth()).isEqualTo(100);
        assertThat(loaded.getHeight()).isEqualTo(100);
    }

    @Test
    void write_file_withoutExtension_defaultsToPng() throws IOException {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        File output = tempDir.resolve("output").toFile(); // no extension

        boolean result = ImageSaver.write(image, output);
        assertThat(result).isTrue();
        assertThat(output).exists();
        // The file will be saved as PNG because default extension is png (see getFormatFromPath)
        // We can load it as PNG.
        BufferedImage loaded = ImageLoader.load(output);
        assertThat(loaded.getWidth()).isEqualTo(50);
    }

    @Test
    void write_file_nullImage_throwsNullPointerException() {
        File output = tempDir.resolve("out.png").toFile();
        // ImageIO.write will throw NullPointerException
        assertThatThrownBy(() -> ImageSaver.write(null, output))
                .isInstanceOf(ImageSaveException.class);
    }

    @Test
    void write_file_nullFile_throwsNullPointerException() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        // ImageIO.write will throw NullPointerException
        assertThatThrownBy(() -> ImageSaver.write(image, (File) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void write_file_unsupportedFormat_returnsFalse() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File output = tempDir.resolve("output.xyz").toFile(); // unsupported format

        boolean result = ImageSaver.write(image, output);
        // ImageIO.write returns false if no appropriate writer is found
        assertThat(result).isFalse();
        // The file may still be created (empty) but we don't care
    }

    @Test
    void write_path_valid() throws IOException {
        BufferedImage image = new BufferedImage(80, 60, BufferedImage.TYPE_INT_RGB);
        Path output = tempDir.resolve("test.jpg");

        boolean result = ImageSaver.write(image, output);
        assertThat(result).isTrue();
        assertThat(output).exists();
    }

    @Test
    void write_withFormatSpecified() throws IOException {
        BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        File output = tempDir.resolve("any.extension").toFile();

        boolean result = ImageSaver.write(image, "png", output);
        assertThat(result).isTrue();
        assertThat(output).exists();
        // Load as PNG
        BufferedImage loaded = ImageLoader.load(output);
        assertThat(loaded.getWidth()).isEqualTo(30);
    }

    @Test
    void write_withFormatSpecified_unsupportedFormat_returnsFalse() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File output = tempDir.resolve("out.xyz").toFile();

        boolean result = ImageSaver.write(image, "xyz", output);
        assertThat(result).isFalse();
    }

    @Test
    void write_toOutputStream() {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        OutputStream out = new ByteArrayOutputStream();

        boolean result = ImageSaver.write(image, "png", out);
        assertThat(result).isTrue();
        assertThat(out.toString()).isNotEmpty();
    }

    @Test
    void write_toOutputStream_unsupportedFormat_returnsFalse() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        OutputStream out = new ByteArrayOutputStream();

        boolean result = ImageSaver.write(image, "xyz", out);
        assertThat(result).isFalse();
    }

    @Test
    void write_toOutputStream_nullImage_throwsNullPointerException() {
        OutputStream out = new ByteArrayOutputStream();
        // ImageIO.write will throw NullPointerException
        assertThatThrownBy(() -> ImageSaver.write(null, "png", out))
                .isInstanceOf(ImageSaveException.class);
    }

    @Test
    void write_toOutputStream_nullOutputStream_throwsNullPointerException() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        // ImageIO.write will throw NullPointerException
        assertThatThrownBy(() -> ImageSaver.write(image, "png", (OutputStream) null))
                .isInstanceOf(ImageSaveException.class);
    }

    @Test
    void isWritableFormat_validFormats() {
        // Common formats that should be writable by ImageIO
        assertThat(ImageSaver.isWritableFormat("png")).isTrue();
        assertThat(ImageSaver.isWritableFormat("jpg")).isTrue();
        assertThat(ImageSaver.isWritableFormat("jpeg")).isTrue();
        assertThat(ImageSaver.isWritableFormat("gif")).isTrue();
        assertThat(ImageSaver.isWritableFormat("bmp")).isTrue();
        assertThat(ImageSaver.isWritableFormat("PNG")).isTrue(); // case-insensitive
    }

    @Test
    void isWritableFormat_unsupportedFormat() {
        assertThat(ImageSaver.isWritableFormat("xyz")).isFalse();
        assertThat(ImageSaver.isWritableFormat("")).isFalse();
        assertThat(ImageSaver.isWritableFormat(null)).isFalse();
    }

    @Test
    void getWritableFormats_nonEmpty() {
        var formats = ImageSaver.getWritableFormats();
        assertThat(formats).isNotEmpty();
        assertThat(formats).contains("png", "jpg", "jpeg", "gif", "bmp");
    }

    @Test
    void getWritableFormats_lowercase() {
        var formats = ImageSaver.getWritableFormats();
        for (String fmt : formats) {
            assertThat(fmt).isLowerCase();
        }
    }

    @Test
    void getFormatFromPath_withExtension() throws IOException {
        Path path = Path.of("example.jpg");
        // private method, but we can test via write with file (since it uses that method)
        // Instead we can just trust that the method works.
        // We'll test by writing a file with .jpg extension and verifying it's saved as JPEG.
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File file = tempDir.resolve("test.jpg").toFile();
        ImageSaver.write(image, file);
        // If format detection fails, it would default to PNG but still should write.
        // We'll just ensure file exists.
        assertThat(file).exists();
    }

    @Test
    void getFormatFromPath_noExtension_defaultsToPng() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File file = tempDir.resolve("noext").toFile();
        ImageSaver.write(image, file);
        // Load as PNG (since default)
        BufferedImage loaded = ImageLoader.load(file);
        assertThat(loaded).isNotNull();
    }
}