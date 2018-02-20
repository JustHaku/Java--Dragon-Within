
/**
 *
 * @author LBals
 */
public class Skills {

    public static int ALL = 0;
    public static int FRIENDLY = 1;
    public static int ENEMY = -1;

    private String name;
    private String description;
    private int cost;
    private int value;

    Character applier;
    private Character[] targets;
    private myBiConsumer<Character, Integer> consuming;
    private myBiConsumer<Character, Integer> applying;
    public final int affects;

    public final boolean unary;
    public final boolean revertable;

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
    public Skills(String name, int cost, int value, myBiConsumer<Character, Integer> wayofpaying,
            myBiConsumer<Character, Integer> wayofapplying, int affects, int affected, boolean isUnary, boolean isRevertable) {
        this.name = name;
        this.cost = cost;
        this.value = value;

        this.consuming = wayofpaying;
        this.applying = wayofapplying;

        unary = isUnary;
        revertable = isRevertable;

        this.targets = new Character[affected];
        this.affects = affects;
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
     * Returns description of the skill;
     *
     * @return description of the skill;
     */
    public String getDescription() {
        return description;
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
        for (int i = 0; i < this.targets.length; i++) {
            this.targets[i] = null;
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
     * use on of the static variables(ALL, FRIENLY, ENEMY) to determine on which
     * characters can this be casted
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
    public Skills getReverted() {
        if (revertable) {
            this.value *= -1;
            try {
                Skills clone = (Skills) this.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                return null;
            } finally {
                value *= -1;
            }
        }
        return null;
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
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * executes the skill on the characters that were added by the
     * {@link Skills.applyTo}
     *
     * @throws Skills.NotEnoughSelectedException when there aren't enough
     * characters selected to cast the skill on
     * @throws Skills.NotEnoughResourcesToCastException when the caster has not
     * got enough resources to cast the spell
     */
    public void executeSkill() throws NotEnoughSelectedException, NotEnoughResourcesToCastException {
        try {
            System.out.println(this.canItbeCast());
            consuming.accept(applier, cost);
        } catch (Exception ex) {
            throw new NotEnoughResourcesToCastException();
        }

        try {
            for (Character ap : targets) {
                applying.accept(ap, applier.attack + value);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     *
     * @param <T>
     * @param <U>
     */
    @FunctionalInterface
    public static interface myBiConsumer<T extends Character, U extends Integer> {

        void accept(T t, U u) throws Exception;
    }

    /**
     * Exception for when the the skill needs more characters to be selected
     */
    public static class NotEnoughSelectedException extends Exception {

        public NotEnoughSelectedException() {
            super("Not enough characters were selected");
        }
    }

    /**
     * Exception for when the character can't cast the spell because there isn't
     * enough mana
     */
    public static class NotEnoughResourcesToCastException extends Exception {

        public NotEnoughResourcesToCastException() {
            super("not enough resources to cast this skill");
        }
    }
}
