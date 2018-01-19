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

import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;

class Test {

    private static int screenWidth = 288;
    private static int screenHeight = 160;
    
    private static int tileSize = 16;
    private static int gridWidth = screenWidth/tileSize;
    private static int gridHeight = screenHeight/tileSize;

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
    private static String worldMap = "src/graphics/world/spritesheet/roguelikeSheet_transparent.png";

    private static String Title = "The Dragon Within";
    private static String Message = "Round and round...";

    private String FontPath;	// Where fonts were found

    private ArrayList<Actor> actors = new ArrayList<Actor>();
    private ArrayList<worldPiece> world = new ArrayList<worldPiece>();

    private abstract class Actor {

        Drawable obj;
        IntConsumer rotate;
        BiConsumer<Float, Float> setPosition;

        int x = 0;	// Current X-coordinate
        int y = 0;	// Current Y-coordinate

        int r = 0;	// Change in rotation per cycle
        int dx = 5;	// Change in X-coordinate per cycle
        int dy = 5;	// Change in Y-coordinate per cycle

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
            //x += dx;
            //y += dy;

            //
            // Check we've not hit screen bounds
            //
            if (x <= minx || x >= maxx) {
                dx *= -1;
                x += dx;
            }
            if (y <= miny || y >= maxy) {
                dy *= -1;
                y += dy;
            }

            //
            // Check we've not collided with any other actor
            //
            for (Actor a : actors) {
                if (a.obj != obj && a.within(x, y)) {
                    dx *= -1;
                    x += dx;
                    dy *= -1;
                    y += dy;
                }
            }
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

        public worldPiece(String textureFile, int x, int y, int c1, int c2) {
            Texture imgTexture = new Texture();
            try {
                imgTexture.loadFromFile(Paths.get(textureFile));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            imgTexture.setSmooth(true);
            piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

            img = new Sprite(imgTexture, piece);
            img.setPosition(x * 16, y * 16);

        }

        void draw(RenderWindow w) {
            w.draw(img);
        }
    }
    
    private class map {
        public map(String mapPath) throws FileNotFoundException{
            world.clear();
            Scanner scanner = new Scanner(new File(mapPath));
            int [][] k = new int [gridHeight][gridWidth*2];
            int p = 0;
            int q = 0;
            while(scanner.hasNextInt()){
                
               k[q][p] = scanner.nextInt();
               p++;
               if(p%(gridWidth*2) == 0){
                   p = 0;
                   q++;
               }
               
            }
            for (int i = 0; i < gridWidth; i++) {
                    for (int j = 0; j < gridHeight; j++) {
                        world.add(new worldPiece(worldMap, i, j, k[j][i * 2], k[j][(i * 2) + 1]));
                    }
                }
            
}
            
        }

    public void run() throws InterruptedException, FileNotFoundException {
                
            map main = new map("src/tilemaps/demo.txt");
       

                



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
                WindowStyle.DEFAULT);
        window.setFramerateLimit(20); // Avoid excessive updates

        int stage = 0;
        while (window.isOpen()) {
            // Clear the screen
            window.clear(Color.WHITE);

            // Move all the actors around
            for (Actor actor : actors) {
                actor.calcMove(0, 0, screenWidth, screenHeight);
                actor.performMove();
                actor.draw(window);
            }

            for (worldPiece worldMap : world) {
                worldMap.draw(window);
            }

            // Update the display with any changes
            window.display();

            // Handle any events
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    // the user pressed the close button
                    window.close();
                }
            }
        }
    }

    public static void main(String args[]) throws InterruptedException, FileNotFoundException {

        Test t = new Test();
        t.run();

    }
}
