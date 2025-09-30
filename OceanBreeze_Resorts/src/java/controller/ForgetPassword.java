package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Customer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Servlet for handling password reset requests.
 */
@WebServlet(name = "ForgetPassword", urlPatterns = {"/ForgetPassword"})
public class ForgetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        try {
     
            JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);

            if (requestBody == null) {
                throw new IllegalArgumentException("Invalid JSON format");
            }

            String email = requestBody.has("email") ? requestBody.get("email").getAsString() : null;
            String password = requestBody.has("password") ? requestBody.get("password").getAsString() : null;

    
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Invalid request parameters.");
                response.getWriter().write(gson.toJson(responseJson));
                return;
            }

         
            try {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                Customer customer = (Customer) session.createQuery("FROM Customer WHERE email = :email")
                        .setParameter("email", email)
                        .uniqueResult();

                if (customer == null) {
                    responseJson.addProperty("status", false);
                    responseJson.addProperty("message", "Customer not found.");
                } else {
                    customer.setPassword(password);
                    session.update(customer);
                    transaction.commit();

                    responseJson.addProperty("status", true);
                    responseJson.addProperty("message", "Password updated successfully.");
                }
            }catch (Exception e) {
            e.printStackTrace();
            }

        } catch (IllegalArgumentException e) {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "An error occurred while processing your request.");
        }

      
        response.getWriter().write(gson.toJson(responseJson));
    }
}
