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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        Enumeration<String> params = request.getParameterNames();


        // Output stream to STDOUT
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/masterdb");

            Connection dbcon = dataSource.getConnection();



            String lastId = "select id from sales order by id desc limit 1;";
            Statement getId = dbcon.createStatement();
            ResultSet idRs = getId.executeQuery(lastId);
            int id = 1;
            if (idRs.next()){ id = idRs.getInt("id"); }
            idRs.close();
            getId.close();
            JsonArray jsonArray = new JsonArray();
            LocalDate date = LocalDate.now();
            DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String datef = date.format(formater);

            while (params.hasMoreElements()){
                String movie_id = params.nextElement();
                int quantity = Integer.parseInt(request.getParameter(movie_id));
                JsonObject jsonObject = new JsonObject();
                String saleIds = "";
                for (int t=0; t<quantity; t++){
                    id = id + 1;
                    saleIds = saleIds + id + ", ";
                    String insertq = "insert into sales values(?, ?, ?, ?);";
                    PreparedStatement insert = dbcon.prepareStatement(insertq);
                    insert.setInt(1,id);
                    insert.setInt(2,(int)session.getAttribute("customer"));
                    insert.setString(3, movie_id.substring(5));
                    insert.setString(4, datef);
                    insert.executeUpdate();
                    insert.close();
                }
                jsonObject.addProperty("movieId", movie_id);
                jsonObject.addProperty("quantity", quantity);
                jsonObject.addProperty("saleId", saleIds);
                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }

}