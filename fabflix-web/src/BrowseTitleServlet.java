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

@WebServlet(name = "BrowseTitleServlet", urlPatterns = "/api/browse-title")
public class BrowseTitleServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String sort = request.getParameter("sort");
        String pnum = request.getParameter("pagenum");
        String offset = request.getParameter("offset");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            Connection dbcon = dataSource.getConnection();

            if(title != null)
            {
                String sort_q = get_sort_clause(sort);
                String pnum_q = get_pagenum_clause(pnum);
                String offset_q = get_offset_clause(offset);

                String query2;
                PreparedStatement statement2;
                String title_like;
                ResultSet rs;
                String count_query;
                PreparedStatement count_statement;
                ResultSet count_rs;

                if (!title.equals("*"))
                {
                    query2 = "select id, title, year,director, name, gid, rating from" +
                            "(select m.id, m.title, m.year,m.director, group_concat(g.name order by g.name) as name, group_concat(g.id order by g.id) as gid " +
                            "from movies as m, genres as g, genres_in_movies as gim " +
                            "where m.id = gim.movieId and g.id = gim.genreId and m.title like ? " +
                            "group by m.id, m.title, m.year,m.director) as m left outer join ratings on m.id=movieId " +
                            sort_q + pnum_q + offset_q;

                    count_query = "select count(*) as count from " +
                            "(select count(*) " +
                            "from movies as m " +
                            "where m.title like ?) as m;";

                    statement2 = dbcon.prepareStatement(query2);
                    title_like = title + '%';
                    statement2.setString(1, title_like);
                    rs = statement2.executeQuery();

                    count_statement = dbcon.prepareStatement(count_query);
                    count_statement.setString(1,title_like);
                    count_rs = count_statement.executeQuery();
                }
                else
                {
                    query2 = "select m.id,m.title,m.year,m.director,m.name,m.gid,rating from(\n" +
                            "select m.id, m.title, m.year,m.director, group_concat(g.name order by g.name) as name, group_concat(g.id order by g.id) as gid\n" +
                            "from movies as m, genres as g, genres_in_movies as gim\n" +
                            "where m.id = gim.movieId and g.id = gim.genreId and m.title regexp '^[^a-z0-9]'\n" +
                            "group by m.id, m.title, m.year,m.director) as m left outer join ratings on m.id=movieId " +
                            sort_q + pnum_q + offset_q;

                    count_query = "select count(*) as count from " +
                            "(select count(*)" +
                            "from movies as m " +
                            "where m.title regexp '^[^a-z0-9]')as m;";

                    statement2 = dbcon.prepareStatement(query2);
                    rs = statement2.executeQuery();

                    count_statement = dbcon.prepareStatement(count_query);
                    count_rs = count_statement.executeQuery();
                }


                JsonArray jsonArray = new JsonArray();

                String movie_count = "";
                while(count_rs.next())
                {
                    movie_count = count_rs.getString("count");
                }
                count_rs.close();
                count_statement.close();
                while (rs.next())
                {
                    String movie_id = rs.getString("id");
                    String movie_name = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genre = rs.getString("name");
                    String movie_gid = rs.getString("gid");
                    String movie_rating = rs.getString("rating");

                    String starQuery = "select group_concat(se.name) as actors, group_concat(se.starId) as sid " +
                            "from(select distinct s.name, sim.starId, count(sim.movieId) as count " +
                            "from stars_in_movies as sim , stars as s, " +
                            "(select s.id as sid from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ?) as counting " +
                            "where sim.starId = s.id and s.id = counting.sid " +
                            "group by starId " +
                            "order by count desc, s.name limit 3) as se;";
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
                    star_statement.close();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_count", movie_count);
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
            }
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
    private String get_sort_clause(String sort) {
        switch (sort) {
            case "r_dsc_t_asc":
                return " order by rating desc, m.title ";
            case "r_dsc_t_dsc":
                return " order by rating desc, m.title desc ";
            case "r_asc_t_asc":
                return " order by rating, m.title ";
            case "r_asc_t_dsc":
                return " order by rating, m.title desc ";
            case "t_dsc_r_asc":
                return " order by m.title desc, rating ";
            case "t_dsc_r_dsc":
                return " order by m.title desc, rating desc ";
            case "t_asc_r_asc":
                return " order by m.title, rating ";
            case "t_asc_r_dsc":
                return " order by m.title, rating desc ";
            default:
                return "";
        }
    }
    private String get_pagenum_clause(String pnum) {
        switch (pnum) {
            case "10":
                return "limit 10 ";
            case "25":
                return "limit 25 ";
            case "50":
                return "limit 50 ";
            case "100":
                return "limit 100 ";
            default:
                return "";
        }
    }

    private String get_offset_clause(String offset){
        return "offset " + offset + ";";
    }



}
