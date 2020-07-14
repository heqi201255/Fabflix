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
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.Context;
import javax.naming.InitialContext;


@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username);
        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());

            Connection dbcon = dataSource.getConnection();
            String check_query = "select * from employees where email = ?;";
            PreparedStatement check_statement = dbcon.prepareStatement(check_query);
            check_statement.setString(1, username);
            System.out.println(check_statement);
            ResultSet rs = check_statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            boolean success;
            System.out.println(rs);

            if (rs.next()){
                String encryptedPassword = rs.getString("password");
                System.out.println(encryptedPassword);
                System.out.println(password);
//                success= password.equals(encryptedPassword);
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                System.out.println(success);
                if (success){
                    request.getSession().setAttribute("user", new User(username));
                    request.getSession().setAttribute("employee", rs.getString("email"));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect Password.");
                }
            }
            else{
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid Employee");
            }
            out.write(responseJsonObject.toString());
            response.setStatus(200);
            rs.close();
            check_statement.close();
            dbcon.close();
        }

        catch (Exception e)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }

        out.close();

    }


}
