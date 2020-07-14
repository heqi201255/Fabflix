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

import javax.naming.Context;
import javax.naming.InitialContext;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "PaymentAuthServlet", urlPatterns = "/api/payment-auth")
public class PaymentAuthServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        String first = request.getParameter("first");
        String last = request.getParameter("last");
        String cardnum = request.getParameter("card");
        String exp = request.getParameter("exp");


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select * from creditcards as c where c.id=?;";
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, cardnum);

            // Perform the query
            ResultSet rs = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();
            // Iterate through each row of rs
            if (rs.next()) {
                if (rs.getString("firstName").equals(first)){
                    if (rs.getString("lastName").equals(last)){
                        if (rs.getString("expiration").equals(exp)){
                            jsonObject.addProperty("status", "success");
                            jsonObject.addProperty("message", "Payment Complete Successfully");
                        } else {
                            jsonObject.addProperty("status", "fail");
                            jsonObject.addProperty("message", "Wrong expiration date");
                        }
                    } else {
                        jsonObject.addProperty("status", "fail");
                        jsonObject.addProperty("message", "Wrong lastName");
                    }
                } else {
                    jsonObject.addProperty("status", "fail");
                    jsonObject.addProperty("message", "Wrong firstName");
                }
            } else {
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "Invalid credit card");
            }

            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
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