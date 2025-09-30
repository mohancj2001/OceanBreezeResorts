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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "Email_Verification", urlPatterns = {"/Email_Verification"})
public class Email_Verification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        // Extract parameters
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");

        if (email == null || otp == null || email.isEmpty() || otp.isEmpty()) {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "Invalid request parameters.");
        } else {
            System.out.println("Received OTP verification request for: " + email + " OTP: " + otp);

            // TODO: Implement OTP verification logic (check OTP against stored value in database)
            boolean isOtpValid = checkOtp(email, otp);

            if (isOtpValid) {
                responseJson.addProperty("status", true);
                responseJson.addProperty("message", "Email verified successfully.");
                Session session = HibernateUtil.getSessionFactory().openSession();

//                Customer customer = new Customer();
                Customer customer = (Customer) session.createQuery("FROM Customer WHERE email = :email")
                        .setParameter("email", email)
                        .uniqueResult();
                Status status = (Status) session.get(Status.class, 1);
                customer.setStatus(status);
                //verifiy add krnn ek
                customer.setVerification_code("verified");

                session.update(customer);
                session.beginTransaction().commit();
            } else {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Invalid OTP. Please try again.");
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(responseJson));
            out.flush();
        }
    }

    private boolean checkOtp(String email, String otp) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria criteria = session.createCriteria(Customer.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("verification_code", otp));

            Customer customer = (Customer) criteria.uniqueResult(); 

            return customer != null; 
        } finally {
            session.close(); 
        }

    }
}
