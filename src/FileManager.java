
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class FileManager {

    public FileManager() {

    }

    public static void save(String fn, Save g) throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream(fn);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(g);
        fout.close();
    }

    public static Game read(String fn) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(fn);
        ObjectInputStream ois = new ObjectInputStream(fin);
        Game g = (Game) ois.readObject();
        fin.close();

        return g;
    }

}
