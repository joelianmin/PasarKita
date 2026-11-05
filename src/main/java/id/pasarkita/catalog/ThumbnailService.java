package id.pasarkita.catalog;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

@Service
class ThumbnailService {

    String thumbnailFor(Long productId) {
        BufferedImage source = new BufferedImage(1600, 1600, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        try {
            int shade = (int) (productId * 37 % 255);
            graphics.setPaint(new GradientPaint(0, 0, new Color(shade, 120, 200), 1600, 1600, Color.WHITE));
            graphics.fillRect(0, 0, 1600, 1600);
        } finally {
            graphics.dispose();
        }
        AffineTransformOp scale = new AffineTransformOp(
                AffineTransform.getScaleInstance(0.125, 0.125), AffineTransformOp.TYPE_BICUBIC);
        BufferedImage thumbnail = scale.filter(source, null);
        return "/thumbnails/" + productId + "-" + Integer.toHexString(thumbnail.getRGB(100, 100)) + ".jpg";
    }
}
