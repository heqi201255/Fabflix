//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Servlet Filter implementation class LoginFilter
// */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
//public class LoginFilter implements Filter {
//    private final ArrayList<String> allowedURIs = new ArrayList<>();
//
//    /**
//     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//     */
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        System.out.println("LoginFilter: " + httpRequest.getRequestURI());
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            // Keep default action: pass along the filter chain
//            chain.doFilter(request, response);
//            return;
//        }
//        System.out.println(httpRequest.getServletPath());
//
//        if(httpRequest.getRequestURI().contains("dashboard.html")){
//            if (httpRequest.getSession().getAttribute("employee") != null){
//                System.out.println("111");
//                chain.doFilter(request,response);
//                return;
//            }
//            else{
//                httpResponse.sendRedirect("login.html");
//                return;
//            }
//        }
//
//        // Check if this URL is allowed to access without logging in
//
//
//        // Redirect to login page if the "user" attribute doesn't exist in session
//        if (httpRequest.getSession().getAttribute("user") == null) {
//            httpResponse.sendRedirect("login.html");
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    private boolean isUrlAllowedWithoutLogin(String requestURI) {
//        /*
//         Setup your own rules here to allow accessing some resources without logging in
//         Always allow your own login related requests(html, js, servlet, etc..)
//         You might also want to allow some CSS files, etc..
//         */
//        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
//    }
//
//    public void init(FilterConfig fConfig) {
//        allowedURIs.add("login.html");
//        allowedURIs.add("login.js");
//        allowedURIs.add("api/login");
//        allowedURIs.add("api/employee-login");
//    }
//
//    public void destroy() {
//        // ignored.
//    }
//
//}