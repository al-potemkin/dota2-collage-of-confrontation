package com.collage.d2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ImageLoader {

    private static final String IMAGES_PATH = "images/characters/";
    private static final String MASK_PATH = "images/Mask.png";
    private static final String DEFAULT_PATH = "images/default.png";

    public BufferedImage loadCharacter(String name) {
        String path = IMAGES_PATH + name + ".png";
        log.debug("Loading character image: {}", path);

        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            log.warn("Character image not found: '{}', falling back to default", name);
            return loadDefault();
        }
        return load(path);
    }

    public BufferedImage loadDefault() {
        log.debug("Loading default image: {}", DEFAULT_PATH);
        ClassPathResource resource = new ClassPathResource(DEFAULT_PATH);
        if (!resource.exists()) {
            throw new IllegalStateException("Default image not found at: " + DEFAULT_PATH);
        }
        return load(DEFAULT_PATH);
    }

    public BufferedImage loadMask() {
        log.debug("Loading mask: {}", MASK_PATH);
        return load(MASK_PATH);
    }

    private BufferedImage load(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new IllegalStateException("Cannot decode image at path: " + path);
            }
            log.debug("Loaded image: {} [{}x{}]", path, img.getWidth(), img.getHeight());
            return img;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read image: " + path, e);
        }
    }
}