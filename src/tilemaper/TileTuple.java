/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tilemaper;

import java.awt.image.BufferedImage;

/**
 *
 * @author valka
 */
public class TileTuple {

    public final BufferedImage image;
    public final int x;
    public final int y;

    public TileTuple(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

}
