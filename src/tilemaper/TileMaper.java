/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tilemaper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author valka
 */
public class TileMaper extends JFrame {

    //holds the displaers
    private final TileDisplayer[][] displayers;

    //tab pane and list of the selectors on it 
    private JTabbedPane tabPane = new JTabbedPane();
    private final ArrayList<TileSelector> selectors;

    // radio buttons
    private JRadioButton[] rbs;
    private String[] rbls = {"Underlay", "Overlay", "Actor"};

    private JMenuBar menuBar = new JMenuBar();

    private TileTuple selected;
    private int selectedTileLayer;

    private File openedFile;
    private HashMap<Integer, TileTuple> spritesheet;
    private String openedSprite;

    public TileMaper(String configFile) throws IOException, UnsupportedLookAndFeelException {
        super("Tile Mapper");

        // initializing class fileds
        displayers = new TileDisplayer[Configs.displayer_height][Configs.displayer_width];
        selectors = new ArrayList<>();

        selected = new TileTuple(null, -1, -1);
        selectedTileLayer = TileDisplayer.UNDERLAY;
        spritesheet = new HashMap<>();
        loadSprites("./src/tilemaper/roguelikeSheet_transparent.png");

        //initializing ui
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        super.setVisible(true);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //content pane panel
        JPanel cpPanel = new JPanel(new BorderLayout());
        super.getContentPane().add(cpPanel);

        initDisplayers(cpPanel, BorderLayout.WEST, Configs.displayer_width, Configs.displayer_height);
        initRBs(cpPanel, BorderLayout.NORTH);
        initMenuBar();

        cpPanel.add(tabPane, BorderLayout.EAST);
        tabPane.setPreferredSize(new Dimension(200, 500));

        initTabbedSelector("./src/tilemaper/roguelikeSheet_transparent.png", "Sprites");

        //make fullscreen
        super.setExtendedState(super.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public static void main(String[] args) {

        try {
            new TileMaper("./src/tilemaper/configs.properties");
        } catch (IOException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the currently selected tuple
     *
     * @param t new tuple
     */
    public void reselect(TileTuple t) {
        selected = t;
    }

    /**
     * Returns the currently selected tuple
     *
     * @return selected tuple
     */
    public TileTuple getSelected() {
        return selected;
    }

    /**
     * Loads tile tuples from a sprite sheet
     *
     * @return list of all tile tuples with images from the spites
     */
    void loadSprites(String path) {
        this.openedSprite = path;

        int sprH = Configs.SPRITE_HEIGHT;
        int sprW = Configs.SPRITE_WIDTH;
        int sprGap = Configs.SPRITE_GAP;

        int sprStartX = Configs.SPRITES_START_X;
        int sprStartY = Configs.SPRITES_START_Y;

        try {
            BufferedImage allSprite = ImageIO.read(new File(path));

            int k, l;
            k = l = 0;
            for (int j = sprStartY; j < allSprite.getHeight(); j += sprH + sprGap, k++) {

                for (int i = sprStartX; i < allSprite.getWidth(); i += sprW + sprGap, l++) {

                    this.spritesheet.put(encode(l, k), new TileTuple(allSprite.getSubimage(i, j, sprW, sprH), k, l));
                }
                l = 0;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, path + " is missing or improperly formated");
        }
    }

    /**
     * Returns the selected layer on which the images are going to be put
     *
     * @return layer number (should correspond to one of TileDisplayer's
     * constants)
     */
    int getSelectedLayer() {
        return selectedTileLayer;
    }

    /**
     * Changes the selected layer
     *
     * @param i number of the layer (use TileDisplayer constants)
     */
    private void selectLayer(int i) {
        selectedTileLayer = i;
    }

    /**
     * Initializes the radio buttons panel. The buttons are going to switch
     * between the layers of the map
     *
     * @param panel where the buttons' panel is going to be located (this has to
     * be a panel with a border layout)
     *
     * @param location on the border laid out panel
     */
    private void initRBs(JPanel panel, String location) {

        JPanel rbpanel = new JPanel();
        rbpanel.setPreferredSize(new Dimension(this.getWidth(), 30));
        ButtonGroup bg = new ButtonGroup();
        rbs = new JRadioButton[3];

        for (Integer i = 0; i < rbs.length; i++) {
            final JRadioButton rb = new JRadioButton(this.rbls[i]);
            rb.setActionCommand(i.toString());

            rb.addActionListener((e) -> {
                selectLayer(Integer.parseInt(rb.getActionCommand()));
            });

            bg.add(rb);
            rbpanel.add(rb, location);

            rbs[i] = rb;
        }

        rbs[0].setSelected(true);
        this.selectedTileLayer = Integer.parseInt(rbs[0].getActionCommand());

        panel.add(rbpanel);
    }

    /**
     * Creates a panel of tile-displayers and places it on a container panel
     *
     * @param panel container that the created panel is going to be placed (has
     * to be border laid out)
     * @param location on the panel
     * @param x number of tile_displayers in a row
     * @param y number of tile_displayers in a column
     */
    private void initDisplayers(JPanel panel, String location, int x, int y) {

        //set create a grid layout with no gap between the buttons
        GridLayout gl = new GridLayout(y, x);
        gl.setVgap(1);
        gl.setHgap(1);

        //make a panel for the buttons and give it size 
        JPanel dispPanel = new JPanel(gl);
        dispPanel.setPreferredSize(new Dimension(1100, 300));

        //initialize displayers adn put them in the panel
        for (TileDisplayer[] displayer : displayers) {
            for (int j = 0; j < displayer.length; j++) {
                displayer[j] = new TileDisplayer(this, selected);

                dispPanel.add(displayer[j]);
            }
        }

        //put the pannel with buttons onto its container
        panel.add(dispPanel, location);
    }

    /**
     * Filles {@code this.tabPane} with tabs containing buttons sprites
     *
     * @param path to the location of the sprite sheet
     * @param panel container that the created panel is going to be placed (has
     * to be border laid out)
     * @param location on the panel
     */
    private void initTabbedSelector(String path, String name) {

        JPanel somepanel = new JPanel();
        somepanel.add(new TileSelector(this, selected));
        tabPane.addTab("None", somepanel);

    }

    void addTab(HashMap<Integer, TileTuple> sprites, String name, Integer location, int down, int left) {
        JPanel selectorPanel = null;

        int k = 0;
        for (int i = 0; i < left; i++) {
            for (int j = 0; j < down; j++, k++) {
                //create a new tab whenever another is filled
                if (k % (Configs.tab_height * Configs.tab_width) == 0) {
                    selectorPanel = new JPanel(new GridLayout(Configs.tab_height, Configs.tab_width));
                    this.tabPane.addTab(name, selectorPanel);
                }

                TileSelector ts = new TileSelector(this, sprites.get(location + encode(i, j)));

                selectors.add(ts);
                selectorPanel.add(ts);
            }
        }
    }

    private void initMenuBar() {
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.addActionListener((e) -> {
            JFileChooser fc = new JFileChooser(".");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setDialogTitle("Save As");
            fc.setApproveButtonText("Save");

            if (fc.showOpenDialog(TileMaper.this) == JFileChooser.APPROVE_OPTION) {
                saveAs(fc.getSelectedFile(), "WorldMap");
            }
        });

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener((ActionEvent e) -> {
            if (openedFile != null) {
                saveMap(openedFile);
            } else {
                saveAs.doClick();
            }
        });

        JMenuItem openMap = new JMenuItem("Open Map");
        openMap.addActionListener((e) -> {
            JFileChooser fc = new JFileChooser(".");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setDialogTitle("Open File");

            if (fc.showOpenDialog(TileMaper.this) == JFileChooser.APPROVE_OPTION) {
                loadMap(fc.getSelectedFile());
            }
        });

        JMenuItem openSprite = new JMenuItem("Open Sprites");
        openSprite.addActionListener((e) -> {
            SSSelector selector = new SSSelector(this, openedSprite);
            while (selector.isClosed()) {

            }
        });

        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(openMap);
        fileMenu.add(openSprite);
        super.setJMenuBar(menuBar);
    }

    private void saveAs(File parent, String name) {
        File newfile = new File(parent, name);
        for (int i = 0; !newfile.mkdir(); i++) {
            newfile.renameTo(new File(parent, name + " " + i));
        }
        if (openedFile == null) {
            openedFile = newfile;
        }

        saveMap(newfile);
    }

    private void saveMap(File parentfile) {
        for (int i = 0; i < rbls.length; i++) {
            try {
                File file = new File(parentfile, rbls[i] + ".txt");
                if (!file.exists()) {
                    file.createNewFile();
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (TileDisplayer[] tds : this.displayers) {
                        for (TileDisplayer td : tds) {
                            String toBeWrote = "";

                            //special format padding
                            int y = td.getLayer(i).y;
                            if (y < 10 && y >= 0) {
                                toBeWrote += "0";
                            }
                            toBeWrote += y + " ";

                            //special format padding
                            int x = td.getLayer(i).x;
                            if (x < 10 && x >= 0) {
                                toBeWrote += "0";
                            }
                            toBeWrote += x + "   ";

                            writer.write(toBeWrote);
                        }
                        writer.newLine();
                    }
                }
            } catch (IOException | NullPointerException e) {
                JOptionPane.showMessageDialog(this, parentfile.getName() + " could not be saved properly./n Parts of it may have been saved");
            }
        }
    }

    private void loadMap(File parent) {
        //first extract the numbers and check if they are in the correct format
        this.openedFile = parent;
        ArrayList<ArrayList<Integer>> stuff = new ArrayList<>();
        for (int i = 0; i < rbls.length; i++) {
            File openfile = new File(parent, rbls[i] + ".txt");
            ArrayList<Integer> substuff = new ArrayList<>();
            stuff.add(substuff);
            try (Scanner reader = new Scanner(openfile)) {

                while (reader.hasNextInt()) {
                    int x = reader.nextInt();
                    int y = reader.nextInt();
                    substuff.add(encode(x, y));
                }

                if (substuff.size() != (displayers.length * displayers[0].length)) {
                    throw new NoSuchElementException();
                }
            } catch (FileNotFoundException | NoSuchElementException e) {
                JOptionPane.showMessageDialog(this, openfile.getName() + " is missing or improperly formated");
                return;
            }
        }

        //after that fill in the numbers into the tile diplayers and fetch corresponding images
        int displayerColumn, displayerRow, displayerLayerIndex;//k coresponds to the number of the current layer//j column
        displayerColumn = displayerRow = displayerLayerIndex = 0;
        for (ArrayList<Integer> layer : stuff) {
            for (Integer tile : layer) {
                TileTuple tt = spritesheet.get(tile);
                if (tile == -101) {
                    tt = new TileTuple(null, -1, -1);
                }

                displayers[displayerRow][displayerColumn].changeLayer(displayerLayerIndex, tt);
                if (displayerColumn == (displayers[displayerRow].length - 1)) {//next row of displayers 
                    displayerColumn = -1;
                    displayerRow++;
                }
                displayerColumn++;
            }

            displayerLayerIndex++;
            displayerColumn = displayerRow = 0;
        }

    }

    int encode(int x, int y) {
        return x * 100 + y;
    }

    private TileTuple decode(int dec) {
        return new TileTuple(null, dec / 100, dec % 100);
    }

    HashMap<Integer, TileTuple> getSprites() {
        return this.spritesheet;
    }
}
