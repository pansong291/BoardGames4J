package windows.panel;

import java.awt.*;

/**
 * @author paso
 * @since 2022/10/6
 */
public class DoubleBufferingPanel extends Canvas {
    private Image image;
    private Graphics graphics;

    @Override
    public void paint(Graphics g) {
    }

    @Override
    public void update(Graphics g) {
        if (image == null || graphics == null) {
            image = createImage(getWidth(), getHeight());
            graphics = image.getGraphics();
        } else {
            graphics.clearRect(0, 0, getWidth(), getHeight());
        }
        paint(graphics);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }

    public void clearCache() {
        image = null;
        graphics = null;
    }
}
