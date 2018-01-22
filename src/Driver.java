//
//	SCC210 Example code
//
//		Andrew Scott, 2015
//

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Semaphore;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.jsfml.audio.Music;

import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;

class Test {

    private static int spd = 2;
    private static int SCALE = 4;
    private static int screenWidth = 288 * SCALE; //Must be a multiple of 288 
    private static int screenHeight = 160 * SCALE; //Must be a multiple of 160

    private static int tileSize = 16 * SCALE; //Must be a multiple of 16
    private static int gridWidth = screenWidth / tileSize;
    private static int gridHeight = screenHeight / tileSize;

    //
    // The Java install comes with a set of fonts but these will
    // be on different filesystem paths depending on the version
    // of Java and whether the JDK or JRE version is being used.
    private static Semaphore s = new Semaphore(1);
    //
    private static String JavaVersion
            = Runtime.class.getPackage().getImplementationVersion();
    private static String JdkFontPath
            = "C:\\Program Files\\Java\\jdk" + JavaVersion
            + "\\jre\\lib\\fonts\\";
    private static String JreFontPath
            = "C:\\Program Files\\Java\\jre" + JavaVersion
            + "\\lib\\fonts\\";

    private static int fontSize = 48;
    private static String FontFile = "LucidaSansRegular.ttf";
    private static String ImageFile = "";
    private static String worldMap = "src/graphics/world/spritesheet/barrier";

    private static String Title = "The Dragon Within";
    private static String Message = "Round and round...";

    private static Texture imgTexture = new Texture();
    private static Texture playerTexture = new Texture();
    private static Texture barrierTexture = new Texture();

    private Image icon;

    private String FontPath;	// Where fonts were found

    private ArrayList<Actor> actors = new ArrayList<Actor>();
    private ArrayList<worldPiece> world = new ArrayList<worldPiece>();
//    private ArrayList<barrier> barriers = new ArrayList<barrier>();

    private abstract class Actor {

        Drawable obj;
        IntConsumer rotate;
        BiConsumer<Float, Float> setPosition;

        int x = 0;	// Current X-coordinate
        int y = 0;	// Current Y-coordinate

        int r = 0;	// Change in rotation per cycle
        int dx = 2;	// Change in X-coordinate per cycle
        int dy = 2;	// Change in Y-coordinate per cycle

        //
        // Is point x, y within area occupied by this object?
        //
        // This should really be done with bounding boxes not points
        //
        boolean within(int x, int y) {
            // Should check object bounds here
            // -- we'd normally assume a simple rectangle
            //    ...and override as necessary
            return false;
        }

        //
        // Work out where object should be for next frame
        //
        void calcMove(int minx, int miny, int maxx, int maxy) {
            //
            // Add deltas to x and y position
            //
//            x += dx;
//            y += dy;

            //
            // Check we've not hit screen bounds
//            if (x <= minx || x >= maxx) {
//                dx *= -1;
//                x += dx;
//            }
//            if (y <= miny || y >= maxy) {
//                dy *= -1;
//                y += dy;
//            }
            // Check we've not hit screen bounds
            // Check we've not collided with any other actor
            //
//            for (Actor a : actors) {
//                if (a.obj != obj && a.within(x, y)) {
//                    dx *= -1;
//                    x += dx;
//                    dy *= -1;
//                    y += dy;
//                }
//            }
//            for (Actor a : actors) {
//                if (a.obj != obj && a.within(x, y)) {
//                    dx *= -1;
//                    x += dx;
//                    dy *= -1;
//                    y += dy;
//                }
//            }
        }

        //
        // Reposition the object
        //
        void performMove() {
            //rotate.accept(r);
            setPosition.accept((float) x, (float) y);
        }

        //
        // Render the object at its new position
        //
        void draw(RenderWindow w) {
            w.draw(obj);
        }
    }

    private class worldPiece {

        private Sprite img;
        private IntRect piece;

        public worldPiece(Texture imgTexture, int x, int y, int c1, int c2) {
            piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

            img = new Sprite(imgTexture, piece);
            img.setPosition(x * tileSize, y * tileSize);
            img.setScale(SCALE, SCALE);

        }

        void draw(RenderWindow w) {
            w.draw(img);
        }
    }

    private class player extends Actor {

        private Sprite img;
        private Sprite weapon;
        private Sprite shield;
        private IntRect[] states = new IntRect[2];
        private int c1;
        private int c2;
        private int w1;
        private int w2;

        public player(Texture imgTexture) {
            c1 = 1;
            c2 = 6;
            states[0] = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

            w1 = 46;
            w2 = 4;
            states[1] = new IntRect(((w1 * 16) + w1), ((w2 * 16) + w2), 16, 16);

            img = new Sprite(imgTexture, states[0]);
            weapon = new Sprite(imgTexture, states[1]);
            img.setScale(SCALE / (float) 1.333, SCALE / (float) 1.333);
            //weapon.setScale(SCALE-1,SCALE-1);

            x = 1;
            y = 1;

            setPosition = img::setPosition;

        }

        @Override
        void calcMove(int minx, int miny, int maxx, int maxy) {
            if (x <= minx || x >= maxx) {
                if (x <= minx) {
                    x += spd;
                } else {
                    x -= spd;
                }
            }
            if (y <= miny || y >= maxy) {
                if (y <= minx) {
                    y += spd;
                } else {
                    y -= spd;
                }

            }
            for (Actor a : actors) {
                if (a.obj != obj && a.within(x, y)) {
                    System.out.println("Collision");
                }
            }

        }

        @Override
        boolean within(int px, int py) {
            if (px > x - (states[0].width * (SCALE / (float) 1.333)) && px < x + (states[0].width * (SCALE / (float) 1.333))
                    && py > y - (states[0].width * (SCALE / (float) 1.333)) && py < y + (states[0].width * (SCALE / (float) 1.333))) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        void draw(RenderWindow w) {
            w.draw(img);
            //w.draw(weapon);
        }
    }

    private class barrier extends Actor {
        private Sprite img;
        private Texture barrierTexture;

        public barrier(int x, int y, Texture barrierTexture) throws IOException {
            this.barrierTexture = barrierTexture;
            img = new Sprite(barrierTexture);

            this.x = x * tileSize;
            this.y = y * tileSize;

            obj = img;

            setPosition = img::setPosition;
        }

        @Override
        void calcMove(int minx, int miny, int maxx, int maxy) {
            for (Actor a : actors) {
                if (a.obj != obj && a.within(x, y)) {
                    if (a.x <= x) {
                        a.x -= spd;
                    } else if (a.x >= x) {
                        a.x += spd;
                    }
                    if (a.y <= y) {
                        a.y -= spd;
                    } else if (a.y >= y) {
                        a.y += spd;
                    }
                }
            }
        }
    }

    private class map {
        public map(String mapPath, Texture imgTexture) throws FileNotFoundException, IOException {
            world.clear();
            Scanner scanner = new Scanner(new File(mapPath));
            int[][] k = new int[gridHeight][gridWidth * 2];
            int p = 0;
            int q = 0;
            while (scanner.hasNextInt()) {

                k[q][p] = scanner.nextInt();
                p++;
                if (p % (gridWidth * 2) == 0) {
                    p = 0;
                    q++;
                }

            }
            for (int i = 0; i < gridWidth; i++) {
                for (int j = 0; j < gridHeight; j++) {
                    world.add(new worldPiece(imgTexture, i, j, k[j][i * 2], k[j][(i * 2) + 1]));
                    if (k[j][i * 2] == 0 && k[j][(i * 2) + 1] == 0) {
                        actors.add(new barrier(i, j, barrierTexture));

                    }
                }
            }

        }

    }

    public void run() throws InterruptedException, FileNotFoundException, IOException {
        imgTexture.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeSheet_transparent.png"));
        playerTexture.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeChar_transparent.png"));
        map main = new map("src/tilemaps/demo.txt", imgTexture);
        player p1 = new player(playerTexture);
        actors.add(p1);
        barrierTexture.loadFromFile(Paths.get("src/graphics/world/Spritesheet/barrier.png"));
        barrier test = new barrier(0, 0, barrierTexture);

        //Gets footsteps sound
        Music m = new Music();
        m.openFromFile(Paths.get("src/audio/rpg/footstep06.ogg"));

        //Opens music from file, sets it to loop, and starts it.
        Music main_theme = new Music();
        main_theme.openFromFile(Paths.get("src/audio/rpg/main_theme.ogg"));
        main_theme.setLoop(true);
        main_theme.play();

        //
        // Check whether we're running from a JDK or JRE install
        // ...and set FontPath appropriately.
        //
        if ((new File(JreFontPath)).exists()) {
            FontPath = JreFontPath;
        } else {
            FontPath = JdkFontPath;
        }

        //
        // Create a window
        //;
        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(screenWidth, screenHeight),
                Title,
                WindowStyle.CLOSE);
        window.setFramerateLimit(30); // Avoid excessive updates
        //window.setIcon(icon);
        while (window.isOpen()) {

            main_theme.getStatus();

            if (window.isOpen()) // Clear the screen
            {
                window.clear(Color.WHITE);
            }

            // Move all the actors around
            if (Keyboard.isKeyPressed(Keyboard.Key.UP)) {
                p1.y -= spd;
                m.play();
            }
            if (Keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
                p1.y += spd;
                m.play();
            }
            if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
                p1.x -= spd;
                m.play();
            }
            if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
                p1.x += spd;
                m.play();

            }
            
            //Draws the backsground and main tiles 
            for (worldPiece worldMap : world) {
                worldMap.draw(window);
            }
            
            //Draws the "Foreground" objects to interact with including: player, barriers and npc
            for (Actor actor : actors) {
                actor.calcMove(0, 0, screenWidth, screenHeight);
                actor.performMove();
                actor.draw(window);
            }

            // Update the display with any changes
            window.display();

            // Handle any events
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    // the user pressed the close button
                    window.close();
                }
                if (event.type == Event.Type.LOST_FOCUS) {
                    window.setFramerateLimit(8);
                } else if (event.type == Event.Type.GAINED_FOCUS) {
                    window.setFramerateLimit(30);
                }
            }
        }
    }

    public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {
        Test t = new Test();
        t.run();

    }
}
