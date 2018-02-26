
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the skills menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class MagicMenu extends Menu implements State {

    private RenderWindow window;
    private int scale;
    private Font text_font;
    private boolean paused = false;
    private int screenHeight;
    private int screenWidth;
    private RectangleShape menuRect;
    private RectangleShape playerRect;
    private ArrayList<Text> skillsText = new ArrayList<>();
    private ArrayList<RectangleShape> skillsRect = new ArrayList<>();
    public static boolean returnTo = false;
    private ArrayList<Character> team;
    private ArrayList<Text> teamText = new ArrayList<>();
    private int selected = 0;
    //private int[] magic; change when implimented

    public MagicMenu(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException {
        menuWindow(window, scale, options_num);
        this.team = team;
        this.window = window;
        this.scale = scale;
        screenHeight = 160 * scale;
        screenWidth = 288 * scale;
        option = 1;

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        soundBuffer = new SoundBuffer();
        soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));

        menuSound = new Sound();
        menuSound.setBuffer(soundBuffer);

        menuRect = new RectangleShape(new Vector2f((screenWidth / 4) * 3, screenHeight - 10));
        menuRect.setFillColor(new Color(11, 2, 138));
        menuRect.setPosition(5 + StateMachine.xOffset, 5 + StateMachine.yOffset);

        playerRect = new RectangleShape(new Vector2f((screenWidth / 4) - 15, screenHeight - 10));
        playerRect.setFillColor(new Color(11, 2, 138));
        playerRect.setPosition(((screenWidth / 4) * 3) + 10 + StateMachine.xOffset, 5 + StateMachine.yOffset);

        for (int i = 0; i < 4; i++) {
            skillsRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 4 - 10)));
            (skillsRect.get(i)).setFillColor(new Color(50, 45, 138));
            (skillsRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 4 - 2) * i) + StateMachine.yOffset);

            skillsText.add(new Text("" + i, text_font, screenHeight / 15));
            (skillsText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 4 - 2) * i) + StateMachine.yOffset);
        }

        for (int i = 0; i < StateMachine.team.size(); i++) {
            teamText.add(new Text(StateMachine.team.get(i).name, text_font, screenHeight / 15));
            bounds = teamText.get(i).getLocalBounds();
            teamText.get(i).setOrigin(bounds.width / 2, bounds.height / 2);
            teamText.get(i).setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * (i*2+1) + StateMachine.yOffset);
        }
    }

    /*
    * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        returnTo = true;
        paused = false;
        selected = 0;
        if(!teamText.isEmpty()){
            teamText.get(selected).setColor(Color.BLACK);            
        }
        

        teamText = new ArrayList<>();
        for (int i = 0; i < StateMachine.team.size(); i++) {
          teamText.add(new Text(StateMachine.team.get(i).name, text_font, screenHeight / 15));
          bounds = teamText.get(i).getLocalBounds();
          teamText.get(i).setOrigin(bounds.width / 2, bounds.height / 2);
          teamText.get(i).setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * (i*2+1) + StateMachine.yOffset);
        }

        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(menuRect);
            window.draw(playerRect);
            skillsText = new ArrayList<>();
            //magic = new Magic[0]; // change when implimented

            // Uncoment when magic is implimented.
            /*for (int i = 0; i < 4; i++) {
              if(magic[i] != null)
              {
                skillsText.add(new Text(magic[i].getName()+"\n"+magic[i].getDescription(), text_font, screenHeight / 15));
                skillsText.get(i).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 4 - 2) * i) + StateMachine.yOffset);
                window.draw(skillsRect.get(i));
                window.draw(skillsText.get(i));
              }
            }*/

            for (int i = 0; i < StateMachine.team.size(); i++) {
              teamText.get(i).setColor(Color.WHITE);
              teamText.get(selected).setColor(Color.BLACK);
              window.draw(teamText.get(i));
            }
            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        paused = true;
                    } else if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                        menuSound.play();
                        selected++;
                        if (selected >= StateMachine.team.size()-1) {
                            selected = StateMachine.team.size()-1;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                      menuSound.play();
                        selected--;
                        if (selected <= 0) {
                            selected = 0;
                        }
                    }
                }
            }
        }
        return 3;
    }
}
