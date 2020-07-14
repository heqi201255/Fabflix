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


@WebServlet(name = "AddstarServlet", urlPatterns = "/api/addstar")
public class AddstarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String sname = request.getParameter("sname");
        String syear = request.getParameter("syear");
        PrintWriter out = response.getWriter();

        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/masterdb");

            Connection dbcon = dataSource.getConnection();
            String insert = "call add_star(?, ?);";
            PreparedStatement insert_statement = dbcon.prepareStatement(insert);
            insert_statement.setString(1, sname);
            int year = Integer.parseInt(syear);
            insert_statement.setInt(2, year);
            ResultSet rs = insert_statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            if(rs.next()){
                String new_id = rs.getString("sid");
                String new_name = rs.getString("name");
                String new_year = rs.getString("year");

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("new_sid", new_id);
                jsonObject.addProperty("new_name", new_name);
                jsonObject.addProperty("new_year", new_year);
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
