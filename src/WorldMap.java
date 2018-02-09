
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
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
public class WorldMap implements Serializable
{

    private Scanner[] scanners;
    private int layer;
    private String worldName = null;
    private int w;

    private final ArrayList<WorldPiece> underlay = new ArrayList<>();
    private final ArrayList<WorldPiece> overlay = new ArrayList<>();
    private final ArrayList<Actor> actorlay = new ArrayList<>();

    String src_path = "src/tilemaps/";

    private Texture imgTexture;

    private int[][] map_holder;

    /**
     * Creates world map using the tilemaps at
     * "src/tilemaps/w/w/_underlay",
     * "src/tilemaps/w/w/_overlay",
     * "src/tilemaps/w/w/_actorlay"
     * where w is the world number.
     * @param imgTexture World texture sprite sheet
     * @param w World number to load
     * @throws FileNotFoundException
     * @throws IOException
     */
    public WorldMap(Texture imgTexture, int w)
            throws FileNotFoundException, IOException {
        this.w = w;

        this.imgTexture = imgTexture;

        Scanner[] scanners = new Scanner[3];

        String underlayTileMap = src_path + w + "/" + w + "_underlay.txt";
        String overlayTileMap = src_path + w + "/" + w + "_overlay.txt";
        String actorlayTileMap = src_path + w + "/" + w + "_actorlay.txt";

        // Clears current map so new one can take its' place
        scanners[0] = new Scanner(new File(underlayTileMap)); // Reads the tileMap file.
        scanners[1] = new Scanner(new File(overlayTileMap)); // Reads the tileMap file.
        scanners[2] = new Scanner(new File(actorlayTileMap)); // Reads the tileMap file.

        map_holder = new int[Game.gridHeight][Game.gridWidth * 2];

        // Creates new world pieces based on integers that were read from the tileMap.
        layer = 0;
        for (int i = 0; i < 3; i++) {
            storeInts(scanners[i]);
        }
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
        while (sc.hasNextInt())
        {
            map_holder[q][p] = sc.nextInt();
            p++;
            if (p % (Game.gridWidth * 2) == 0)
            {
                p = 0;
                q++;
            }
        }
        layer++;
        buildMaps();
    }
    
    public void setWorldName(String s){
        worldName = s;
    }
    
    public String getWorldName(){
        if(worldName != null){
            return worldName + " (" + Integer.toString(w) + ")";
            
        }else{
            return "World: " + Integer.toString(w);
        }
    }
    

    private void buildMaps() {
        for (int map_width = 0; map_width < Game.gridWidth; map_width++)
        {
            for (int map_height = 0; map_height < Game.gridHeight; map_height++)
            {
                if (layer == 1)
                {
                    if (map_holder[map_height][map_width * 2] == -1 && map_holder[map_height][(map_width * 2) + 1] == -1)
                    {
                      //do nothing
                    }
                    else
                    {
                      underlay.add(new WorldPiece(imgTexture, map_width, map_height, map_holder[map_height][map_width * 2], map_holder[map_height][(map_width * 2) + 1]));
                    }
                }
                else if (layer == 2)
                {
                  if (map_holder[map_height][map_width * 2] == -2 && map_holder[map_height][(map_width * 2) + 1] == -2)
                  {
                      actorlay.add(new WorldPieceActor(imgTexture, map_width, map_height, 0, 5,actorlay));
                  }
                  else
                  {
                      overlay.add(new WorldPiece(imgTexture, map_width, map_height, map_holder[map_height][map_width * 2], map_holder[map_height][(map_width * 2) + 1]));
                  }
                }
                else if (layer == 3)
                {
                  if (map_holder[map_height][map_width * 2] == -1 && map_holder[map_height][(map_width * 2) + 1] == -1)
                  {
                    //do nothing
                  }
                  else
                  {
                      actorlay.add(new WorldPieceActor(imgTexture, map_width, map_height, map_holder[map_height][map_width * 2], map_holder[map_height][(map_width * 2) + 1],actorlay));
                  }
                }
            }
        }

    }
}
