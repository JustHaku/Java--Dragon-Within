/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tilemaper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author valka
 */
public class TileSelector extends JButton {

    private final TileTuple tuple;
    private static final int RESIZE_RATIO = 3;

    /**
     * Creates a new tile selector button
     *
     * @param tileMaper where this displayer is going to be in
     * @param t
     */
    public TileSelector(TileMaper tileMaper, TileTuple t) {
        tuple = t;
        if (t.image == null) {
            super.setText("None");
        } else {
            super.setIcon(new ImageIcon(resize(t.image, RESIZE_RATIO)));
        }

        super.addActionListener((e) -> {
            tileMaper.reselect(getTuple());
        });
    }

    public TileTuple getTuple() {
        return tuple;
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

        this.setPreferredSize(new Dimension(image.getWidth() * ratio, image.getHeight() * ratio));

        return resize;
    }

}
