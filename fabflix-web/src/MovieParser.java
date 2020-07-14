import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Connection;

public class MovieParser {

    private Document dom;
    private PrintWriter moviesWriter;
    private PrintWriter genresWriter;
    private PrintWriter gimWriter;
    private PrintWriter inconsistentReportWriter;
    private int lastId;
    private HashMap<String,String> existedGenres;
    private HashSet<String> existedMovies;
    private HashSet<String> existedGim;
    private HashMap<String, String> genreTranslator;


    public MovieParser(){
    }

    public void run() {
        try {
            File path = new File("ParsedFiles");
            if (!path.exists()){
                path.mkdir();
            }
            moviesWriter = new PrintWriter(new File("ParsedFiles/movies.txt"));
            genresWriter = new PrintWriter(new File("ParsedFiles/genres.txt"));
            gimWriter = new PrintWriter(new File("ParsedFiles/gim.txt"));
            inconsistentReportWriter = new PrintWriter(new File("ParsedFiles/moviesInconsistentDataReport.txt"));

            parseXmlFile();
            getData();
            parseDocument();

            moviesWriter.close();
            genresWriter.close();
            gimWriter.close();
            inconsistentReportWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("stanford-movies/mains243.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument(){
        Element docEle = dom.getDocumentElement();
        NodeList directorList = docEle.getElementsByTagName("directorfilms");
        int movieNum = 0;
        int newId = lastId+1;
        if (directorList != null && directorList.getLength() > 0) {
            for (int i = 0; i < directorList.getLength(); i++) {
                Element el = (Element) directorList.item(i);
                String director = getTextValue(el, "dirname");
                NodeList movieCollection = el.getElementsByTagName("films");
                if(movieCollection != null && movieCollection.getLength() > 0) {
                    for (int n = 0; n < movieCollection.getLength(); n++) {
                        Element movie = (Element) movieCollection.item(n);
                        NodeList movieList = movie.getElementsByTagName("film");
                        if(movieList != null && movieList.getLength() > 0) {
                            for (int k = 0; k < movieList.getLength(); k++) {
                                Element m = (Element) movieList.item(k);
                                String mid = getTextValue(m,"fid");
                                String title = getTextValue(m,"t");
                                String d = getTextValue(m, "dirn");
                                if (d==null){
                                    d = director;
                                }
                                String year = getTextValue(m,"year");
                                NodeList genres = m.getElementsByTagName("cats");
                                System.out.println("Parsing Movie No."+movieNum + " ID: "+mid);
                                movieNum++;
                                if(genres != null && genres.getLength() > 0) {
                                    for (int b = 0; b < genres.getLength(); b++) {
                                        Element g = (Element) genres.item(b);
                                        String genre = getTextValue(g,"cat");
                                        if (genre!=null){
                                            String[] gs = genre.trim().toUpperCase().split(" ");
                                            for (String gg : gs){
                                                if (genreTranslator.containsKey(gg)){gg = genreTranslator.get(gg);}
                                                if (!existedGenres.containsKey(gg.toUpperCase())){
                                                    existedGenres.put(gg.toUpperCase(), Integer.toString(newId));
                                                    newId++;
//                                                    genresWriter.write("'"+gg+ "','" + existedGenres.get(gg.toUpperCase())+"'\n");
                                                    genresWriter.write(existedGenres.get(gg.toUpperCase())+",'"+gg+"'\n");
                                                } else {
                                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Genre '"+gg+"' already exist\n");
                                                    System.out.println("MovieParser Duplicate Data: Genre '"+gg+"' already exist");
                                                }
                                                if (mid!=null){
                                                    String gid = existedGenres.get(gg.toUpperCase());
                                                    if (!existedGim.contains(gid+mid)){
                                                        gimWriter.write("" + gid + ",'" + mid + "'\n");
                                                    } else {
                                                        inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Genre '"+gg+"' is alreadt in_Movie '"+mid+"'\n");
                                                        System.out.println("MovieParser Duplicate Data: Genre '"+gg+"' is alreadt in_Movie '"+mid+"'");
                                                    }
                                                }
                                            }
                                        } else {
                                            inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie Genre\n");
                                            System.out.println("MovieParser Inconsistent Data: Missing Genre");
                                        }
                                    }
                                } else {
                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie Genre\n");
                                    System.out.println("MovieParser Inconsistent Data: Missing Genre");
                                }
                                if (mid!=null && title!=null && year!=null && d!=null){
                                    if (!existedMovies.contains(title)){
                                        moviesWriter.write("'" + mid + "','" + title + "'," + year + ",'" + d + "'\n");
                                    } else {
                                        System.out.println("MovieParser Duplicate Data: Movie '"+title+"' already exist");
                                        inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Movie '"+title+"' already exist\n");
                                    }
                                }
                                if (mid==null){System.out.println("MovieParser Inconsistent Data: Missing Movie ID");
                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie ID\n");}
                                if (title==null){System.out.println("MovieParser Inconsistent Data: Missing Movie Title");
                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie Title\n");}
                                if (year==null){System.out.println("MovieParser Inconsistent Data: Missing Movie Year");
                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie year\n");}
                                if (d==null){System.out.println("MovieParser Inconsistent Data: Missing Director");
                                    inconsistentReportWriter.write("MovieParser at Movie No."+movieNum+" ID="+mid+": Missing Movie director\n");}
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Movie Parsing Complete");
    }

    private void getData(){
        lastId = 0;
        existedGenres = new HashMap<String, String>();
        existedMovies = new HashSet<String>();
        existedGim = new HashSet<String>();
        genreTranslator = new HashMap<String, String>();
        genreTranslator.put("Susp".toUpperCase(), "Thriller");
        genreTranslator.put("CnR".toUpperCase(), "Cops and Robbers");
        genreTranslator.put("Dram".toUpperCase(), "Drama");
        genreTranslator.put("West".toUpperCase(), "Western");
        genreTranslator.put("Myst".toUpperCase(), "Mystery");
        genreTranslator.put("S.F.".toUpperCase(), "Science Fiction");
        genreTranslator.put("Advt".toUpperCase(), "Adventure");
        genreTranslator.put("Horr".toUpperCase(), "Horror");
        genreTranslator.put("Romt".toUpperCase(), "Romantic");
        genreTranslator.put("Comd".toUpperCase(), "Comedy");
        genreTranslator.put("Musc".toUpperCase(), "Musical");
        genreTranslator.put("Docu".toUpperCase(), "Documentary");
        genreTranslator.put("Porn".toUpperCase(), "Pornography");
        genreTranslator.put("Noir".toUpperCase(), "Black");
        genreTranslator.put("BioP".toUpperCase(), "Biographical Picture");
        genreTranslator.put("TV".toUpperCase(), "TV Show");
        genreTranslator.put("TVs".toUpperCase(), "TV Series");
        genreTranslator.put("TVm".toUpperCase(), "TV Miniseries");
        try{
            String loginUser = "mytestuser";
            String loginPasswd = "mypassword";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query = "select * from genres order by id desc;";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){ lastId = rs.getInt("id");
            existedGenres.put(rs.getString("name").toUpperCase(), rs.getString("id"));}
            while (rs.next()){
                existedGenres.put(rs.getString("name").toUpperCase(), rs.getString("id"));
            }
            rs.close();
            statement.close();
            String query2 = "select title from movies;";
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery(query2);
            while (rs2.next()){
                existedMovies.add(rs2.getString("title"));
            }
            rs2.close();
            statement2.close();
            String query3 = "select * from genres_in_movies;";
            Statement statement3 = connection.createStatement();
            ResultSet rs3 = statement3.executeQuery(query3);
            while (rs3.next()){
                existedGim.add(rs3.getString("genreId")+rs3.getString("movieId"));
            }
            rs3.close();
            statement3.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("\"Error during connecting to Database\"");
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

    public static void main(String[] args) {
        MovieParser mp = new MovieParser();
        mp.run();
    }
}