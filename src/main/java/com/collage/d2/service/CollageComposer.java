package com.collage.d2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class CollageComposer {

    private static final int COLLAGE_WIDTH = 1280;
    private static final int COLLAGE_HEIGHT = 288;
    private static final int CELL_WIDTH = 256;
    private static final int CELL_HEIGHT = 144;
    private static final int CHARS_PER_ROW = 5;

    public byte[] compose(List<BufferedImage> direImages, List<BufferedImage> radiantImages, BufferedImage mask) {
        log.info("Composing collage {}x{}", COLLAGE_WIDTH, COLLAGE_HEIGHT);

        BufferedImage canvas = new BufferedImage(COLLAGE_WIDTH, COLLAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Row 0 — Dire (top)
        drawRow(g, direImages, 0);
        // Row 1 — Radiant (bottom)
        drawRow(g, radiantImages, 1);

        // Overlay mask
        log.debug("Applying mask overlay");
        g.drawImage(mask, 0, 0, COLLAGE_WIDTH, COLLAGE_HEIGHT, null);

        g.dispose();

        return toBytes(canvas);
    }

    private void drawRow(Graphics2D g, List<BufferedImage> images, int row) {
        for (int i = 0; i < CHARS_PER_ROW; i++) {
            int x = i * CELL_WIDTH;
            int y = row * CELL_HEIGHT;
            BufferedImage scaled = scale(images.get(i));
            g.drawImage(scaled, x, y, null);
            log.debug("Drew image row={} col={} at ({},{})", row, i, x, y);
        }
    }

    private BufferedImage scale(BufferedImage source) {
        BufferedImage result = new BufferedImage(CELL_WIDTH, CELL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, CELL_WIDTH, CELL_HEIGHT, null);
        g.dispose();
        return result;
    }

    private byte[] toBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to encode collage to PNG", e);
            throw new IllegalStateException("Failed to encode collage to PNG", e);
        }
    }
}