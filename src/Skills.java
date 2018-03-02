
import java.io.Serializable;

/**
 *
 * @author LBals
 */
public class Skills implements Serializable {

    static final long serialVersionUID = 42L;

    public static int ALL = 0;
    public static int FRIENDLY = 1;
    public static int ENEMY = -1;

    private final String name;
    private String description;
    private String posteffect;
    private int cost;
    private int value;

    private Character applier;
    private final Character[] targets;
    private final myBiConsumer<Character, Integer> consuming;
    private final myBiConsumer<Character, Integer> applying;
    public final int affects;

    public final boolean unary;
    public final boolean revertable;
    private boolean damaging = false;

    /**
     * Creates a new skill
     *
     * @param name of the skill
     * @param cost cost for allying the skill
     * @param value of the effect of the skill
     * @param wayofpaying way of paying for the skill
     * @param wayofapplying way of applying the skill
     * @param affects the types of character it affects(use the static constant
     * in this class to pass here)
     * @param affected number of characters that this skill affects
     * @param isUnary true if the skills is applied on the character casting it
     * @param isRevertable if the skill can be reverted
     */
    private Skills(String name, int cost, int value, myBiConsumer<Character, Integer> wayofpaying,
            myBiConsumer<Character, Integer> wayofapplying, int affects, int affected, boolean isUnary, boolean isRevertable) {

        this.name = name;
        this.cost = cost;
        this.value = value;

        this.consuming = wayofpaying;
        this.applying = wayofapplying;

        revertable = isRevertable;

        unary = isUnary;
        if (unary) {
            targets = new Character[1];
        } else {
            targets = new Character[affected];
        }

        this.affects = affects;
    }

    /**
     * Creates a new skill that is not unary
     *
     * @param name of the skill
     * @param cost cost for allying the skill
     * @param value of the effect of the skill
     * @param wayofpaying way of paying for the skill
     * @param wayofapplying way of applying the skill
     * @param affects the types of character it affects(use the static constant
     * in this class to pass here)
     * @param affected number of characters that this skill affects
     * @param isRevertable if the skill can be reverted
     */
    public Skills(String name, int cost, int value, myBiConsumer<Character, Integer> wayofpaying, myBiConsumer<Character, Integer> wayofapplying, int affects, int affected, boolean isRevertable) {
        this(name, cost, value, wayofpaying, wayofapplying, affects, affected, false, isRevertable);
    }

    /**
     * Creates a new skill that is unary
     *
     * @param name of the skill
     * @param cost cost for allying the skill
     * @param value of the effect of the skill
     * @param wayofpaying way of paying for the skill
     * @param wayofapplying way of applying the skill
     * @param isRevertable if the skill can be reverted
     */
    public Skills(String name, int cost, int value, myBiConsumer<Character, Integer> wayofpaying, myBiConsumer<Character, Integer> wayofapplying, boolean isRevertable) {
        this(name, cost, value, wayofpaying, wayofapplying, Skills.FRIENDLY, 1, true, isRevertable);
    }

    /**
     * Return name of the skill
     *
     * @return name of the skill
     */
    public String getName() {
        return name;
    }

    /**
     * Returns description of the skill. IMPORTANT call this after executeSkill
     * and before unBindAll
     *
     * @return description of the skill;
     */
    public String getDescription() {
        return description;
    }

    public String getPostEffectText() {
        StringBuilder bul = new StringBuilder();

        for (Character c : targets) {
            String temp = posteffect.replaceFirst("!", Integer.toString(Math.abs(value)));
            bul.append(c.name).append(temp).append("\n");
        }

        bul.deleteCharAt(bul.length() - 1);

        return bul.toString();
    }

    /**
     * Adds a target that this skill needs to be applied to
     *
     * @param applyee character that this skill needs to be applied to
     */
    public void addTarget(Character applyee) {

        for (int i = 0; i < this.targets.length; i++) {
            if (this.targets[i] == null) {
                this.targets[i] = applyee;
                break;
            }
        }
    }

    /**
     * Removes all references to the characters that the skill would be casted
     * on.
     */
    public void unBindAll() {
        if (!unary) {
            for (int i = 0; i < this.targets.length; i++) {
                this.targets[i] = null;
            }
        }
    }

    /**
     * sets a description for the skill
     *
     * @param desc
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Sets the post effect description of the skill
     *
     * @param post effect description
     */
    public void setPostEffectText(String post) {
        this.posteffect = post;
    }

    /**
     * use one of the static variables(ALL, FRIENLY, ENEMY) to determine on
     * which characters can this be casted
     *
     * @param i one of [ALL, FRIENLY, ENEMY]
     * @return true if can affect i
     */
    public boolean doesitAffect(int i) {
        if (affects == ALL) {
            return true;
        } else {
            return affects == i;
        }
    }

    /**
     * gets the reversed version of the skill
     *
     * @return
     */
    public Runnable getReverted() {
        return () -> {
            try {
                for (Character ap : targets) {
                    applying.accept(ap, -(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

    }

    /**
     * Checks if the skill requires the selection of more targets
     *
     * @return true if targets are needed
     */
    public boolean needsMoreTargets() {
        return targets[targets.length - 1] == null;
    }

    /**
     * teaches a skill to a character
     *
     * @param dude
     */
    public void teachTo(Character dude) {
        applier = dude;
        dude.addSkill(this);
    }

    /**
     * Checks whether the caster of the skill has enough recourses to cast it
     *
     * @return true if it can be casted
     */
    public boolean canItbeCast() {
        try {
            consuming.accept(applier, cost);
            consuming.accept(applier, -cost);
            return true;
        } catch (SkillExeption e) {
            return false;
        }
    }

    /**
     * executes the skill on the characters that were added by the
     * {@link Skills.applyTo}
     *
     * @throws Skills.SkillExeption when the skill cannot be casted
     */
    public String executeSkill() throws SkillExeption {
        consuming.accept(applier, cost);

        //for damaging abilities the value retrieved from the xml file acts as a percentage
        if (damaging) {
            value = (applier.totalDmg() * value / 100) + applier.totalDmg();
        }
        StringBuilder bul = new StringBuilder();
        try {
            // apply the effect on each target
            for (Character ap : targets) {
               int effvalue = applying.accept(ap, value);
               String temp = posteffect.replaceFirst("!", Integer.toString(Math.abs(effvalue)));
                bul.append(ap
                        .name).append(temp).append("\n");
            }
            bul.deleteCharAt(bul.length() - 1);
        } catch (NullPointerException e) {
            throw new SkillExeption("Not enough targets");
        }
        return bul.toString();
    }

    /**
     * Sets a flag that states that the skill would reduce health points
     *
     * @param d true if the skill should take hp
     */
    public void setDamaging(boolean d) {
        this.damaging = d;
    }

    /**
     * Used for describing the way of payment and away of applying of the skill
     *
     * @param <T> A character that is going to pay for the skill or going to be
     * a target of the skill;
     * @param <U> Value of the skill
     */
    @FunctionalInterface
    public static interface myBiConsumer<T extends Character, U extends Integer> {

        int accept(T t, U u) throws SkillExeption;
    }

    /**
     * Exception for when the caster can't cast the skill. Examples: when the
     * caster doesn't have enough points to cast it; when there aren't enough
     * targets for the skill to be cast
     */
    public static class SkillExeption extends Exception {

        public SkillExeption(String s) {
            super(s);
        }
    }
}
