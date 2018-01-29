
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.audio.Music;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;

/**
 * The main game class for running the game. This is the main game class which
 * runs the game. This class will construct pieces of the game and draw them in
 * a loop.
 *
 * @author LBals
 */
public class Game {

    /**
     * The speed in which the player moves at.
     */
    public final static int spd = 4;

    /**
     * The scale of the game. This is changed when you want the game screen to
     * change.
     */
    public final static int SCALE = 4;

    /**
     * Width of the game screen. Must be a multiple of 288.
     */
    public final static int screenWidth = 288 * SCALE;

    /**
     * Height of the game screen. Must be a multiple of 160.
     */
    public final static int screenHeight = 160 * SCALE;

    /**
     * Size of the tile. Must be a multiple of 16 Allows for easy scaling
     */
    public final static int tileSize = 16 * SCALE;

    /**
     * How many tiles wide the game screen is.
     */
    public final static int gridWidth = screenWidth / tileSize;

    /**
     * How may tiles high the game screen is.
     */
    public final static int gridHeight = screenHeight / tileSize;

    // The Java install comes with a set of fonts but these will
    // be on different filesystem paths depending on the version
    // of Java and whether the JDK or JRE version is being used.
    private static final String JavaVersion
            = Runtime.class.getPackage().getImplementationVersion();
    private static final String JdkFontPath
            = "C:\\Program Files\\Java\\jdk" + JavaVersion
            + "\\jre\\lib\\fonts\\";
    private static final String JreFontPath
            = "C:\\Program Files\\Java\\jre" + JavaVersion
            + "\\lib\\fonts\\";

    private static final String Title = "The Dragon Within";

    // Textures for the game.
    public static final Texture worldSpriteSheet = new Texture();
    public static final Texture playerSpriteSheet = new Texture();
    public static final Texture barrierTexture = new Texture();

    // Audio for the game.
    private static final Music mainTheme = new Music();
    private static final Music footsteps1 = new Music();
    private static final Music footsteps2 = new Music();

    // Clocks for the game.
    private static final Clock footstepsTimer = new Clock();

    // State of footsteps for swapping audio.
    private static int footstepsState = 0;

    private String FontPath; // Where fonts were found.

    // Arrays lists for background pieces (WorldPiece) and foreground pieces (Actor)
    private static final ArrayList<WorldPiece> underlay0 = new ArrayList<WorldPiece>();
    private static final ArrayList<WorldPiece> overlay0 = new ArrayList<WorldPiece>();
    private static final ArrayList<Actor> actorlay0 = new ArrayList<Actor>();

    public static final ArrayList<WorldPiece> underlay1 = new ArrayList<WorldPiece>();
    public static final ArrayList<WorldPiece> overlay1 = new ArrayList<WorldPiece>();
    public static final ArrayList<Actor> actorlay1 = new ArrayList<Actor>();

    public static final ArrayList<WorldMap> maps = new ArrayList<>();

    public static int worldNum = 0;

    /**
     * Array list of actors. Typically used for adding/removing from the list.
     * Add actors when you want them to be displayed in game.
     *
     * @return Array list of actors.
     */
    public static ArrayList<Actor> returnActors() {
        return actorlay0;
    }

    public static void changeWorld(int w) {
        worldNum = w;
    }

    /**
     * Array list of tiles. Typically used for creating a game map. Add
     * WorldPieces when you want them to be displayed in game.
     *
     * @return Arrays list of tiles.
     */
    public static ArrayList<WorldPiece> returnWorldPieces() {
        return overlay0;
    }

    public static ArrayList<WorldPiece> returnUnderlayPieces() {
        return underlay0;
    }

    //Slows down the footsteps and also has 2 sounds for footsteps.
    private void playFootsteps() {
        if (footstepsTimer.getElapsedTime().asMilliseconds() > 500) {
            if (footstepsState == 0) {
                footsteps1.play();
                footstepsState = 1;
            } else if (footstepsState == 1) {
                footsteps2.play();
                footstepsState = 0;
            }
            footstepsTimer.restart();
        }
    }

    /**
     * Main game loop for starting the game. Call this method to start the game.
     *
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void run() throws InterruptedException, FileNotFoundException, IOException {

        worldSpriteSheet.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeSheet_transparent.png"));

        playerSpriteSheet.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeChar_transparent.png"));

        barrierTexture.loadFromFile(Paths.get("src/graphics/world/Spritesheet/barrier.png"));

        maps.add(new WorldMap(worldSpriteSheet, 0));
        maps.add(new WorldMap(worldSpriteSheet, 1));
        maps.add(new WorldMap(worldSpriteSheet, 2));

        Player player1 = new Player(playerSpriteSheet);

        maps.get(0).getActor().add(new Portal(player1, worldSpriteSheet, 8, 4, 33, 1, 8, 9, 2,maps.get(0).getActor())); //Door to interior
        maps.get(0).getActor().add(new Portal(player1, worldSpriteSheet, 6, 10, 33, 0, 6, 1, 1, maps.get(0).getActor()));
        maps.get(0).getActor().add(new Portal(player1, worldSpriteSheet, 7, 10, 33, 0, 6, 1, 1, maps.get(0).getActor()));

        maps.get(1).getActor().add(new Portal(player1, worldSpriteSheet, 6, 0, 0, 5, 6, 9, 0, maps.get(1).getActor()));
        maps.get(1).getActor().add(new Portal(player1, worldSpriteSheet, 7, 0, 0, 5, 6, 9, 0, maps.get(1).getActor()));
        
        maps.get(2).getActor().add(new Portal(player1, worldSpriteSheet, 8, 10, 0, 5, 8, 5, 0, maps.get(2).getActor()));
        
        
        for(WorldMap a: maps){
            a.getActor().add(player1);     
        }
        
        

        footsteps1.openFromFile(Paths.get("src/audio/rpg/footstep00.ogg"));
        footsteps2.openFromFile(Paths.get("src/audio/rpg/footstep01.ogg"));
        footsteps1.setVolume(50);
        footsteps2.setVolume(50);

        mainTheme.openFromFile(Paths.get("src/audio/rpg/main_theme.ogg"));
        mainTheme.setLoop(true);
        mainTheme.play();

        // Check whether we're running from a JDK or JRE install
        // ...and set FontPath appropriately.
        if ((new File(JreFontPath)).exists()) {
            FontPath = JreFontPath;
        } else {
            FontPath = JdkFontPath;
        }

        // Create a window.
        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(
                screenWidth, screenHeight),
                Title,
                WindowStyle.CLOSE);
        window.setFramerateLimit(60); // Avoid excessive updates.

        while (window.isOpen()) {

            mainTheme.getStatus();

            if (window.isOpen()) { // Clear the screen.
                window.clear(Color.WHITE);
            }
            
            // Check for input (UP,DOWN,LEFT,RIGHT)
            if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
                player1.moveUp();
                playFootsteps();
            } else if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
                player1.moveDown();
                playFootsteps();
            } else if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
                player1.moveLeft();
                playFootsteps();
            } else if (Keyboard.isKeyPressed(Keyboard.Key.D)) {
                player1.moveRight();
                playFootsteps();
            }

            

            //Draws underlay tiles
            for (WorldPiece ul : maps.get(worldNum).getUnder()) {
                ul.draw(window);
            }
            //Draws the backsground and main tiles.ssdddw
            for (WorldPiece wp : maps.get(worldNum).getOver()) {
                wp.draw(window);
            }
            

            //Draws the "Foreground" objects to interact with including: player, barriers and npc.
            for (Actor actor : maps.get(worldNum).getActor()) {
                actor.calcMove(0, 0, screenWidth, screenHeight);
                actor.performMove();
                actor.draw(window);
            }
            
            // Update the display with any changes.
            window.display();

            // Handle any events.
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    // the user pressed the close button.
                    window.close();
                }
                if (event.type == Event.Type.LOST_FOCUS) {
                    // Will set FPS to low if game is in background.
                    window.setFramerateLimit(8);
                    mainTheme.pause();

                } else if (event.type == Event.Type.GAINED_FOCUS) {
                    window.setFramerateLimit(60);
                    mainTheme.play();;
                    
                }
            }
        }

    }

    /**
     * Starts the game.
     *
     * @param args
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {
        Game game1 = new Game();
        game1.run();

    }
}
