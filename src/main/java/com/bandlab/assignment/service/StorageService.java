package com.bandlab.assignment.service;

import com.bandlab.assignment.service.clients.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private StorageClient storageClient;

    @Autowired
    public StorageService(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File convertedFile = handleAndConvertMultipart(file);
        return storageClient.uploadFile(convertedFile);
    }

    private File handleAndConvertMultipart(MultipartFile file) throws IOException {
        File convertedFile = Files.createTempFile("temp", file.getOriginalFilename()).toFile();
        Files.copy(file.getInputStream(), convertedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        file.transferTo(convertedFile);
        return convertAndResizeImageFile(convertedFile);
    }

    private File convertAndResizeImageFile(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        // We don't need to convert images explicitly from png, jpg and bmp, we can resize first and then write it as JPG
        File convertedImageFile = Files.createTempFile("temp-img", file.getName()).toFile();
        BufferedImage target = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = target.createGraphics();
        graphics2D.drawImage(image, 0, 0, 600, 600, null);
        ImageIO.write(target, "jpg", convertedImageFile);
        return convertedImageFile;
    }
}
