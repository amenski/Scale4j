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

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for working with image metadata.
 */
public final class MetadataUtils {
    
    private static final Logger LOGGER = Logger.getLogger(MetadataUtils.class.getName());
    
    private MetadataUtils() {
        // Utility class
    }
    
    /**
     * Gets a child node with the given name from a parent metadata node.
     *
     * @param parent the parent node
     * @param nodeName the name of the child node to find
     * @return the child node, or null if not found
     */
    public static IIOMetadataNode getChildNode(IIOMetadataNode parent, String nodeName) {
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
    
    /**
     * Gets or creates a child node with the given name.
     *
     * @param parent the parent node
     * @param nodeName the name of the child node
     * @return the existing or newly created child node, or null if parent is null
     */
    public static IIOMetadataNode getOrCreateChildNode(IIOMetadataNode parent, String nodeName) {
        if (parent == null) {
            return null;
        }
        IIOMetadataNode existing = getChildNode(parent, nodeName);
        if (existing != null) {
            return existing;
        }
        IIOMetadataNode newNode = new IIOMetadataNode(nodeName);
        parent.appendChild(newNode);
        return newNode;
    }
    
    /**
     * Merges EXIF orientation into image metadata.
     *
     * @param metadata the image metadata to update
     * @param orientationValue the EXIF orientation value (1-8)
     */
    public static void mergeExifOrientation(IIOMetadata metadata, int orientationValue) {
        try {
            String exifFormat = "http://ns.adobe.com/exif/1.0/";
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(exifFormat);
            if (root == null) {
                root = new IIOMetadataNode(exifFormat);
            }
            IIOMetadataNode orientationNode = getOrCreateChildNode(root, "Orientation");
            if (orientationNode != null) {
                orientationNode.setAttribute("value", String.valueOf(orientationValue));
                metadata.mergeTree(exifFormat, root);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to merge EXIF orientation into metadata", e);
        }
    }
    
    /**
     * Merges EXIF orientation into image metadata.
     *
     * @param metadata the image metadata to update
     * @param orientation the EXIF orientation enum value
     */
    public static void mergeExifOrientation(IIOMetadata metadata, ExifOrientation orientation) {
        if (orientation == null) {
            orientation = ExifOrientation.TOP_LEFT;
        }
        mergeExifOrientation(metadata, orientation.getTagValue());
    }
}