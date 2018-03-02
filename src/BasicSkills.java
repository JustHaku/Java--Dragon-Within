
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
 * @author valka
 */
public class BasicSkills implements Serializable {

    static final long serialVersionUID = 42L;

    /**
     * this holds all the lambda expressions that relate to ways of paying for
     * the skill and ways of applying the skill
     */
    private final static HashMap<String, Skills.myBiConsumer<Character, Integer>> SKILLMAP = new HashMap<>();

    //fill in the skillmap
    static {
        //method for healing
        SKILLMAP.put("heal", (character, value) -> {
            int pre = character.health;
            character.heal(value);
            return Math.abs(character.health - pre);
        });
        //method for gaining mana
        SKILLMAP.put("manareg", (character, value) -> {
            int pre = character.mana;
            character.regen(value);
            return Math.abs(character.mana - pre);
        });
        //method for gaining deffecne points
        SKILLMAP.put("deffbuff", (character, value) -> {
            int pre = character.defence;
            character.defence = Math.max(0, character.defence + value);
            return Math.abs(character.defence - pre);
        });
        //method for gaining speed points
        SKILLMAP.put("speedbuff", (character, value) -> {
            character.speed += value;
            return value;
        });

        //method for paying with mana
        SKILLMAP.put("mana", (character, value) -> {
            int pre = character.mana;
            if (character.mana - value < 0) {
                throw new Skills.SkillExeption("insufficient mana to cast");
            }
            character.mana -= value;
            return Math.abs(character.mana - pre);
        });

//        SKILLMAP.put("action", (character, value) -> {
//
//        });
    }

    /**
     * Creates a new biconsumer and passes the type of the damage to it
     *
     * @param element string describing damage
     * @return
     */
    private static Skills.myBiConsumer<Character, Integer> getTypedDamageSkill(String element) {
        return (Character t, Integer u) -> {
            int pre = t.health;
            t.takeDamage(element, u);
            return Math.abs(t.health - pre);
        };
    }

    /**
     * Hold a reference to the xml file that has the skills in it
     */
    private Document skillDocument;

    /**
     * Creates a new basic skills class that can retrieve skills from an xml
     * file
     *
     * @param path of the xml file
     * @throws java.io.IOException when the file is not on the selected path or
     *                             the file doesn't have the right format
     */
    public BasicSkills(String path) throws IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            skillDocument = docBuilder.parse(new File(path));
            skillDocument.normalize();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("The selected file is not a skills file");
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

        //get the cost, way of payment and value of the skill
        int cost = Integer.parseInt(skill.getAttribute("cost"));
        String payment = skill.getAttribute("payment");
        int damage = Integer.parseInt(skill.getAttribute("value"));

        //get the way that it affects a target and the type of the target
        String affway = skill.getAttribute("affway");

        //get if tis unary and if it's revertable
        boolean unary = Boolean.parseBoolean(skill.getAttribute("unary"));
        boolean revertable = Boolean.parseBoolean(skill.getAttribute("revertable"));

        // get name
        Element child = (Element) skill.getElementsByTagName("name").item(0);
        String stitle = child.getTextContent();
        // get description
        child = (Element) skill.getElementsByTagName("description").item(0);
        String descString = child.getTextContent();
        // getting post effect description
        child = (Element) skill.getElementsByTagName("post").item(0);
        String post = child.getTextContent();

        // getting the way that the skill is going to be payed for
        Skills.myBiConsumer<Character, Integer> wayofpaying = SKILLMAP.get(payment);

        // get the way of applying this skill
        Skills.myBiConsumer<Character, Integer> wayofapp;
        if (affway.equals("damage")) {
            wayofapp = getTypedDamageSkill(skill.getAttribute("type"));
        } else {
            wayofapp = SKILLMAP.get(affway);
        }

        // create a skill depending on if it's unary
        Skills skl;
        if (unary) {
            skl = new Skills(stitle, cost, damage, wayofpaying, wayofapp, revertable);
        } else {
            // get the number of affected and the type of affected(fiendly/enemy)
            int affected = Integer.parseInt(skill.getAttribute("affected"));
            String affect = skill.getAttribute("affect");

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
            skl = new Skills(stitle, cost, damage, wayofpaying, wayofapp, aff, affected, revertable);
        }
        //add descriptions
        skl.setDescription(descString);
        skl.setPostEffectText(post);
        skl.setDamaging(affway.equals("damage"));

        return skl;
    }

    /**
     * Read all the skills stored on an xml file
     *
     * @param path of the xml file
     * @return a list of all the skills
     * @throws Exception for a lot of reasons
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
}
