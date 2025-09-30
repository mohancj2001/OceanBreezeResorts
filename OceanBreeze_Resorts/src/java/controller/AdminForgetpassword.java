package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Admin;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminForgetpassword", urlPatterns = {"/AdminForgetpassword"})
public class AdminForgetpassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);

        String email = (String) request.getSession().getAttribute("adminEmail");
        if (email == null) {
            responseJson.addProperty("message", "Session expired or invalid request");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseJson));
            return;
        }

        JsonObject dto = gson.fromJson(request.getReader(), JsonObject.class);
        String code = dto.get("code").getAsString();
        String password = dto.get("password").getAsString();

        try  {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(Admin.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("verification", code));

            Admin admin = (Admin) criteria.uniqueResult();

            if (admin != null) {
                admin.setPassword(password);
                session.update(admin);
                session.getTransaction().commit();

                responseJson.addProperty("status", true);
                responseJson.addProperty("message", "Password changed successfully");
            } else {
                responseJson.addProperty("message", "Invalid verification code");
            }

        } catch (Exception e) {
            e.printStackTrace(); 
            responseJson.addProperty("message", "An error occurred while resetting the password");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }
}
