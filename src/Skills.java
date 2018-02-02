
import java.util.function.BiConsumer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class Skills {

    private String name;
    private String description;
    private int cost;
    private int value;

    private Character applier;
    private Character applyee;
    private BiConsumer<Character, Integer> consuming;
    private BiConsumer<Character, Integer> applying;
    public final boolean unary;
    private boolean revertable;

    public Skills(String name, int cost, int value, BiConsumer<Character, Integer> wayofpaying, BiConsumer<Character, Integer> wayofapplying, boolean isUnary,boolean isRevertable) {
        this.name = name;
        unary = isUnary;
        revertable = isRevertable;
        
        if(unary){
            applyee = applier;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void applyTo(Character applyee) {
        this.applyee = applyee;
    }
    
    public void teachTo(Character dude){
        /*
            applier = dude
            dude.addSkill(this)\dude.learn(this)    
        */
    }
    
    public void revert() {
        if (revertable){
            applying.accept(applyee, -value);
        }
        if(!unary){
            applyee = null;
        }
    }

    public void executeSkill() {
        consuming.accept(applier, cost);
        applying.accept(applyee, value);
    }
}
