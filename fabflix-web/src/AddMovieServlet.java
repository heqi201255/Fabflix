import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Enumeration;

import javax.naming.Context;
import javax.naming.InitialContext;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/masterdb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String mname = request.getParameter("mtitle");
        String myear = request.getParameter("myear");
        String mdirector = request.getParameter("mdirector");
        String mgenre = request.getParameter("mgenre");
        String mstar = request.getParameter("mstar");
        PrintWriter out = response.getWriter();

        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/masterdb");

            Connection dbcon = dataSource.getConnection();
            if(dbcon == null){
                System.out.println("dbcon is null");
            }

            String insert = "call add_movie(?, ?, ?, ?, ?);";
            PreparedStatement insert_statement = dbcon.prepareStatement(insert);
            insert_statement.setString(1, mname);
            int year = Integer.parseInt(myear);
            insert_statement.setInt(2, year);
            insert_statement.setString(3, mdirector);
            insert_statement.setString(4, mgenre);
            insert_statement.setString(5, mstar);

            ResultSet rs = insert_statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            if(rs.next()){
                String mid = rs.getString("mid");
                String title = rs.getString("title");
                String movie_year = rs.getString("year");
                String director = rs.getString("director");
                String genre = rs.getString("genre");
                String star = rs.getString("star");

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("mid", mid);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genre",genre);
                jsonObject.addProperty("star",star);
                jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            insert_statement.close();
            dbcon.close();
        }
        catch (Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }
}
