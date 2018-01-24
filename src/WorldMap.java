
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.jsfml.graphics.Texture;

/**
 * Creates world map using the tilemaps at 
 * "src/tilemaps/w/w/_underlay",
 * "src/tilemaps/w/w/_overlay",
 * "src/tilemaps/w/w/_actorlay"
 * where x is the world number.
 * 
 * the -1 flag in all layers means do nothing. 
 * the -2 flag in the over layer means add a barrier.
 * 
 *
 * @author LBals
 */
public class WorldMap {

    private Scanner[] scanners;
    private int layer;

    private final ArrayList<WorldPiece> underlay = new ArrayList<>();
    private final ArrayList<WorldPiece> overlay = new ArrayList<>();
    private final ArrayList<Actor> actorlay = new ArrayList<>();
    
    String underlayTileMap = new String("src/tilemaps/");
    String overlayTileMap = new String("src/tilemaps/");
    String actorlayTileMap = new String("src/tilemaps/");

    private Texture imgTexture;

    private int[][] k;

    /**
     * Creates world map using the tilemaps at 
     * "src/tilemaps/w/w/_underlay",
     * "src/tilemaps/w/w/_overlay",
     * "src/tilemaps/w/w/_actorlay"
     * where x is the world number.
     * @param imgTexture World texture sprite sheet
     * @param w World number to load
     * @throws FileNotFoundException
     * @throws IOException
     */
    public WorldMap(Texture imgTexture, int w)
            throws FileNotFoundException, IOException {
        
        this.imgTexture = imgTexture;

        Scanner[] scanners = new Scanner[3];
        
        underlayTileMap = underlayTileMap + w + "/" + w + "_underlay.txt";
        overlayTileMap = overlayTileMap + w + "/" + w + "_overlay.txt";
        actorlayTileMap = actorlayTileMap + w + "/" + w + "_actorlay.txt";

        // Clears current map so new one can take its' place
        scanners[0] = new Scanner(new File(underlayTileMap)); // Reads the tileMap file.
        scanners[1] = new Scanner(new File(overlayTileMap)); // Reads the tileMap file.
        scanners[2] = new Scanner(new File(actorlayTileMap)); // Reads the tileMap file.

        k = new int[Game.gridHeight][Game.gridWidth * 2];

        layer = 0;
        for (int i = 0; i < 3; i++) {
            storeInts(scanners[i]);
        }
        // Creates new world pieces based on integers that were read from the tileMap.
    }

    /**
     * Returns under layer of the map
     * @return underlay
     */
    public ArrayList<WorldPiece> getUnder() {
        return underlay;
    }

    /**
     * Returns over layer of the map
     * @return overlay
     */
    public ArrayList<WorldPiece> getOver() {
        return overlay;
    }

    /**
     * Returns actor layer of the map.
     * @return actorlay
     */
    public ArrayList<Actor> getActor() {
        return actorlay;
    }

    private void storeInts(Scanner sc) {
        int p = 0;
        int q = 0;

        //Stores integers from map file into an array
        while (sc.hasNextInt()) {
            k[q][p] = sc.nextInt();
            p++;
            if (p % (Game.gridWidth * 2) == 0) {
                p = 0;
                q++;
            }
        }
        layer++;
        buildMaps();
    }

    private void buildMaps() {
        for (int i = 0; i < Game.gridWidth; i++) {
            for (int j = 0; j < Game.gridHeight; j++) {
                if (layer == 1) {
                    if (k[j][i * 2] == -1 && k[j][(i * 2) + 1] == -1) {

                    } else {
                        underlay.add(new WorldPiece(imgTexture, i, j, k[j][i * 2], k[j][(i * 2) + 1]));
                    }
                }
                if (layer == 2) {
                    if (k[j][i * 2] == -1 && k[j][(i * 2) + 1] == -1) {

                    } else if (k[j][i * 2] == -2 && k[j][(i * 2) + 1] == -2) {
                        actorlay.add(new WorldPieceActor(imgTexture, i, j, 0, 5,actorlay));

                    } else {
                        overlay.add(new WorldPiece(imgTexture, i, j, k[j][i * 2], k[j][(i * 2) + 1]));
                    }
                }
                if (layer == 3) {
                    if (k[j][i * 2] == -1 && k[j][(i * 2) + 1] == -1) {
                    }else {
                        actorlay.add(new WorldPieceActor(imgTexture, i, j, k[j][i * 2], k[j][(i * 2) + 1],actorlay));
                    }

                }
            }
        }

    }
}
