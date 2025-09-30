/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Admin;
import entity.Branch_Types;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author mohan
 */
@WebServlet(name = "UpdateEmployee", urlPatterns = {"/UpdateEmployee"})
public class UpdateEmployee extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
   
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            System.out.println("Received JSON: " + jsonBuffer.toString());

            JsonObject json = JsonParser.parseString(jsonBuffer.toString()).getAsJsonObject();

            int id = json.get("id").getAsInt();
            String firstName = json.has("firstName") ? json.get("firstName").getAsString() : null;
            String lastName = json.has("lastName") ? json.get("lastName").getAsString() : null;
            String email = json.has("email") ? json.get("email").getAsString() : null;
            String password = json.has("password") ? json.get("password").getAsString() : null;
            Integer branchId = json.has("branchId") ? json.get("branchId").getAsInt() : null;

            Gson gson = new Gson();
            JsonObject responseJson = new JsonObject();

            transaction = session.beginTransaction();

            Admin admin = (Admin) session.get(Admin.class, id);
            if (admin == null) {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Admin not found");
                out.print(gson.toJson(responseJson));
                return;
            }

            Branch_Types branch_Types = (Branch_Types) session.get(Branch_Types.class, branchId);
            

            if (firstName != null) admin.setFirst_name(firstName);
            if (lastName != null) admin.setLast_name(lastName);
            if (email != null) admin.setEmail(email);
            if (password != null) admin.setPassword(password);
            if (branchId != null) admin.setBranch_Types(branch_Types);

            session.update(admin);
            transaction.commit();

  
            responseJson.addProperty("status", "success");
            responseJson.addProperty("message", "Updated successfully");
            responseJson.add("data", json);

            out.print(gson.toJson(responseJson));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            System.out.println("Error processing request: " + e.getMessage());

            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", "An error occurred: " + e.getMessage());
            out.print(errorJson.toString());

        } finally {
            out.flush();
        }
        
    }
    
}
