
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Game implements State, Serializable {

    public static final int spd = 4; //The speed in which the player moves at.
    public static int SCALE; //The scale of the game. This is changed when you want the game screen to change.
    public static int screenWidth; //Width of the game screen. Must be a multiple of 288.
    public static int screenHeight; //Height of the game screen. Must be a multiple of 160.
    public static int tileSize; //Size of the tile. Must be a multiple of 16 Allows for easy scaling.
    public static int gridWidth; //How many tiles wide the game screen is.
    public static int gridHeight; //How may tiles high the game screen is.
    public static Player player1;
    public static ArrayList<Skills> skill;

    //String name, int health, int mana, int atk, int def, int spd, int lvl, boolean isFriendly -> Constructor for unique npc
    public static NPC Petros = new NPC("Petros", 103, 104, 15, 16, 12, 3, true);
    private RenderWindow window;
    private int battleChance = 0;

    private Save s;

    // The Java install comes with a set of fonts but these will
    // be on different filesystem paths depending on the version
    // of Java and whether the JDK or JRE version is being used.
    private final String JavaVersion = Runtime.class.getPackage().getImplementationVersion();
    private final String JdkFontPath = "C:\\Program Files\\Java\\jdk" + JavaVersion + "\\jre\\lib\\fonts\\";
    private final String JreFontPath = "C:\\Program Files\\Java\\jre" + JavaVersion + "\\lib\\fonts\\";

    private FileManager f;
    public Inventory playerInv = new Inventory();
    public Inventory traderInv = new Inventory();
    public Inventory trader2Inv = new Inventory();
    Helper h;

    //The game title
    private final String Title = "The Dragon Within Pt.1";
    private Trader trader;

    //private Event event;
    // Textures for the game.
    public final Texture worldSpriteSheet = new Texture();
    public final Texture playerSpriteSheet = new Texture();
    public final Texture barrierTexture = new Texture();
    public final Texture uiTexture = new Texture();

    // Audio for the game.
    public static final Music mainTheme = new Music();
    private final Music footsteps1 = new Music();
    private final Music footsteps2 = new Music();
    private final Music openChest = new Music();
    private final Music openDoor = new Music();
    private final Music closeDoor = new Music();
    private final Music stairs = new Music();
    private ArrayList<TalkNPC> npcs = new ArrayList<>();

    public static int state = 1;

    // Clocks for the game.
    private final Clock footstepsTimer = new Clock();
    private final Clock saveTimer = new Clock();
    private final Clock routeClock = new Clock();

    private MessageBox routeMessage;

    // State of footsteps for swapping audio.
    private int footstepsState = 0;

    //Tests whether window is minimised or not
    static boolean isMinimized = false;

    private String FontPath; // Where fonts were found.

    // Arrays lists for background pieces (WorldPiece) and foreground pieces (Actor)
    private final ArrayList<WorldMap> maps = new ArrayList<>();

    //Definition of item list
    Consumable potion = new Consumable(1, "Potion", 20, 0, 100);
    Consumable ether = new Consumable(2, "Ether", 0, 20, 150);
    
    Consumable freshFish = new Consumable(2, "\"Fresh Fish\"", -20, 0, 5);
    
    TalkNPC pete;

    Weapon dagger = new Weapon(1, "Dagger", 60);
    Weapon cleaver = new Weapon(2, "Cleaver", 100);

    public int worldNum = 0;

    public ArrayList<MessageBox> constructMessage(String[] messages) {
        ArrayList<MessageBox> m = new ArrayList<>();

        for(String p: messages){
            m.add(new MessageBox(0,Game.screenHeight - (49 * (Game.SCALE / 2)),p, Color.BLACK));            
        }
        
        return m;       
    }
    
    

    public Game(RenderWindow window, int scale) throws InterruptedException, IOException {
        Activator.activators.clear();
        this.window = window;
        this.SCALE = scale;
        this.screenWidth = 288 * scale;
        this.screenHeight = 160 * scale;
        this.tileSize = 16 * scale;
        this.gridWidth = screenWidth / tileSize;
        this.gridHeight = screenHeight / tileSize;
        window.setKeyRepeatEnabled(true);
        init();
        try {
            skill = BasicSkills.readSkills("./skills.xml");
            skill.get(0).teachTo(player1);
            skill.get(0).teachTo(Petros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Array list of actors. Typically used for adding/removing from the list.
     * Add actors when you want them to be displayed in game.
     *
     * @param w
     * @return Array list of actors.
     */
    public void changeWorld(int w) {
        battleChance = 0;

        try {
            System.out.println("Saved");
            s = new Save(playerInv, player1, this, Activator.activators);
            Save.save("src/saves/save000", s);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        worldNum = w;
        String worldName = maps.get(worldNum).getWorldName();
        routeMessage = new MessageBox(Game.screenWidth - (190 * (Game.SCALE)), 0, worldName, Color.BLACK);
        routeClock.restart();

    }

    public int getWorldNum() {
        return worldNum;
    }

    public void load(Save s) {
        changeWorld(s.getWorld());
        player1.setPosition(s.getX(), (s.getY()));
        playerInv = s.getInventory();
        for (Activator a : Activator.activators) {
            System.out.println(a.activated);
            if (s.getID().get(Activator.activators.indexOf(a)) == true) {
                a.setActivated();
            }
        }
        for (Boolean a : s.getID()) {
            System.out.println("Save file: " + a);
        }
    }

    //Slows down the footsteps and also has 2 sounds for footsteps.
    private void playFootsteps() {
        if (!isMinimized) {
            if (footstepsTimer.getElapsedTime().asMilliseconds() > 350) {
                if (maps.get(worldNum).isHostile() && Math.floor(Math.random() * Math.floor(20)) == 0) {
                    battleChance = 10;
                }
                if (footstepsState == 0) {
                    footsteps1.play();
                    footstepsState = 1;
                    //battleChance++;

                } else if (footstepsState == 1) {
                    footsteps2.play();
                    footstepsState = 0;
                    //battleChance++;

                }
                footstepsTimer.restart();
            }
        }
    }

    //Creates copy of item and makes it an activator and then adds it to the world
    private void addActivator(int m, int x, int y, Consumable c) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new Consumable(c.getId(), c.getName(), c), playerInv));
    }

    private void addActivator(int m, int x, int y, Consumable c, Music f) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new Consumable(c.getId(), c.getName(), c), playerInv, f));
    }

    private void addActivator(int m, int x, int y, Weapon c) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new Weapon(c.getId(), c.getName(), c.dmg), playerInv));
    }

    private void addActivator(int m, int x, int y, Weapon c, Music f) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new Weapon(c.getId(), c.getName(), c.dmg), playerInv, f));
    }

    private void addActivator(int m, int x, int y, Weapon c, Music f, int x2, int y2) {
        AddItem d = null;
        for (Actor a : maps.get(m).getActor()) {
            if (a.x == x * Game.tileSize && a.y == y * Game.tileSize && a.getClass() == WorldPieceActor.class) {
                d = new AddItem(worldSpriteSheet, "", x, y, new Weapon(c.getId(), c.getName(), c.dmg), playerInv, f, (WorldPieceActor) a);
            }
        }
        if (d != null) {
            maps.get(m).getActor().add(d);
            d.addAlt(x2, y2);
        } else {
            throw new java.lang.NullPointerException("No WorldPieceActor at given coordinates");
        }

        //System.out.println(x + " " + y);
    }
    
    private void addActivator(int m, int x, int y, Consumable c, Music f, int x2, int y2) {
        AddItem d = null;
        for (Actor a : maps.get(m).getActor()) {
            if (a.x == x * Game.tileSize && a.y == y * Game.tileSize && a.getClass() == WorldPieceActor.class) {
                d = new AddItem(worldSpriteSheet, "", x, y, new Consumable(c.getId(), c.getName(),c), playerInv, f, (WorldPieceActor) a);
            }
        }
        if (d != null) {
            maps.get(m).getActor().add(d);
            d.addAlt(x2, y2);
        } else {
            throw new java.lang.NullPointerException("No WorldPieceActor at given coordinates");
        }

        //System.out.println(x + " " + y);
    }

    private void addActivator(int m, int x, int y, Trinket c) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new Trinket(c.getId(), c.getName()), playerInv));
    }

    private void addActivator(int m, int x, int y, KeyItem c) {
        maps.get(m).getActor().add(new AddItem(worldSpriteSheet, "", x, y, new KeyItem(c.getId(), c.getName()), playerInv));
    }

    //Creates portal with texture and size
    //m = map number, t = target world, x1 y1 = position of piece, x2 y2 = which sprite, x3 y3 = set player location, w h = height and width of portal
    private void addPort(int m, int t, int x1, int y1, int x2, int y2, int x3, int y3, int w, int h) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1 + i, y1 + j, x2, y2, x3, y3, t, maps.get(m).getActor(), this));
            }
        }
    }

    private void addPort(int m, int t, int x1, int y1, int x2, int y2, int x3, int y3, int w, int h, Music k) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1 + i, y1 + j, x2, y2, x3, y3, t, maps.get(m).getActor(), this, k));
            }
        }
    }

    //Creates portal with texture
    //m = map number, t = target world, x1 y1 = position of piece, x2 y2 = which sprite, x3 y3 = set player location
    private void addPort(int m, int t, int x1, int y1, int x2, int y2, int x3, int y3) {
        maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1, y1, x2, y2, x3, y3, t, maps.get(m).getActor(), this));
    }

    private void addPort(int m, int t, int x1, int y1, int x2, int y2, int x3, int y3, Music k) {
        maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1, y1, x2, y2, x3, y3, t, maps.get(m).getActor(), this, k));
    }

    //Creates portal without texture
    //m = map number, t = target world x1 y1 = position of piece, x2 y2 = set player location
    private void addPort(int m, int t, int x1, int y1, int x2, int y2) {
        maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1, y1, 0, 5, x2, y2, t, maps.get(m).getActor(), this));
    }

    private void addPort(int m, int t, int x1, int y1, int x2, int y2, Music k) {
        maps.get(m).getActor().add(new Portal(player1, worldSpriteSheet, x1, y1, 0, 5, x2, y2, t, maps.get(m).getActor(), this, k));
    }

    //Creates portals on each map
    //m1, m2 = maps you want to "link", x1, y1 = location of portal, s = size of portal, o = orientation of portal (width or height)
    //Only add portals on the world so that if o = "" then add to the world "above" or if o = "y" then add portal to the world to the "left"
    private void addExtPort(int m1, int m2, int x1, int y1, int s, String o) {
        if (o.equals("x") || o.equals("X")) {
            for (int i = 0; i < s; i++) {
                maps.get(m1).getActor().add(new Portal(player1, worldSpriteSheet, x1 + i, y1, 0, 5, x1 + i, Game.gridHeight - y1, m2, maps.get(m1).getActor(), this));
                maps.get(m2).getActor().add(new Portal(player1, worldSpriteSheet, x1 + i, Game.gridHeight - y1 - 1, 0, 5, x1 + i, y1 - 1, m1, maps.get(m2).getActor(), this));
            }
        } else if (o.equals("y") || o.equals("Y")) {
            for (int i = 0; i < s; i++) {
                maps.get(m1).getActor().add(new Portal(player1, worldSpriteSheet, x1, y1 + i, 0, 5, Game.gridWidth - x1, y1 + i, m2, maps.get(m1).getActor(), this));
                maps.get(m2).getActor().add(new Portal(player1, worldSpriteSheet, Game.gridWidth - x1 - 1, y1 + i, 0, 5, x1 - 1, y1 + i, m1, maps.get(m2).getActor(), this));
            }
        } else {
            throw new java.lang.Error("No such orientation: Must be X or Y");
        }
    }

    private void initMaps() throws IOException {
        int wn = 0;
        while (true) {
            try {
                maps.add(new WorldMap(worldSpriteSheet, wn));
                wn++;

            } catch (FileNotFoundException e) {
                break;
            }
        }

        maps.get(0).setWorldName("Initium");
        maps.get(1).setWorldName("Flodowth Landing");
        maps.get(2).setWorldName("Orphanage 1F");
        maps.get(3).setWorldName("Orphanage 2F");
        maps.get(4).setWorldName("Bedroom 1");
        maps.get(5).setWorldName("Bedroom 2");

        maps.get(0).setHostile();

    }

    private void initActivators() {
        addActivator(5, 4, 3, potion, openChest,24,5);
        addActivator(5, 13, 3, ether, openChest, 24, 5);
        addActivator(1, 5, 6, dagger, openChest, 38, 11);
        addActivator(4, 8, 3, cleaver, openChest, 38, 11);
        //addActivator();

    }

    private void initPlayer() throws IOException {
        player1 = new Player(playerSpriteSheet, maps, this);
        player1.setTilePosition(1, 4);
        worldNum = 5;
        trader = new Trader("Trader",
                new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)),
                        "Psssst. Wanna buy a potion?",
                        Color.BLACK),
                playerSpriteSheet,
                1, 9,
                this, window, playerInv, traderInv,0,0);
        maps.get(0).getActor().add(trader);
        traderInv.addConsumable(potion);
        traderInv.addConsumable(ether);
        
        trader = new Trader("Trader",
                new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)),
                        "Freeeeesh fish!",
                        Color.BLACK),
                playerSpriteSheet,
                1, 8,
                this, window, playerInv, trader2Inv,16,4);
        maps.get(15).getActor().add(trader);
        trader2Inv.addConsumable(freshFish);
        
        String[] s1 = {
            "Welcome to the world of Galkevar",
            "Use WASD to move around, E to interact, and ESC to exit menus.",
            "Go forth and fulfil your destiny!"
        } ;
        npcs.add(new TalkNPC("Pete", constructMessage(s1) ,playerSpriteSheet, 1,10,7,3));
        maps.get(5).getActor().add(npcs.get(0));
        
        String[] s2 = {
            "I've been stuck in this orphanage for years...",
            "We finally get to leave when we are 18...",
            "Just 3 more lonely years."
        };
        npcs.add(new TalkNPC("Pete", constructMessage(s2) ,playerSpriteSheet, 1,9,16,7));
        maps.get(5).getActor().add(npcs.get(1));
        
        String[] s3 = {
            "I get out in one week! I shall become a baker!"
        };
        npcs.add(new TalkNPC("Pete", constructMessage(s3) ,playerSpriteSheet, 1,7,1,6));
        maps.get(5).getActor().add(npcs.get(2));
        
        String[] s4 = { 
            "50 Years i have been a fisherman. My time is nearly over.",
            "In that chest, over there, is a dagger...",
            "Take it. You'll need it more than I, in these dark times."
           
        };
        npcs.add(new TalkNPC("Pete", constructMessage(s4) ,playerSpriteSheet, 1,8,10,3));
        maps.get(1).getActor().add(npcs.get(3));
        
        String[] s5 = { 
            "*Cough* *Cough*. That fish i ate was disgusting."   
        };
        npcs.add(new TalkNPC("Pete", constructMessage(s5) ,playerSpriteSheet, 1,8,7,5));
        maps.get(6).getActor().add(npcs.get(4));

    }

    private void loadSounds() throws IOException {
        footsteps1.openFromFile(Paths.get("src/audio/rpg/footstep00.ogg"));
        footsteps2.openFromFile(Paths.get("src/audio/rpg/footstep01.ogg"));
        footsteps1.setVolume(50);
        footsteps2.setVolume(50);

        openChest.openFromFile(Paths.get("src/audio/rpg/dropLeather.ogg"));
        openDoor.openFromFile(Paths.get("src/audio/rpg/doorOpen_1.ogg"));
        closeDoor.openFromFile(Paths.get("src/audio/rpg/doorClose_4.ogg"));
        stairs.openFromFile(Paths.get("src/audio/rpg/stairs.ogg"));

        mainTheme.openFromFile(Paths.get("src/audio/rpg/main_theme.ogg"));
        mainTheme.setLoop(true);
        mainTheme.setVolume(80);

    }

    private void loadTextures() throws IOException {
        worldSpriteSheet.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeSheet_transparent.png"));
        playerSpriteSheet.loadFromFile(Paths.get("src/graphics/world/Spritesheet/roguelikeChar_transparent.png"));
        barrierTexture.loadFromFile(Paths.get("src/graphics/world/Spritesheet/barrier.png"));
    }

    private void loadPortals() throws IOException {
        addExtPort(0, 1, 4, 9, 4, "x"); //Path to the fishing and back
        addExtPort(0, 6, 17, 5, 2, "y"); //Path to the forest and back
        addPort(0, 2, 8, 4, 37, 2, 8, 8, openDoor); //Door to Orphanage

        addPort(2, 0, 8, 9, 8, 5, closeDoor); //Orphanage exit
        addPort(2, 0, 9, 9, 8, 5, closeDoor); //Orphanage exit
        addPort(2, 3, 0, 2, 35, 18, 1, 5, stairs); //Orphanage left stairs to 1st floor
        addPort(2, 3, 17, 2, 34, 18, 16, 5, stairs); //Orphanage right stairs to 1st floor

        addPort(3, 2, 0, 5, 36, 18, 1, 2, stairs); //Orphanage left stairs to ground floor
        addPort(3, 2, 17, 5, 37, 18, 16, 2, stairs); //Orphanage right stairs to ground floor
        addPort(3, 4, 2, 2, 37, 1, 8, 8, openDoor); //Orphanage hall to left bedroom
        addPort(3, 5, 12, 2, 37, 1, 8, 8, openDoor); //Orphanage hall to right bedroom

        addPort(4, 3, 8, 9, 0, 5, 2, 3, 2, 1, closeDoor); //Orphanage left bedroom to hall
        addPort(5, 3, 8, 9, 0, 5, 12, 3, 2, 1, closeDoor); //Orphanage right bedroom to hall
        
         addPort(6,7,8,0,7,8); //Path to first dungeon
        addPort(6,11,17,5,1,5); //Road to second forest map path
        addPort(6,11,17,6,1,6); //Road to second forest map path
        
        addPort(7,6,7,9,8,1); //First dungeon back to path
        addPort(7,6,8,9,9,1); //First dungeon back to path
        
        addPort(7,8,7,4,7,1); //First dungeon drop
        
        addPort(8,9,1,3,8,3); //First dungeon escape room
        
        addPort(8,10,16,3,2,4); //First dungeon treasury/boss room
        
        addPort(9,7,8,1,7,6); //First dungeon escape ladder
        addPort(9,8,10,3,3,3); //Escape to main dungeon teleport
        
        addPort(10,8,1,3,14,3); //First dungeon escape ladder
        
        addPort(11,12,17,5,1,5); //Road to third forest map path
        addPort(11,12,17,6,1,6); //Road to third forest map path
        
        addPort(11,6,0,5,16,5); //Road to third forest map path
        addPort(11,6,0,6,16,6); //Road to third forest map path
        
        addPort(12,11,0,5,16,5); //Road to first sand map path
        addPort(12,11,0,6,16,6); //Road to first sand map path
        
        addPort(12,13,8,9,8,1); //Path from crossroads to fishing area
        addPort(12,13,9,9,9,1); //Path from crossroads to fishing area
        addPort(12,13,10,9,10,1); //Path from crossroads to fishing area
        
        addPort(13,12,8,0,8,8); //Return to crossroads from fishing area
        addPort(13,12,9,0,9,8); //Return to crossroads from fishing area
        addPort(13,12,10,0,10,8); //Return to crossroads from fishing area
        
        addPort(13,14,8,9,8,1); //Path from crossroads to fishing area
        addPort(13,14,9,9,9,1); //Path from crossroads to fishing area
        addPort(13,14,10,9,10,1); //Path from crossroads to fishing area
        
        addPort(14,13,8,0,8,8); //Return to path from fishing area
        addPort(14,13,9,0,9,8); //Return to path from fishing area
        addPort(14,13,10,0,10,8); //Return to path from fishing area
        
        addPort(14,15,0,1,16,1); //Bridge to fishing area start
        addPort(14,15,0,2,16,2); //Bridge to fishing area start
        addPort(14,15,0,3,16,3); //Bridge to fishing area start
        addPort(14,15,0,4,16,4); //Bridge to fishing area start
        addPort(14,15,0,5,16,5); //Bridge to fishing area start
        
        addPort(15,14,17,1,1,1); //Return from main fishing area to path (left)
        addPort(15,14,17,2,1,1); //Return from main fishing area to path (left)
        addPort(15,14,17,3,1,1); //Return from main fishing area to path (left)
        addPort(15,14,17,4,1,1); //Return from main fishing area to path (left)
        addPort(15,14,17,5,1,1); //Return from main fishing area to path (left)
    }

    private void referencePlayer() {
        for (WorldMap a : maps) {
            a.getActor().add(player1);
        }
    }

    /**
     * Main game loop for starting the game. Call this method to start the game.
     *
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void init() throws InterruptedException, FileNotFoundException, IOException {
        loadTextures();
        initMaps();
        initActivators();
        initPlayer();
        loadPortals();

        loadSounds();
        referencePlayer();

        h = new Helper();
        h.toggleHidden();

        // Check whether we're running from a JDK or JRE install ...and set FontPath appropriately.
        if ((new File(JreFontPath)).exists()) {
            FontPath = JreFontPath;
        } else {
            FontPath = JdkFontPath;
        }

    }

    @Override
    public int run() {
        state = 1;
        int menuSleep = 15;
        mainTheme.play();
        

        mainTheme.setVolume(80);
        while (window.isOpen() && state == 1) {

//            if(subState = 2){
//
//            }


            if (!player1.movementLock) {
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
            }

            mainTheme.getStatus();
            if (window.isOpen()) {
                // Clear the screen
                window.clear(Color.WHITE);
            }

            // Starts a battle every 10 steps.
            if (battleChance >= 10) {
                battleChance = 0;
                mainTheme.pause();
                state = 2;
            }

            // Check for input (UP,DOWN,LEFT,RIGHT)
            if (menuSleep > 0) {
                menuSleep--;
            }
            //Draws underlay tiles
            for (WorldPiece ul : maps.get(worldNum).getUnder()) {
                ul.draw(window);
            }

            //Draws the backsground and main tiles.
            for (WorldPiece wp : maps.get(worldNum).getOver()) {
                wp.draw(window);
            }

            //Draws the "Foreground" objects to interact with including: player, barriers and npc.
            for (Actor actor : maps.get(worldNum).getActor()) {
                actor.calcMove(0, 0, screenWidth, screenHeight);
                actor.performMove();
                actor.draw(window);
            }

            for (MessageBox m : AddItem.messages) {
                if (m.hidden == false) {
                    m.draw(window);
                }
            }

            if (!trader.isHidden()) {
                trader.drawMessage(window);
            }
            
            
            

            //if (routeMessage != null && routeClock.getElapsedTime().asSeconds() <= 1.6) {
            if(routeMessage != null){
                routeMessage.draw(window);
                
            }
                
            //}

            if (h != null) {
                h.Draw(window);
            }
            for(TalkNPC n: npcs){
                n.drawMessage(window);                  
            }
                              

            // Update the display with any changes.
            window.display();

//            if (saveTimer.getElapsedTime().asSeconds() > 20) {
//                try {
//                    System.out.println("Saved");
//                    s = new Save(playerInv, player1, this, Activator.activators);
//                    Save.save("src/saves/save000", s);
//                } catch (IOException ex) {
//                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                saveTimer.restart();
//            }
            // Handle any events.
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    window.close(); // the user pressed the close button.
                }
                if (event.type == Event.Type.LOST_FOCUS) {
                    isMinimized = true;
                    window.setFramerateLimit(2); // Will set FPS to low if game is in background.
                    mainTheme.pause();
                } else if (event.type == Event.Type.GAINED_FOCUS) {
                    isMinimized = false;
                    window.setFramerateLimit(60);
                    mainTheme.play();
                }
                if (event.type == Event.Type.KEY_PRESSED && player1.isMoving() == false) {
                    if (Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && menuSleep <= 0) {
                        //mainTheme.pause();
                        state = 3;
                    }

                }

            }
        }
        mainTheme.setVolume(mainTheme.getVolume() / 2);
        //int peter = 2;
        return state;
    }

}
