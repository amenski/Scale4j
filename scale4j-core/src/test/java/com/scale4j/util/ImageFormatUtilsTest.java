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
package com.scale4j.util;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImageFormatUtils.
 */
class ImageFormatUtilsTest {

    @Test
    void testGetFormatFromFileNull() {
        assertEquals("png", ImageFormatUtils.getFormatFromFile(null));
    }

    @Test
    void testGetFormatFromFileJpeg() {
        File file = new File("test.jpg");
        assertEquals("jpeg", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileJpegUppercase() {
        File file = new File("test.JPG");
        assertEquals("jpeg", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFilePng() {
        File file = new File("test.png");
        assertEquals("png", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileTiff() {
        File file = new File("test.tiff");
        assertEquals("tiff", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileTif() {
        File file = new File("test.tif");
        assertEquals("tiff", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileWebP() {
        File file = new File("test.webp");
        assertEquals("webp", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileNoExtension() {
        File file = new File("testfile");
        assertEquals("png", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileAvif() {
        File file = new File("test.avif");
        assertEquals("avif", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileBmp() {
        File file = new File("test.bmp");
        assertEquals("bmp", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromFileGif() {
        File file = new File("test.gif");
        assertEquals("gif", ImageFormatUtils.getFormatFromFile(file));
    }

    @Test
    void testGetFormatFromPathNull() {
        assertEquals("png", ImageFormatUtils.getFormatFromPath(null));
    }

    @Test
    void testGetFormatFromPath() {
        Path path = Paths.get("test.jpg");
        assertEquals("jpeg", ImageFormatUtils.getFormatFromPath(path));
    }

    @Test
    void testGetFormatFromExtensionNull() {
        assertEquals("png", ImageFormatUtils.getFormatFromExtension(null));
    }

    @Test
    void testGetFormatFromExtensionEmpty() {
        assertEquals("png", ImageFormatUtils.getFormatFromExtension(""));
    }

    @Test
    void testGetFormatFromExtension() {
        assertEquals("jpeg", ImageFormatUtils.getFormatFromExtension("jpg"));
        assertEquals("jpeg", ImageFormatUtils.getFormatFromExtension("jpeg"));
        assertEquals("png", ImageFormatUtils.getFormatFromExtension("PNG"));
        assertEquals("tiff", ImageFormatUtils.getFormatFromExtension("tif"));
        assertEquals("webp", ImageFormatUtils.getFormatFromExtension("WEBP"));
    }

    @Test
    void testGetFormatFromPathNoExtension() {
        Path path = Paths.get("testfile");
        assertEquals("png", ImageFormatUtils.getFormatFromPath(path));
    }

    @Test
    void testGetFormatFromPathDotFile() {
        Path path = Paths.get(".hidden");
        assertEquals("png", ImageFormatUtils.getFormatFromPath(path));
    }

    @Test
    void testGetFormatFromPathWithMultipleDots() {
        Path path = Paths.get("test.image.png");
        assertEquals("png", ImageFormatUtils.getFormatFromPath(path));
    }
}
