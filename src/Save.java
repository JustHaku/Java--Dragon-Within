
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class Save implements Serializable {

    static final long serialVersionUID = 42L;

    private int worldNum;
    private int x;
    private int y;
    private Inventory i;
    private ArrayList<Boolean> active = new ArrayList<>();
    private ArrayList<Boolean> active2 = new ArrayList<>();
    public ArrayList<Character> team = new ArrayList<>();
    ArrayList<ArrayList<Object>> stats = new ArrayList<ArrayList<Object>>();

    public Save(Inventory i, Player p, Game g, ArrayList<Activator> a, ArrayList<ScriptedNPC> ad, ArrayList<Character> team) {
        worldNum = g.getWorldNum();
        x = p.getX();
        y = p.getY();
        this.i = i;
        /*this.team = team;*/
        for(Character k : team){
            stats.add(k.collectStats());
        }
        for (Activator k : a) {
            active.add(k.activated);
        }
        for (ScriptedNPC k : ad) {
            active2.add(k.hadItems);
        }
    }

    public int getWorld() {
        return worldNum;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Inventory getInventory() {
        return i;
    }

    public ArrayList<Boolean> getID() {
        return active;
    }
    
    public ArrayList<Boolean> getID2() {
        return active2;
    }

    public static void save(String fn, Save s) throws FileNotFoundException, IOException {
        try {
            Files.delete(Paths.get(fn));

        } catch (NoSuchFileException e) {

        }

        Files.createFile(Paths.get(fn));

        FileOutputStream fout = new FileOutputStream(fn);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(s);
        fout.close();
    }

    public static Save load(String fn) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(fn);
        ObjectInputStream ois = new ObjectInputStream(fin);
        Save s = (Save) ois.readObject();
        fin.close();

        return s;
    }
}
