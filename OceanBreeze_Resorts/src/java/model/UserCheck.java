package model;

import dto.Admin_DTO;
import entity.Admin;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebFilter(filterName = "UserCheck", urlPatterns = {"/index.html", "/add-hotel.html", "/manage-bookings.html", "/manage-employees.html", "/add-product.html", "/manage-rooms.html", "/sales-analysis.html"})
public class UserCheck implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();  // Ensure proper session management

        try {
            // Check if user session exists
            Object userObj = httpServletRequest.getSession().getAttribute("user");

            if (userObj instanceof Admin_DTO) {
                Admin_DTO admin_DTO = (Admin_DTO) userObj;

                // Ensure ID is of correct type
                Integer adminId = admin_DTO.getId();

                if (adminId != null) {
                    Admin dbBusinessUser = (Admin) session.get(Admin.class, adminId);

                    if (dbBusinessUser != null) {
                        chain.doFilter(request, response);
                        return;
                    }
                }
            }
            httpServletResponse.sendRedirect("login.html");

        } catch (Exception e) {
            e.printStackTrace();
            httpServletResponse.sendRedirect("login.html");
        } finally {
            transaction.commit();
            session.close();
        }
    }

    @Override
    public void destroy() {
    }
}
