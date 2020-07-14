import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CastParser {

    private Document castDom;
    private Document actorDom;
    private PrintWriter starsWriter;
    private PrintWriter simWriter;
    private PrintWriter inconsistentReportWriter;
    private HashMap<String, String> existedStars;
    private HashSet<String> existedSim;


    public CastParser() {
    }

    public void run() {
        try {
            File path = new File("ParsedFiles");
            if (!path.exists()){
                path.mkdir();
            }
            starsWriter = new PrintWriter(new File("ParsedFiles/stars.txt"));
            simWriter = new PrintWriter(new File("ParsedFiles/sim.txt"));
            inconsistentReportWriter = new PrintWriter(new File("ParsedFiles/castsInconsistentDataReport.txt"));

            parseXmlFile();
            getData();
            parseActors();
            parseCasts();

            starsWriter.close();
            simWriter.close();
            inconsistentReportWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            castDom = db.parse("stanford-movies/casts124.xml");
            actorDom = db.parse("stanford-movies/actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseActors(){
        Element docEle = actorDom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("actor");
        int actorNum = 0;
        if(nl != null && nl.getLength() > 0)
        {
            for(int i = 0; i < nl.getLength(); i++)
            {
                Element actor = (Element) nl.item(i);
                String name = getTextValue(actor,"stagename");
                String birthYear = getTextValue(actor,"dob");
                System.out.println("Parsing Actor No."+actorNum);
                if (name==null){
                    System.out.println("ActorParser Inconsistent Data: Missing Star Name");
                    inconsistentReportWriter.write("ActorParser at Actor No."+actorNum+" Name="+name+": Missing Star Name\n");
                } else {
                    if (!existedStars.containsKey(name)){
                        existedStars.put(name, generateStarId(name));
                        starsWriter.write("'"+ existedStars.get(name) + "','" + name + "'," + birthYear + "\n");
                    } else {
                        System.out.println("ActorParser Duplicate Data: Star '"+name+"' already exist");
                        inconsistentReportWriter.write("ActorParser at Actor No."+actorNum+" Name="+name+": Star '"+name+"' already exist\n");
                    }
                }
                actorNum++;
                if(birthYear==null){System.out.println("ActorParser Inconsistent Data: Missing Star Birth Year");
                    inconsistentReportWriter.write("ActorParser at Actor No."+actorNum+" Name="+name+": Missing Birth Year\n");}
            }
        }
        System.out.println("Actors Parsing Complete");
    }

    private void parseCasts(){
        Element docEle = castDom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("filmc");
        int indexNum = 0;
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element cast = (Element) nl.item(i);//
                NodeList references  = cast.getElementsByTagName("m");
                if(references != null && references.getLength() > 0)
                {
                    for(int j = 0; j < references.getLength(); j ++ )
                    {
                        Element item = (Element) references.item(j);
                        String mid = getTextValue(item, "f");
                        System.out.println("Parsing Cast No."+indexNum);
                        if (mid==null){
                            System.out.println("CastParser Inconsistent Data: Missing Movie ID");
                            inconsistentReportWriter.write("CastParser at Index No."+indexNum+": Missing MovieId\n");
                        } else {
                            String star = getTextValue(item,"a");
                            if (star==null){
                                System.out.println("CastParser Inconsistent Data: Missing Star Name");
                                inconsistentReportWriter.write("CastParser at Index No."+indexNum+": Missing Star Name\n");
                            } else {
                                if (!existedStars.containsKey(star)) {
                                    existedStars.put(star, generateStarId(star));
                                } else {
                                    System.out.println("CastParser Duplicate Data: Star '"+star+"' already exist");
                                    inconsistentReportWriter.write("CastParser at Index No."+indexNum+": Star '"+star+"' already exist\n");
                                }
                                if (existedSim.contains(existedStars.get(star)+mid)){
                                    System.out.println("CastParser Duplicate Data: Star_in_Movie already exist");
                                    inconsistentReportWriter.write("CastParser at Index No."+indexNum+": Star_in_Movie already exist\n");
                                } else {
                                    simWriter.write("'" + existedStars.get(star) + "','" + mid + "'\n");
                                }
                            }
                        }
                        indexNum++;
                    }
                }
            }
        }
        System.out.println("Casts Parsing Complete");
    }

    private void getData(){
        existedStars = new HashMap<String, String>();
        existedSim = new HashSet<String>();
        try{
            String loginUser = "mytestuser";
            String loginPasswd = "mypassword";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query = "select id, name from stars;";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                existedStars.put(rs.getString("name"), rs.getString("id"));
            }
            rs.close();
            statement.close();
            String query3 = "select * from stars_in_movies;";
            Statement statement3 = connection.createStatement();
            ResultSet rs3 = statement3.executeQuery(query3);
            while (rs3.next()){
                existedSim.add(rs3.getString("starId")+rs3.getString("movieId"));
            }
            rs3.close();
            statement3.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error during connecting to Database");
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        try {
            if (nl != null && nl.getLength() > 0) {
                Element el = (Element) nl.item(0);
                textVal = el.getFirstChild().getNodeValue();
            }
        } catch (Exception e) {
            return textVal;
        }
        return textVal;
    }

    private String generateStarId(String star){
        String a = String.valueOf(star.hashCode());
        if (a.startsWith("-")){
            a = a.substring(1);
        }
        if (a.length()>10){
            return a.substring(0,11);
        }
        return a;
    }

    public static void main(String[] args) {
        CastParser cp = new CastParser();
        cp.run();
    }
}
