/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities.services;

import com.aptitekk.aptibook.core.entities.File;
import com.aptitekk.aptibook.core.util.LogManager;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.ejb.Stateful;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Stateful
public class FileService extends MultiTenantEntityServiceAbstract<File> implements Serializable {

    private static final int MAX_IMAGE_SIZE_PX = 1000;

    /**
     * Uploads the image, scales it, and crops it.
     *
     * @param part The image part file.
     * @return A File entity containing the data of the image.
     * @throws IOException If the image is not a true image, or otherwise cannot be parsed.
     */
    public File createFileFromImagePart(Part part) throws IOException {
        if (part == null) {
            LogManager.logError("Attempt to upload image failed due to a null Part");
            return null;
        }

        BufferedImage bufferedImage = ImageIO.read(part.getInputStream());

        // Convert the image to remove the Alpha channel if it exists.
        BufferedImage tempRgbImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = tempRgbImage.createGraphics();
        g2d.setColor(Color.WHITE); // Or what ever fill color you want...
        g2d.fillRect(0, 0, tempRgbImage.getWidth(), tempRgbImage.getHeight());
        g2d.drawImage(bufferedImage, 0, 0, null);
        g2d.dispose();
        bufferedImage = tempRgbImage;

        // Scale the image up or down first
        int requiredWidthHeight;

        if (bufferedImage.getWidth() > MAX_IMAGE_SIZE_PX || bufferedImage.getHeight() > MAX_IMAGE_SIZE_PX)
            requiredWidthHeight = MAX_IMAGE_SIZE_PX;
        else if (bufferedImage.getWidth() > bufferedImage.getHeight())
            requiredWidthHeight = bufferedImage.getWidth();
        else
            requiredWidthHeight = bufferedImage.getHeight();

        bufferedImage = Thumbnails.of(bufferedImage)
                .size(requiredWidthHeight, requiredWidthHeight)
                .crop(Positions.CENTER)
                .outputQuality(1.0)
                .asBufferedImage();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "JPG", outputStream);
        byte[] output = outputStream.toByteArray();
        if (output != null) {
            File file = new File();
            file.setData(output);

            try {
                insert(file);
                return file;
            } catch (Exception e) {
                LogManager.logError("Attempt to upload image failed while persisting: " + e.getMessage());
                return null;
            }
        } else {
            LogManager.logError("Attempt to upload image failed due to a null byte array output.");
            return null;
        }
    }

}
