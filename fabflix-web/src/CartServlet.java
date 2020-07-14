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
import java.util.ArrayList;
import java.util.Random;

import javax.naming.Context;
import javax.naming.InitialContext;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")

public class CartServlet extends HttpServlet {
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "previousItems" from session
        ArrayList<String> Cart = (ArrayList<String>) session.getAttribute("Cart");

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
        // ArrayList for the user
        PrintWriter out = response.getWriter();
        String item = request.getParameter("item"); // Get parameter that sent by GET request url
        String req = request.getParameter("req");

        System.out.println("Param-item: "+item);
        System.out.println("req: "+req);

        response.setContentType("application/json");
        try {
            if (Cart == null) {

                // Add the newly created ArrayList to session, so that it could be retrieved next time
                session.setAttribute("Cart", new ArrayList<>());
            }
            JsonObject jsonObj = new JsonObject();

            synchronized (Cart) {
                if (item != null) {

                    boolean updated = false;
                    // item: id_quantity_singlePrice
                    switch (req) {
                        case "add":
                            for (int i=0; i< Cart.size(); i++) {
                                String[] l = Cart.get(i).split("_");
                                if (l[0].equals(item)) {
                                    l[1] = Integer.toString(Integer.parseInt(l[1]) + 1);
                                    Cart.set(i, l[0] + '_' + l[1] + '_' + l[2]);
                                    updated = true;
                                    break;
                                }
                            }
                            if (!updated) {
                                Random rand = new Random();
                                Cart.add(item + "_" + 1 + "_" + rand.nextInt(100));
                                updated = true;
                            }
                            if (updated) {
                                jsonObj.addProperty("status", "success");
                                jsonObj.addProperty("message", "item added successfully");
                            } else {
                                jsonObj.addProperty("status", "fail");
                                jsonObj.addProperty("message", "failed to add item");
                            }
                            break;
                        case "minus":
                            for (int i=0; i< Cart.size(); i++) {
                                String[] l = Cart.get(i).split("_");
                                if (l[0].equals(item)) {
                                    l[1] = Integer.toString(Integer.parseInt(l[1]) - 1);
                                    if (!l[1].equals("0")) {
                                        Cart.set(i, l[0] + '_' + l[1] + "_" + l[2]);
                                        updated = true;
                                    }
                                    break;
                                }
                            }
                            if (updated) {
                                jsonObj.addProperty("status", "success");
                                jsonObj.addProperty("message", "item decreased successfully");
                            } else {
                                jsonObj.addProperty("status", "fail");
                                jsonObj.addProperty("message", "failed to decrease item.");
                            }
                            break;
                        case "remove":
                            for (int i=0; i< Cart.size(); i++) {
                                String[] l = Cart.get(i).split("_");
                                if (l[0].equals(item)) {
                                    Cart.remove(Cart.get(i));
                                    updated = true;
                                    break;
                                }
                            }
                            if (updated) {
                                jsonObj.addProperty("status", "success");
                                jsonObj.addProperty("message", "item removed successfully");
                            } else {
                                jsonObj.addProperty("status", "fail");
                                jsonObj.addProperty("message", "failed to remove item, item does not exist in your shopping cart.");
                            }
                            break;
                    }
                } else {
                    if (req.equals("total")){
                        int total = 0;
                        for (String s : Cart) {
                            String[] l = s.split("_");
                            total = total + Integer.parseInt(l[1]) * Integer.parseInt(l[2]);
                        }
                        jsonObj.addProperty("total", Integer.toString(total));
                    }
                }
            }
            out.println(jsonObj.toString());
            response.setStatus(200);

        } catch (Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        // get title

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        String id = request.getParameter("id");
        try {
            if (id!=null) {

                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

                Connection dbcon = dataSource.getConnection();
                JsonObject jsonObject = new JsonObject();
                String query = "select title from movies as m where m.id =?;";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, id);
                ResultSet rs = statement.executeQuery();
                rs.next();
                jsonObject.addProperty("title", rs.getString("title"));
                rs.close();
                statement.close();
                out.println(jsonObject.toString());
                dbcon.close();
            } else {
                HttpSession session = request.getSession();
                ArrayList<String> Cart = (ArrayList<String>) session.getAttribute("Cart");

                JsonArray jsonArray = new JsonArray();
                if (Cart == null) {
                    // Add the newly created ArrayList to session, so that it could be retrieved next time
                    session.setAttribute("Cart", new ArrayList<>());
                    out.println(jsonArray.toString());
                } else {

                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup("java:/comp/env");
                    DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

                    Connection dbcon = dataSource.getConnection();
                    synchronized (Cart){
                        JsonObject jsonObject = new JsonObject();
                        for (int i=0; i< Cart.size(); i++){
                            System.out.println("item: "+i);
                            String[] l = Cart.get(i).split("_");
                            String query = "select title from movies as m where m.id =?;";
                            PreparedStatement statement = dbcon.prepareStatement(query);
                            statement.setString(1, l[0]);
                            ResultSet rs = statement.executeQuery();
                            jsonObject.addProperty("mid", l[0]);
                            rs.next();
                            jsonObject.addProperty("title", rs.getString("title"));
                            jsonObject.addProperty("quantity", l[1]);
                            jsonObject.addProperty("price", l[2]);
                            jsonArray.add(jsonObject);
                            rs.close();
                            statement.close();
                        }
                        out.println(jsonArray.toString());
                    }
                    dbcon.close();
                }
            }
        } catch (Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();

    }
}