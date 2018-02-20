
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author valka
 */
public class BasicSkills {

    /**
     * this holds all the lambda expressions that relate to ways of paying for
     * the skill and ways of applying the skill
     */
    private final static HashMap<String, Skills.myBiConsumer<Character, Integer>> SKILLMAP = new HashMap<>();

    //fill in the skillmap
    static {
        SKILLMAP.put("heal", (character, value) -> {
        });

        SKILLMAP.put("manareg", (character, value) -> {
        });

        SKILLMAP.put("deffbuff", (character, value) -> {
        });

        SKILLMAP.put("speedbuff", (character, value) -> {
        });

        SKILLMAP.put("mana", (character, value) -> {

            if (character.mana - value < 0) {
                throw new Exception("mana " + character.mana + " value " + value + "result " + (character.mana - value));
            }
            character.mana -= value;
        });

        SKILLMAP.put("action", (character, value) -> {
        });
    }

    /**
     * Creates a new biconsumer and passes the type of the damage to it
     *
     * @param element string describing damage
     * @return
     */
    private static Skills.myBiConsumer<Character, Integer> getTypedDamageSkill(String element) {
        return (t, u) -> {
            t.takeDamage(element, u);
        };
    }

    private Document skillDocument;

    /**
     * Creates a new basic skills class that can retrieve skills from an xml
     * file/
     *
     * @param path of the xml file
     */
    public BasicSkills(String path) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            skillDocument = docBuilder.parse(new File(path));
            skillDocument.normalize();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance of a skill by reading it from the loaded xml file
     *
     * @param name name of the skill in the file
     * @return a skill instance; null if skill is not present
     */
    public Skills getNewSkillInstance(String name) {
        try {
            Node root = this.skillDocument.getElementsByTagName("skills").item(0);

            XPath xpath = XPathFactory.newInstance().newXPath();

            XPathExpression xpathexp = xpath.compile("//*[@name='" + name + "']");
            NodeList nl = (NodeList) xpathexp.evaluate(root, XPathConstants.NODESET);

            return skillFromNode(nl.item(0));
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Creates a skill from an xml node
     *
     * @param sk node containing the skill
     * @return
     */
    private static Skills skillFromNode(Node sk) {
        Element skill = (Element) sk;

        int cost = Integer.parseInt(skill.getAttribute("cost"));
        String payment = skill.getAttribute("payment");
        int damage = Integer.parseInt(skill.getAttribute("value"));
        String type = skill.getAttribute("type");
        String affway = skill.getAttribute("afftype");
        String affect = skill.getAttribute("affect");
        int affected = Integer.parseInt(skill.getAttribute("affected"));
        boolean unary = Boolean.parseBoolean(skill.getAttribute("unary"));
        boolean revertable = Boolean.parseBoolean(skill.getAttribute("revertable"));

        // get name
        Element child = (Element) skill.getElementsByTagName("name").item(0);
        String stitle = child.getTextContent();
        // get description
        child = (Element) skill.getElementsByTagName("description").item(0);
        String descString = child.getTextContent();

        Skills.myBiConsumer<Character, Integer> wayofpaying = SKILLMAP.get(payment);

        // get the way of applying this skill
        Skills.myBiConsumer<Character, Integer> wayofapp;
        if (affway.equals("damage")) {
            wayofapp = getTypedDamageSkill(type);
        } else {
            wayofapp = SKILLMAP.get(affway);
        }

        // get the types of character it affects
        int aff;
        switch (affect) {
            case "all":
                aff = Skills.ALL;
                break;
            case "friendly":
                aff = Skills.FRIENDLY;
                break;
            default:
                aff = Skills.ENEMY;
                break;
        }

        //make skill and add description
        Skills skl = new Skills(stitle, cost, damage, wayofpaying, wayofapp, aff, affected, unary, revertable);
        skl.setDescription(descString);

        return skl;
    }

    /**
     * Read all the skills stored on an xml file
     *
     * @param path of the xml file
     * @return a list of all the skills
     * @throws Exception for a lot of reasons
     *
     */
    public static ArrayList<Skills> readSkills(String path) throws Exception {
        ArrayList<Skills> skillslist = new ArrayList<>();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new File("skills.xml"));
            document.normalize();

            NodeList rootNodes = document.getElementsByTagName("skills");
            Element e = (Element) rootNodes.item(0);

            NodeList skills = e.getElementsByTagName("skill");

            for (int i = 0; i < skills.getLength(); i++) {
                //make skill and add description
                Skills skl = skillFromNode(skills.item(i));
                skillslist.add(skl);
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new Exception();
        }
        return skillslist;
    }

    public static void main(String[] args) {
        try {
            BasicSkills bsk = new BasicSkills("skills.xml");
            Skills fireball = bsk.getNewSkillInstance("fireball");
            System.out.println(fireball.getName());

//            
//            List<Skills> skills = readSkills("skills.xml");
            Character fr = new Character();
            fr.mana = 100;
            fr.health = 100;
            fr.attack = 60;
            fr.defence = 30;

            Character en = new Character();
            en.mana = 50;
            en.health = 100;
            en.attack = 60;
            en.defence = 30;

            Skills sk = fireball;
            sk.teachTo(fr);
            sk.addTarget(en);
            sk.executeSkill();

//            System.out.println("BasicSkills.main()");
        } catch (Skills.NotEnoughResourcesToCastException | Skills.NotEnoughSelectedException ex) {
            Logger.getLogger(BasicSkills.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
