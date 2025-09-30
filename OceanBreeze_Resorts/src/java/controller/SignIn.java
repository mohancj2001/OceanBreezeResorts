/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Customer_DTO;
import entity.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);
        try {
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);

            String email = requestJson.get("email").getAsString();
            String password = requestJson.get("password").getAsString();

            System.out.println(email);
            System.out.println(password);

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(Customer.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("password", password));

            Customer_DTO customer_DTO = gson.fromJson(request.getReader(), Customer_DTO.class);
            if (!criteria.list().isEmpty()) {

         
                Customer customer = (Customer) criteria.uniqueResult();

                responseJson.addProperty("message", "Sign In Success");


                responseJson.addProperty("customer", gson.toJson(customer));
               customer_DTO.setFirst_name(customer.getFirst_name());
                        customer_DTO.setLast_name(customer.getLast_name());
                        customer_DTO.setEmail(customer.getEmail());
                        customer_DTO.setPassword(null);
                        request.getSession().setAttribute("customer", customer_DTO);
//                System.out.println(customer.getFirst_name() + " " + customer.getLast_name());

                responseJson.addProperty("status", true);

            } else {
                responseJson.addProperty("message", "Invalid Credentials!");
            }
            session.close();
            
        } catch (Exception e) {
            responseJson.addProperty("message", e.getMessage());
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
