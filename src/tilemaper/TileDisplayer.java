/*````````````````````
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tilemaper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author valka
 */
public class TileDisplayer extends JButton {

    public static final int UNDERLAY = 0;
    public static final int OVERLAY = 1;
    public static final int ACTOR = 2;

    private static final int RESIZE_RATIO = 5;
    private final TileTuple[] layers = new TileTuple[3];

    public TileDisplayer(TileMaper tm, TileTuple t) {
        for (int i = 0; i < layers.length; i++) {
            layers[i] = t;
        }
        super.setIcon(overlay());
        super.setBorderPainted(false);

        super.addActionListener((e) -> {
            changeLayer(tm.getSelectedLayer(), tm.getSelected());
        });
    }

    public final void changeLayer(int layerNum, TileTuple t) {
        layers[layerNum] = t;
        ImageIcon icon = overlay();

        this.setIcon(icon);

        //JOptionPane.showMessageDialog(null, getIcon());
    }

    public TileTuple getLayer(int layerNum) {
        return layers[layerNum];
    }

    private ImageIcon overlay() {
        // checks if any of the layes has an image if not there is no need to overlay
        TileTuple presentImage;
        layercheck:
        {
            for (TileTuple tt : layers) {
                presentImage = tt;
                if (presentImage != null && presentImage.image != null) {
                    break layercheck;
                }
            }
            return null;
        }

        BufferedImage fImg = new BufferedImage(presentImage.image.getWidth(), presentImage.image.getHeight(), presentImage.image.getType());
        Graphics2D grph = fImg.createGraphics();

        //overlay layers in one image
        for (TileTuple tt : layers) {
            if (tt.image != null) {
                grph.drawImage(tt.image, 0, 0, null);
            }
        }
        grph.dispose();

        return new ImageIcon(resize(fImg, RESIZE_RATIO));
    }

    /**
     * Resizes a an image
     *
     * @param image to be resized
     * @param ratio the ratio of resizing
     * @return a new instance of the image resized
     */
    private BufferedImage resize(BufferedImage image, int ratio) {
        BufferedImage resize = new BufferedImage(image.getWidth() * ratio, image.getHeight() * ratio, image.getType());
        Graphics2D grp = resize.createGraphics();

        grp.drawImage(image, 0, 0, image.getWidth() * ratio, image.getHeight() * ratio, null);
        grp.dispose();

        this.setPreferredSize(new Dimension(image.getWidth() * ratio + 5, image.getHeight() * ratio));

        return resize;
    }

}
