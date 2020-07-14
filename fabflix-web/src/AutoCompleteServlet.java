
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;



// server endpoint URL
@WebServlet("/api/auto-complete")
public class AutoCompleteServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private static final long serialVersionUID = 1L;


    public AutoCompleteServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("search");
            System.out.println(query);

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            Connection dbcon = dataSource.getConnection();
            String sql_query = "select id, title from movies where MATCH (title) AGAINST (? IN BOOLEAN MODE) limit 10;";
            PreparedStatement statement = dbcon.prepareStatement(sql_query);

            String[] token = query.split(" ");
            String all_token = "";
            for (String t : token){
                all_token = all_token + "+" + t + "* ";
            }
            statement.setString(1, all_token.toString());
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                String movie_id = rs.getString("id");
                String movie_name = rs.getString("title");
                jsonArray.add(generateJsonObject(movie_id,movie_name));
            }
            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }

    }

    private static JsonObject generateJsonObject(String mid, String mtitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", mtitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("ID", mid);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
