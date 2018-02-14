
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import org.jsfml.window.*;
import org.jsfml.graphics.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.system.Vector2i;

/**
 * The class that changes the scenes of the game controlling what the player
 * sees.
 *
 * @author Kirk Sparnenn
 */
public class StateMachine {

    public static ArrayList<Character> team = new ArrayList<>();
    public static State[] states = new State[16];
    private static boolean locked = false;
    public static int state = 0;
    public static Game gameWorld;
    private int scale;

    public static void toggleLock() {
        locked = locked != true;
    }

    public StateMachine(int scale) {
        this.scale = scale;
    }

    /**
     * Changes states/ screens when given the relevent number.
     */
    public int run() throws InterruptedException, IOException {
        int screenWidth = 288;
        int screenHeight = 160;
        //int scale = VideoMode.getDesktopMode().height/160;

        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(screenWidth * scale, screenHeight * scale), "The Dragon Within", WindowStyle.CLOSE);
        window.setFramerateLimit(60); // Limit the framerate to 60.
        window.setPosition(Vector2i.ZERO);
        Image icon = new Image();
        icon.loadFromFile(Paths.get("src/graphics/dragon_icon.png"));
        window.setIcon(icon);

        gameWorld = new Game(window, scale);

        State mainMenu = new MainMenu(window, scale, 4);

        team.add(Game.player1);
        team.add(Game.Petros);

        State battleSystem = new BattleSystem(window, scale, 3, team);
        State inventoryMenu = new InventoryMenu(window, scale, 7, team);
        State settingsMenu = new SettingsMenu(window, scale, 3);
        State itemsMenu = new ItemsMenu(window, scale, 4, gameWorld.playerInv);
        State skillsMenu = new SkillsMenu(window, scale, 1);
        State magicMenu = new MagicMenu(window, scale, 1);
        states[0] = mainMenu;
        states[1] = gameWorld;
        states[2] = battleSystem;
        states[3] = inventoryMenu;
        states[4] = settingsMenu;
        states[5] = itemsMenu;
        states[6] = skillsMenu;
        states[7] = magicMenu;

        Vector2i v = new Vector2i(100, 100);
        window.setKeyRepeatEnabled(true);

        while (window.isOpen()) {
            if (state == 2) {
                states[state] = new BattleSystem(window, scale, 3, team);
            } else if (state == 3) {
                states[state] = new InventoryMenu(window, scale, 7, team);
            }

            // Change state if no new class is required.
            if (!locked) {
                state = states[state].run();
            }

            //FileManager.save("src/saves/save000", (Game)gameWorld);
            if (state == 99) {

                gameWorld = new Game(window, scale);
                states[1] = gameWorld;

                //window.setSize(new Vector2i(100,100));
                //System.out.println(window.getSize());
                state = 1;
            }
            if (state > 100) {
                scale = state - 100;
                state = 0;
                window.close();
            }
        }
        return scale;
    }
}
