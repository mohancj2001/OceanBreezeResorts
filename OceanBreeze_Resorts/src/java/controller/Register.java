package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Customer;
import entity.Status;
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

@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);

        JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

//        String mobile = request.getParameter("mobile");
//        String fname = request.getParameter("firstName");
//        String lname = request.getParameter("lastName");
//        String password = request.getParameter("password");
        String fname = jsonObject.get("first_name").getAsString();
        String email = jsonObject.get("email").getAsString();
        String lname = jsonObject.get("last_name").getAsString();
        String password = jsonObject.get("password").getAsString();
        String mobile = jsonObject.get("mobile").getAsString();

        System.out.println(mobile);
        System.out.println(fname);
        System.out.println(lname);
        System.out.println(password);
        System.out.println(email);

        Random random = new Random();
        String code = String.format("%04d", random.nextInt(10000));

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(Customer.class);
        criteria.add(Restrictions.eq("email", email));

        if (criteria.list().isEmpty()) {
            Customer customer = new Customer();
            customer.setFirst_name(fname);
            customer.setLast_name(lname);
            customer.setEmail(email);
            customer.setMobile(mobile);
            customer.setPassword(password);
            customer.setVerification_code(code);

            Status status = (Status) session.get(Status.class, 2);
            customer.setStatus(status);

            session.save(customer);
            session.beginTransaction().commit();

            Mail.sendMail(customer.getEmail(), "OceanBreeze Resorts - Verification Code", "<!DOCTYPE html>\n"
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
            responseJson.addProperty("message", "Registration Complete");

        } else {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "This email already Registered");
        }

        session.close();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
