
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Window;
import org.jsfml.window.event.Event;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class Intro implements State {

    private RenderWindow w;
    private Texture mainBG = new Texture();
    private Sprite mainBGSprite = new Sprite();
    private Thread change;
    private Clock c = new Clock();
    private ArrayList<Text> pages = new ArrayList();
    private Text enter_name, char_name;
    private Font text_font;
    private int pageNum;
    private boolean end = false;
    private Music backMusic = new Music();
    private ArrayList<Music> menu = new ArrayList<>();
    private boolean naming = false;
    public String name = "";
    private String[] valid = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public Intro(RenderWindow w) {
        this.w = w;

        menu.add(new Music());
        menu.add(new Music());
        menu.add(new Music());
        menu.add(new Music());
        try {
            menu.get(0).openFromFile(Paths.get("src/audio/ui/click1.ogg"));
            menu.get(1).openFromFile(Paths.get("src/audio/ui/click5.ogg"));
            menu.get(2).openFromFile(Paths.get("src/audio/ui/switch3.ogg"));
            menu.get(3).openFromFile(Paths.get("src/audio/ui/switch33.ogg"));
        } catch (IOException ex) {
            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            backMusic.openFromFile(Paths.get("src/audio/Dungeon Theme 1.wav"));
            backMusic.setLoop(true);
        } catch (IOException ex) {
            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            mainBG.loadFromFile(Paths.get("src/graphics/parchment_alpha.png"));
        } catch (IOException ex) {
            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
        }

        text_font = new Font();
        try {
            text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, null, ex);
        }

        enter_name = new Text("Enter your character's name:\n"
                + name,
                text_font, w.getSize().x / 40);

        enter_name.setOrigin(enter_name.getLocalBounds().width / 2, enter_name.getLocalBounds().height / 2);
        enter_name.setPosition(w.getSize().x / 2, w.getSize().y / 4 + w.getSize().y / 5);
        enter_name.setColor(new Color(0, 0, 0, 255));

        pages.add(new Text(""
                + "Long ago was a story of when dragons once ruled the realm\n"
                + "of Glakevar. Legend tells they were guardians who protected\n"
                + "the land from evil and chaos. Their powers were vast but\n"
                + "never were they used for the purpose of evil.\n\n"
                + "Noone knows of the dragons origin now that they have all died\n"
                + "out.",
                text_font, w.getSize().x / 40));

        pages.add(new Text(""
                + "The dragons have not been seen for over 10 years and \n"
                + "darkness has begun to creep into the realm.\n\n"
                + "Villages have been more susceptible to attacks from bandits \n"
                + "resulting in chaos and death.\n\n"
                + "The realm has been in decline for many years and the breaking\n"
                + "is soon to be at hand.",
                text_font, w.getSize().x / 40));

        pages.add(new Text(""
                + "You were left as a child at an orphanage of initium \n"
                + "and are working your way towards becoming a fully fledged\n"
                + "blacksmith under your master: Leuthard.\n\n"
                + "At the age of 21 you are forced to leave orphange and today\n"
                + "is your 21st birthday.\n\n"
                + "I sense you have a greater destiny to fulfill than that of\n"
                + "a blacksmith's apprentice...",
                text_font, w.getSize().x / 40));

        for (Text t : pages) {
            t.setOrigin(t.getLocalBounds().width / 2, t.getLocalBounds().height / 2);
            t.setPosition(w.getSize().x / 2, w.getSize().y / 4 + w.getSize().y / 5);
            t.setColor(new Color(0, 0, 0, 0));

        }

        mainBGSprite.setTexture(mainBG);
        mainBGSprite.setScale(w.getSize().x / mainBGSprite.getGlobalBounds().width, w.getSize().y / mainBGSprite.getGlobalBounds().height);
        mainBGSprite.setPosition(0, 0);

        change = new Thread(() -> {
            for (Text t : pages) {
                int cc = 0;
                c.restart();
                while (true) {
                    if (Keyboard.isKeyPressed(Keyboard.Key.E)) {
                        cc = 255;
                        try {
                            Thread.sleep(350);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (cc == 255) {
                        break;
                    }
                    t.setColor(new Color(0, 0, 0, cc));
                    if (c.getElapsedTime().asSeconds() >= 0.02) {
                        cc++;
                        c.restart();
                    }
                }
                while (true) {
                    if (Keyboard.isKeyPressed(Keyboard.Key.E)) {
                        cc = 0;
                        try {
                            Thread.sleep(350);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (cc == 0) {
                        break;
                    }

                    t.setColor(new Color(0, 0, 0, cc));
                    if (c.getElapsedTime().asSeconds() >= 0.055) {
                        cc--;
                        c.restart();
                    }

                }

                pageNum++;
            }

            naming = true;

        });

    }

    private void updateName() {
        enter_name.setString("Enter your character's name:\n\n"
                + name + "\n\nPress enter to submit.\n\n\n\n\n\n\n\n Note: Character name must contain 3 or more characters.");

        enter_name.setOrigin(enter_name.getLocalBounds().width / 2, enter_name.getLocalBounds().height / 2);
        enter_name.setPosition(w.getSize().x / 2, w.getSize().y / 4 + w.getSize().y / 5);
        enter_name.setColor(new Color(0, 0, 0, 255));
    }

    @Override
    public int run() {
        w.setKeyRepeatEnabled(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Intro.class.getName()).log(Level.SEVERE, null, ex);
        }
        backMusic.play();
        change.start();
        Full:
        while (w.isOpen()) {
            if (end == true) {
                break;
            }
            backMusic.getStatus();
            w.clear(Color.BLACK);
            w.draw(mainBGSprite);
            if (pages.size() > pageNum) {
                w.draw(pages.get(pageNum));
            }
            if (naming == true) {
                updateName();
                w.draw(enter_name);
            }

            w.display();
            for (Event event : w.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    w.close(); // the user pressed the close button.
                }
                if (event.type == Event.Type.LOST_FOCUS) {
                    w.setFramerateLimit(2); // Will set FPS to low if game is in background.
                } else if (event.type == Event.Type.GAINED_FOCUS) {
                    w.setFramerateLimit(60);
                }
                if (event.type == Event.Type.KEY_PRESSED && naming == true) {
                    boolean validity = false;
                    String p = "";
                    for (String s : valid) {
                        if (Keyboard.isKeyPressed(Keyboard.Key.valueOf(s))) {
                            validity = true;
                            System.out.println(s + " is Valid");
                            p = s;
                            break;
                        }
                    }
                    if (validity == true) {
                        if (name.length() != 10) {
                            name += p;
                            menu.get(2).play();
                        } else {
                            menu.get(0).play();
                        }

                        System.out.println(name);
                    } else if (Keyboard.isKeyPressed(Keyboard.Key.BACKSPACE)) {
                        if (name.length() > 0) {
                            name = name.substring(0, name.length() - 1);
                            menu.get(1).play();
                        } else {
                            menu.get(0).play();
                        }

                        System.out.println(name);
                    } else if (Keyboard.isKeyPressed(Keyboard.Key.RETURN)) {

                        if (name.length() >= 3) {
                            menu.get(3).play();
                            Game.player1.name = name;
                            c.restart();
                            while (backMusic.getVolume() > 0) {
                                if (c.getElapsedTime().asSeconds() >= 0.02) {
                                    backMusic.setVolume(backMusic.getVolume() - 1);
                                    c.restart();
                                }
                            }
                            break Full;

                        } else {
                            menu.get(0).play();

                        }

                    }

                    if (name.length() >= 1) {
                        String temp1, temp2;
                        temp1 = name.substring(1);
                        String toLowerCase = temp1.toLowerCase();
                        temp2 = name.substring(0, 1);
                        String toUpperCase = temp2.toUpperCase();
                        name = toUpperCase + toLowerCase;
                    }

                }

            }

        }
        w.setKeyRepeatEnabled(true);

        backMusic.stop();

        return 98;
    }

}
