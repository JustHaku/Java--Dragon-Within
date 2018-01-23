
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.jsfml.graphics.Texture;

/**
 * Changes the current world map based on a text file with coordinates.
 * The text file is also called a tileMap as it defines which types of tiles go where.
 * Each pair of numbers correlates to the coordinates of a piece on the sprite sheet.
 * Each location of the pairs of numbers in the text file correlates to location of the game screen.
 * The pairs of number can not exceed the grid size e.g.
 * 
 * Below is the correct format of the tileMap.
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0   0 0
 * 
 * @author LBals
 */
public class WorldMap {

    /**
     * Constructs a new map based on a text file of coordinates.
     * See class description for further details.
     * 
     * @param imgTexture Spritesheet containing the pieces of the world.
     * @param barrierTexture Texture of barrier.
     * @param mapPath Text file containing the tileMap so that the constructor can build the pieces.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public WorldMap(Texture imgTexture, Texture barrierTexture,String mapPath) throws FileNotFoundException, IOException {
        Game.returnWorldPieces().clear(); // Clears current map so new one can take its' place
        
        Scanner scanner = new Scanner(new File(mapPath)); // Reads the tileMap file.
        
        int[][] k = new int[Game.gridHeight][Game.gridWidth * 2];
        int p = 0;
        int q = 0;
        
        //Stores integers from map file into an array
        while (scanner.hasNextInt()) {
            k[q][p] = scanner.nextInt();
            p++;
            if (p % (Game.gridWidth * 2) == 0) {
                p = 0;
                q++;
            }
        }
        
        // Creates new world pieces based on integers that were read from the tileMap.
        for (int i = 0; i < Game.gridWidth; i++) {
            for (int j = 0; j < Game.gridHeight; j++) {
                Game.returnWorldPieces().add(new WorldPiece(imgTexture, i, j, k[j][i * 2], k[j][(i * 2) + 1]));
                if (k[j][i * 2] == 0 && k[j][(i * 2) + 1] == 0) {
                    Game.returnActors().add(new Barrier(barrierTexture, i, j));
                }
            }
        }
    }
}
