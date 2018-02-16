
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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

    static Skills.myBiConsumer<Character, Integer> manapay = (character, value) -> {
        if (character.mana - value < 0) {
            throw new Exception();
        }
        character.mana -= value;
    };

    static Skills.myBiConsumer<Character, Integer> actionpay = (character, value) -> {
//        if (character.actionp - value < 0) {
//            throw new Exception();
//        }
//        character.actionp-=value;
    };

//    public static Skills heal = new Skills("Heal",
//            30, 40,//costs: 30; the effected stat is changed by 40
//            (character, value) -> {/*character.reduceManaBy(value)*/
//            },// a function describing how the character performming the skill is going to pay for the skill
//            (character, value) -> {/*character.increaseHealthby(value) */
//            },// a function describing how the skill is going affect the character that the skill is applied on
//            false, false); // skill is not unary; the effects of it cannot be reverted after the battle ends
//
//    public static Skills fireball = new Skills("Fireball",
//            50, 60,//
//            (character, value) -> {/*character.reduceManaBy(value)*/
//            },
//            (character, value) -> {/*character.reduceHealthBy(value)*/
//            }, false, false);
//
//    public static Skills speedbuff = new Skills("Speed Up",
//            20, 40,//
//            (character, value) -> {/*character.reduceActionPointsBy(value)*/
//            },//
//            (character, value) -> {/*character.increaseSpeedBy(value)*/
//            },//
//            true, true);// skill is performed on the character casting it; the buff is removed after battle
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
                Element skill = (Element) skills.item(i);
                //get attributes for a skill
                int cost = Integer.parseInt(skill.getAttribute("cost"));
                String payment = skill.getAttribute("payment");
                int damage = Integer.parseInt(skill.getAttribute("damage"));
                String type = skill.getAttribute("type");
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

                Skills.myBiConsumer<Character, Integer> wayofpaying = (payment.equals("mana")) ? manapay : actionpay;
                Skills.myBiConsumer<Character, Integer> wayofapp = (character, value) -> {
                    character.takeDamage(type, value);
                    // chracter.takeDamage
                };

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

                skillslist.add(skl);
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new Exception();
        }
        return skillslist;
    }

    public static void main(String[] args) {
        try {
            List<Skills> skills = readSkills("skills.xml");
            Character fr = new Character();
            fr.mana = 0;
            fr.health = 100;
            fr.attack = 60;
            fr.defence = 30;

            Character en = new Character();
            en.mana = 50;
            en.health = 100;
            en.attack = 60;
            en.defence = 30;

            Skills sk = skills.get(0);
            sk.teachTo(fr);
            sk.applyTo(en);
            sk.executeSkill();

            System.out.println("BasicSkills.main()");

        } catch (Exception ex) {
            Logger.getLogger(BasicSkills.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
