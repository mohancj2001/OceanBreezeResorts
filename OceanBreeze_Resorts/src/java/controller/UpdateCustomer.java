/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Customer;
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
@WebServlet(name = "UpdateCustomer", urlPatterns = {"/UpdateCustomer"})
public class UpdateCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Gson gson = new Gson();
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", false);

            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

            String fname = jsonObject.get("first_name").getAsString();
            String email = jsonObject.get("email").getAsString();
            String lname = jsonObject.get("last_name").getAsString();
            String password = jsonObject.get("password").getAsString();
            String mobile = jsonObject.get("mobile").getAsString();

//        System.out.println(mobile);
//        System.out.println(fname);
//        System.out.println(lname);
//        System.out.println(password);
//        System.out.println(email);
            Session session = HibernateUtil.getSessionFactory().openSession();

            Customer customer = (Customer) session.createQuery("FROM Customer WHERE email = :email")
                        .setParameter("email", email)
                        .uniqueResult();
            
            customer.setFirst_name(fname);
            customer.setLast_name(lname);
            customer.setMobile(mobile);
            customer.setPassword(password);

            
            
            responseJson.addProperty("status", true);
                responseJson.addProperty("message", "Update successfully.");
            
            session.update(customer);
            session.beginTransaction().commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
