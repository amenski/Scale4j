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

import com.scale4j.util.ImageFormatUtils;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExifMetadata {

    private static final Logger LOGGER = Logger.getLogger(ExifMetadata.class.getName());

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
                    IIOMetadataNode orientationNode = MetadataUtils.getChildNode(root, "Orientation");
                    if (orientationNode != null) {
                        int orientationValue = Integer.parseInt(orientationNode.getAttribute("value"));
                        return ExifOrientation.fromTagValue(orientationValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read EXIF orientation from metadata", e);
        }
        return ExifOrientation.TOP_LEFT;
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
     * Creates a copy of this metadata with a different orientation.
     * Preserves all other metadata (camera settings, geotags, etc.).
     *
     * @param newOrientation the new orientation to set
     * @return a new ExifMetadata instance with the updated orientation
     */
    public ExifMetadata withOrientation(ExifOrientation newOrientation) {
        ExifMetadata copy = new ExifMetadata(newOrientation, this.metadata);
        return copy;
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
                    IIOMetadataNode orientationNode = MetadataUtils.getChildNode(root, "Orientation");
                    if (orientationNode != null) {
                        int orientationValue = Integer.parseInt(orientationNode.getAttribute("value"));
                        return ExifOrientation.fromTagValue(orientationValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read EXIF orientation from metadata", e);
        }
        return ExifOrientation.TOP_LEFT;
    }

    public IIOMetadata getMetadata() {
        return metadata;
    }

    public BufferedImage applyAutoRotation(BufferedImage image) {
        if (orientation == null || orientation == ExifOrientation.TOP_LEFT || !orientation.requiresTransformation()) {
            return image;
        }

        int rotation = orientation.getRotationDegrees();
        boolean flipH = orientation.isFlipHorizontal();
        boolean flipV = orientation.isFlipVertical();

        int width = image.getWidth();
        int height = image.getHeight();

        AffineTransform transform = new AffineTransform();

        if (rotation != 0) {
            double radians = Math.toRadians(rotation);
            switch (rotation) {
                case 90:
                    transform.translate(height, 0);
                    break;
                case 180:
                    transform.translate(width, height);
                    break;
                case 270:
                    transform.translate(0, width);
                    break;
            }
            transform.rotate(radians);
        }

        double scaleX = flipH ? -1.0 : 1.0;
        double scaleY = flipV ? -1.0 : 1.0;
        transform.scale(scaleX, scaleY);

        if (flipH) {
            transform.translate(-width, 0);
        }
        if (flipV) {
            transform.translate(0, -height);
        }

        int newWidth = width;
        int newHeight = height;
        if (rotation == 90 || rotation == 270) {
            newWidth = height;
            newHeight = width;
        }

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        return rotated;
    }

    public void applyToImage(BufferedImage image, File file) throws IOException {
        String format = getFormatFromFile(file);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            ImageIO.write(image, format, file);
            return;
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(ios);
            IIOMetadata imageMetadata = writer.getDefaultImageMetadata(
                new javax.imageio.ImageTypeSpecifier(image), null);

            if (imageMetadata != null) {
                MetadataUtils.mergeExifOrientation(imageMetadata, orientation != null ? orientation.getTagValue() : 1);
            }

            javax.imageio.IIOImage iioImage = new javax.imageio.IIOImage(image, null, imageMetadata);
            writer.write(iioImage);
        } finally {
            writer.dispose();
        }
    }



    private String getFormatFromFile(File file) {
        return ImageFormatUtils.getFormatFromFile(file);
    }
}
