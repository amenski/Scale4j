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
package com.scale4j.metadata;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExifMetadata {

    private ExifOrientation orientation;
    private IIOMetadata metadata;

    /**
     * Creates a new empty ExifMetadata instance.
     */
    public ExifMetadata() {
        this.orientation = ExifOrientation.TOP_LEFT;
        this.metadata = null;
    }

    /**
     * Creates a new ExifMetadata with the specified orientation.
     *
     * @param orientation the EXIF orientation
     */
    public ExifMetadata(ExifOrientation orientation) {
        this.orientation = orientation;
        this.metadata = null;
    }

    /**
     * Creates a new ExifMetadata with the specified orientation and metadata.
     *
     * @param orientation the EXIF orientation
     * @param metadata the IIOMetadata
     */
    public ExifMetadata(ExifOrientation orientation, IIOMetadata metadata) {
        this.orientation = orientation;
        this.metadata = metadata;
    }

    private ExifOrientation getOrientationFromMetadata(IIOMetadata metadata) {
        if (metadata == null) {
            return ExifOrientation.TOP_LEFT;
        }
        try {
            String[] metadataFormatNames = metadata.getMetadataFormatNames();
            for (String formatName : metadataFormatNames) {
                if ("http://ns.adobe.com/exif/1.0/".equals(formatName)) {
                    IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
                    IIOMetadataNode orientationNode = getChildNode(root, "Orientation");
                    if (orientationNode != null) {
                        int orientationValue = Integer.parseInt(orientationNode.getAttribute("value"));
                        return ExifOrientation.fromTagValue(orientationValue);
                    }
                }
            }
        } catch (Exception e) {
        }
        return ExifOrientation.TOP_LEFT;
    }

    private IIOMetadataNode getChildNode(IIOMetadataNode parent, String nodeName) {
        if (parent == null) {
            return null;
        }
        for (int i = 0; i < parent.getLength(); i++) {
            IIOMetadataNode child = (IIOMetadataNode) parent.item(i);
            if (child.getNodeName().equals(nodeName)) {
                return child;
            }
        }
        return null;
    }

    public static ExifMetadata readFromFile(File file) throws IOException {
        ExifMetadata exif = new ExifMetadata();
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    IIOMetadata imageMetadata = reader.getImageMetadata(0);
                    exif.metadata = imageMetadata;
                    exif.orientation = exif.getOrientationFromMetadata(imageMetadata);
                } finally {
                    reader.dispose();
                }
            }
        }
        return exif;
    }

    public static ExifMetadata readFromStream(InputStream stream) throws IOException {
        ExifMetadata exif = new ExifMetadata();
        try (ImageInputStream iis = ImageIO.createImageInputStream(stream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    IIOMetadata imageMetadata = reader.getImageMetadata(0);
                    exif.metadata = imageMetadata;
                    exif.orientation = exif.getOrientationFromMetadata(imageMetadata);
                } finally {
                    reader.dispose();
                }
            }
        }
        return exif;
    }

    /**
     * Returns the EXIF orientation.
     *
     * @return the orientation
     */
    public ExifOrientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the EXIF orientation.
     *
     * @param orientation the orientation to set
     */
    public void setOrientation(ExifOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Reads the orientation from IIOMetadata.
     *
     * @param metadata the IIOMetadata to read from
     * @return the ExifOrientation, defaults to TOP_LEFT if not found
     */
    public ExifOrientation readOrientationFromMetadata(IIOMetadata metadata) {
        if (metadata == null) {
            return ExifOrientation.TOP_LEFT;
        }
        try {
            String[] metadataFormatNames = metadata.getMetadataFormatNames();
            for (String formatName : metadataFormatNames) {
                if ("http://ns.adobe.com/exif/1.0/".equals(formatName)) {
                    IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
                    IIOMetadataNode orientationNode = getChildNode(root, "Orientation");
                    if (orientationNode != null) {
                        int orientationValue = Integer.parseInt(orientationNode.getAttribute("value"));
                        return ExifOrientation.fromTagValue(orientationValue);
                    }
                }
            }
        } catch (Exception e) {
            // Silently return default
        }
        return ExifOrientation.TOP_LEFT;
    }

    public IIOMetadata getMetadata() {
        return metadata;
    }

    public BufferedImage applyAutoRotation(BufferedImage image) {
        if (orientation == null || orientation == ExifOrientation.TOP_LEFT) {
            return image;
        }

        boolean flipH = orientation.isFlipHorizontal();
        boolean flipV = orientation.isFlipVertical();

        if (!flipH && !flipV) {
            return image;
        }

        double scaleX = flipH ? -1.0 : 1.0;
        double scaleY = flipV ? -1.0 : 1.0;

        AffineTransform transform = new AffineTransform();
        transform.scale(scaleX, scaleY);

        if (flipH && flipV) {
            transform.translate(-image.getWidth(), -image.getHeight());
        } else if (flipH) {
            transform.translate(-image.getWidth(), 0);
        } else if (flipV) {
            transform.translate(0, -image.getHeight());
        }

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }

    public void applyToImage(BufferedImage image, File file) throws IOException {
        if (metadata == null) {
            return;
        }

        String format = getFormatFromFile(file);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            return;
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(ios);
            IIOMetadata imageMetadata = writer.getDefaultImageMetadata(
                new javax.imageio.ImageTypeSpecifier(image), null);

            if (imageMetadata != null && metadata.isStandardMetadataFormatSupported()) {
                mergeExifOrientation(imageMetadata, ExifOrientation.TOP_LEFT.getTagValue());
            }

            writer.write(image);
        } finally {
            writer.dispose();
        }
    }

    private void mergeExifOrientation(IIOMetadata metadata, int orientationValue) {
        try {
            String exifFormat = "http://ns.adobe.com/exif/1.0/";
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(exifFormat);
            IIOMetadataNode orientationNode = getChildNode(root, "Orientation");
            if (orientationNode != null) {
                orientationNode.setAttribute("value", String.valueOf(orientationValue));
                metadata.mergeTree(exifFormat, root);
            }
        } catch (Exception e) {
        }
    }

    private String getFormatFromFile(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "jpeg";
        } else if (name.endsWith(".png")) {
            return "png";
        } else if (name.endsWith(".gif")) {
            return "gif";
        } else if (name.endsWith(".bmp")) {
            return "bmp";
        } else if (name.endsWith(".webp")) {
            return "webp";
        } else if (name.endsWith(".avif")) {
            return "avif";
        }
        return "png";
    }
}
