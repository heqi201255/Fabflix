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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query = "select id, title, year, director, rating from (movies left outer join ratings on id = movieId) where id = ?;";
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieid = rs.getString("id");
                String movietitle = rs.getString("title");
                String movieyear = rs.getString("year");
                String moviedirector = rs.getString("director");
                String genreQuery = "select group_concat(g.name order by g.name) as gname,  group_concat(g.id order by g.name) as gid " +
                        "from genres as g, genres_in_movies as gim " +
                        "where g.id = gim.genreId and gim.movieId = ?;";
                PreparedStatement statement2 = dbcon.prepareStatement(genreQuery);
                statement2.setString(1,movieid);
                ResultSet rs2 = statement2.executeQuery();

                String moviegenre = "";
                String genreid = "";
                while (rs2.next()){
                    moviegenre = rs2.getString("gname");
                    genreid = rs2.getString("gid");
                }
                rs2.close();

                String starQuery = "select group_concat(se.name order by se.count desc, se.name) as actors, group_concat(se.starId order by se.count desc, se.name) as sid " +
                        "from(select distinct s.name, sim.starId, count(sim.movieId) as count " +
                        "from stars_in_movies as sim , stars as s, " +
                        "(select s.id as sid from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ?) as counting " +
                        "where sim.starId = s.id and s.id = counting.sid " +
                        "group by starId " +
                        "order by count desc) as se;";
                PreparedStatement statement3 = dbcon.prepareStatement(starQuery);
                statement3.setString(1,movieid);
                ResultSet rs3 = statement3.executeQuery();
                String moviestar = "";
                String starid = "";
                while (rs3.next()){
                    moviestar = rs3.getString("actors");
                    starid = rs3.getString("sid");
                }
                rs3.close();
                String movierating = rs.getString("rating");


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieid);
                jsonObject.addProperty("movie_title", movietitle);
                jsonObject.addProperty("movie_year", movieyear);
                jsonObject.addProperty("movie_director", moviedirector);
                jsonObject.addProperty("star_id", starid);
                jsonObject.addProperty("movie_stars", moviestar);
                jsonObject.addProperty("movie_genre", moviegenre);
                jsonObject.addProperty("movie_gid", genreid);
                jsonObject.addProperty("movie_rating", movierating);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
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

