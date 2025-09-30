/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Customer;
import entity.Status;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author mohan
 */
@WebServlet(name = "Verify_User", urlPatterns = {"/Verify_User"})
public class Verify_User extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        // Read request body correctly
        JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
        String email = requestBody != null && requestBody.has("email") ? requestBody.get("email").getAsString() : null;

        if (email == null || email.isEmpty()) {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "Invalid request parameters.");
        } else {
            try  {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Customer customer = (Customer) session.createQuery("FROM Customer WHERE email = :email")
                        .setParameter("email", email)
                        .uniqueResult();

                if (customer == null) {
                    responseJson.addProperty("status", false);
                    responseJson.addProperty("message", "User not found.");
                } else {
                    responseJson.addProperty("status", true);
                    responseJson.add("customer", gson.toJsonTree(customer));
                }
            } catch (Exception e) {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Database error: " + e.getMessage());
            }
        }

        // Write response to output stream
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(responseJson));
            out.flush();
        }
    }
}

