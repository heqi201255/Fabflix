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
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            Connection dbcon = dataSource.getConnection();
            DatabaseMetaData metaData = dbcon.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", null);
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String table = rs.getString("TABLE_NAME");
                ResultSet column_result = metaData.getColumns(null, null, table, null);

                //JsonObject jsonObject = new JsonObject();
                while(column_result.next()){
                    JsonObject jsonObject = new JsonObject();
                    String attribute_name = column_result.getString("COLUMN_NAME");
                    String attribute_type = column_result.getString("TYPE_NAME");
                    String attribute_size = column_result.getString("COLUMN_SIZE");

                    jsonObject.addProperty("table_name", table);
                    jsonObject.addProperty("attribute_name", attribute_name);
                    jsonObject.addProperty("attribute_type", attribute_type);
                    jsonObject.addProperty("attribute_size", attribute_size);
                    jsonArray.add(jsonObject);
                }
                column_result.close();
                //jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
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
