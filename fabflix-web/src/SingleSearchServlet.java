import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;


@WebServlet(name = "SingleSearchServlet", urlPatterns = "/api/search")
public class SingleSearchServlet extends HttpServlet{
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        String search = request.getParameter("search");

        String sort = request.getParameter("sort");
        String pnum = request.getParameter("pagenum");
        String offset = request.getParameter("offset");
        PrintWriter out = response.getWriter();

        try{
            String sort_q = get_sort_clause(sort);
            String pnum_q = get_pagenum_clause(pnum);
            String offset_q = get_offset_clause(offset);

            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            Connection dbcon = dataSource.getConnection();

            String query = "select id, title, year, director, rating from\n" +
                    "((select m.id, m.title, m.year, m.director\n" +
                    "from movies as m\n" +
                    "where m.title like ? or m.year = ? or m.director like ? \n" +
                    "union \n" +
                    "select m.id, m.title, m.year, m.director\n" +
                    "from movies as m, stars_in_movies as sim, stars as s \n" +
                    "where m.id=sim.movieId and s.id=sim.starId and s.name like ? \n" +
                    "group by m.id) as m \n" +
                    "left outer join ratings on m.id = movieId)" + sort_q + pnum_q + offset_q;

            String count_query = "select count(*) as count from\n" +
                    "(select m.id\n" +
                    "from movies as m\n" +
                    "where m.title like ? or m.year=? or m.director like ?\n" +
                    "union\n" +
                    "select m.id\n" +
                    "from movies as m, stars_in_movies as sim, stars as s\n" +
                    "where m.id=sim.movieId and s.id=sim.starId and s.name like ?\n" +
                    "group by m.id) as m;";

            PreparedStatement statement = dbcon.prepareStatement(query);
            PreparedStatement count_statement = dbcon.prepareStatement(count_query);

            statement.setString(1, "%"+search+"%");
            statement.setString(2, search);
            statement.setString(3, "%"+search+"%");
            statement.setString(4, "%"+search+"%");
            count_statement.setString(1, "%"+search+"%");
            count_statement.setString(2, search);
            count_statement.setString(3, "%"+search+"%");
            count_statement.setString(4, "%"+search+"%");
            ResultSet count_rs = count_statement.executeQuery();
            String movie_count="";
            while (count_rs.next()){
                movie_count = count_rs.getString("count");
            }
            count_rs.close();
            count_statement.close();

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next())
            {
                String movie_id = rs.getString("id");
                String movie_name = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                String gq = "select group_concat(g.name order by g.name) as name, group_concat(g.id order by g.id) as gid " +
                        "from movies as m, genres_in_movies as gim, genres as g " +
                        "where m.id=? and m.id=gim.movieId and g.id=gim.genreId;";
                PreparedStatement genre_statement = dbcon.prepareStatement(gq);
                genre_statement.setString(1,movie_id);
                ResultSet grs = genre_statement.executeQuery();
                String movie_genre="";
                String movie_gid="";
                while (grs.next()){
                    movie_genre = grs.getString("name");
                    movie_gid = grs.getString("gid");
                }
                grs.close();
                genre_statement.close();

                String starQuery = "select group_concat(se.name) as actors, group_concat(se.starId) as sid " +
                        "from(select distinct s.name, sim.starId, count(sim.movieId) as count " +
                        "from stars_in_movies as sim , stars as s, " +
                        "(select s.id as sid from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ?) as counting " +
                        "where sim.starId = s.id and s.id = counting.sid " +
                        "group by starId " +
                        "order by count desc, s.name limit 3) as se;";
                PreparedStatement star_statement = dbcon.prepareStatement(starQuery);
                star_statement.setString(1, movie_id);
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
            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();
        }
        catch (Exception e) {
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
            case "20":
                return "limit 20 ";
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