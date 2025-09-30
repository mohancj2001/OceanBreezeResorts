/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Admin_DTO;
import dto.Response_DTO;
import entity.Admin;
import entity.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "VerifyAdmin", urlPatterns = {"/VerifyAdmin"})
public class VerifyAdmin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);

        JsonObject dto = gson.fromJson(request.getReader(), JsonObject.class);
        String email = dto.get("email").getAsString();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            Criteria criteria = session.createCriteria(Admin.class);
            criteria.add(Restrictions.eq("email", email));

            Admin admin = (Admin) criteria.uniqueResult();

            if (admin != null) {
                Random random = new Random();
                String code = String.format("%04d", random.nextInt(10000));

                admin.setVerification(code);
                session.update(admin);
                session.getTransaction().commit();

                // Store email in session for later retrieval
                request.getSession().setAttribute("adminEmail", email);

                // Send Email
                Mail.sendMail(admin.getEmail(), "OceanBreeze Resorts - Verification Code", "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "    <meta charset=\"UTF-8\">\n"
                        + "    <title>OceanBreeze Resorts - Verification Code</title>\n"
                        + "</head>\n"
                        + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">\n"
                        + "    <div style=\"width: 100%; max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);\">\n"
                        + "        <div style=\"text-align: center; color: #0073e6; font-size: 24px; font-weight: bold;\">OceanBreeze Resorts</div>\n"
                        + "        <div style=\"text-align: center; font-size: 18px; color: #333; margin: 20px 0;\">\n"
                        + "            <p>Hello,</p>\n"
                        + "            <p>Your verification code for OceanBreeze Resorts is:</p>\n"
                        + "            <div style=\"font-size: 24px; font-weight: bold; color: #0073e6; background: #f0f8ff; display: inline-block; padding: 10px 20px; border-radius: 5px; margin: 10px 0;\">\n"
                        + "                " + code + "\n"
                        + "            </div>\n"
                        + "            <p>Please enter this code to verify your email address. This code will expire in 10 minutes.</p>\n"
                        + "        </div>\n"
                        + "        <div style=\"text-align: center; font-size: 14px; color: #777; margin-top: 20px;\">\n"
                        + "            <p>If you didn't request this code, please ignore this email.</p>\n"
                        + "            <p>Thank you,<br>OceanBreeze Resorts Team</p>\n"
                        + "        </div>\n"
                        + "    </div>\n"
                        + "</body>\n"
                        + "</html>");

                responseJson.addProperty("status", true);
                responseJson.addProperty("message", "Verification Code Sent");
            } else {
                responseJson.addProperty("message", "Admin not found");
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            responseJson.addProperty("message", "An error occurred: " + e.getMessage());
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
