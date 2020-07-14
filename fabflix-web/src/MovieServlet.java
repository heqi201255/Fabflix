import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String gid = request.getParameter("gid");
        String title = request.getParameter("title");
        String sort = request.getParameter("sort");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            if (gid != null) {
                String sort_q = get_sort_clause(sort);
                String query = "SELECT m.id, m.title, m.year, m.director, g.name, g.id as gid, r.rating " +
                        "from movies as m, genres as g, genres_in_movies as gim, ratings as r " +
                        "where m.id = gim.movieId and g.id = gim.genreId and g.id = ? and r.movieId = m.id " +
                        sort_q +
                        "limit 20 ;";

                // Declare our statement
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, gid);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {

                    String movie_id = rs.getString("id");
                    String movie_name = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genre = rs.getString("name");
                    String movie_gid = rs.getString("gid");
                    String movie_rating = rs.getString("rating");

                    String starQuery = "select group_concat(s.name) as actors, group_concat(s.id) as sid from (select s.name, s.id from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ? limit 3) as s;";
                    PreparedStatement star_statement = dbcon.prepareStatement(starQuery);
                    star_statement.setString(1,movie_id);
                    ResultSet rs_star = star_statement.executeQuery();

                    String movie_stars = "";
                    String star_id = "";
                    while (rs_star.next()){
                        movie_stars = rs_star.getString("actors");
                        star_id = rs_star.getString("sid");
                    }
                    rs_star.close();


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_name", movie_name);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_genre", movie_genre);
                    jsonObject.addProperty("movie_gid", movie_gid);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("movie_stars", movie_stars);
                    jsonObject.addProperty("star_id", star_id);
                    jsonArray.add(jsonObject);
                }

                // write JSON string to output
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

                rs.close();
                statement.close();
                dbcon.close();
            }

            if(title != null)
            {
                String sort_q = get_sort_clause(sort);
                String query2 = "select m.id, m.title, m.year,m.director, group_concat(g.name order by g.name) as name, group_concat(g.id order by g.id) as gid,r.rating " +
                        "from movies as m, genres as g, genres_in_movies as gim, ratings as r " +
                        "where m.id = gim.movieId and g.id = gim.genreId and m.title like ? and r.movieId = m.id " +
                        "group by m.id, m.title, m.year,m.director, r.rating " +
                        sort_q +
                        "limit 20;";
                PreparedStatement statement2 = dbcon.prepareStatement(query2);
                String title_like = title + '%';
                statement2.setString(1, title_like);
                ResultSet rs = statement2.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while (rs.next())
                {
                    String movie_id = rs.getString("id");
                    String movie_name = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genre = rs.getString("name");
                    String movie_gid = rs.getString("gid");
                    String movie_rating = rs.getString("rating");

                    String starQuery = "select group_concat(s.name) as actors, group_concat(s.id) as sid from (select s.name, s.id from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ? limit 3) as s;";
                    PreparedStatement star_statement = dbcon.prepareStatement(starQuery);
                    star_statement.setString(1,movie_id);
                    ResultSet rs_star = star_statement.executeQuery();

                    String movie_stars = "";
                    String star_id = "";
                    while (rs_star.next()){
                        movie_stars = rs_star.getString("actors");
                        star_id = rs_star.getString("sid");
                    }
                    rs_star.close();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_name", movie_name);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_genre", movie_genre);
                    jsonObject.addProperty("movie_gid", movie_gid);
                    jsonObject.addProperty("movie_stars", movie_stars);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("star_id", star_id);
                    jsonArray.add(jsonObject);
                }
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

                rs.close();
                statement2.close();
                dbcon.close();
            }

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
    private String get_sort_clause(String sort) {
        if (sort.equals("title_asc")) {
            return " ORDER BY m.title ";
        } else if (sort.equals("title_dsc")) {
            return " ORDER BY m.title DESC ";
        } else if (sort.equals("rating_asc")) {
            return " ORDER BY r.rating ";
        } else if (sort.equals("rating_dsc")) {
            return " ORDER BY r.rating DESC ";
        } else {
            return "";
        }
    }



}

