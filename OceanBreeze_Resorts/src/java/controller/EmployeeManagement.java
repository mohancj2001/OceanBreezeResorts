/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Admin;
import entity.Booking;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "EmployeeManagement", urlPatterns = {"/EmployeeManagement"})
public class EmployeeManagement extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String adminId = req.getParameter("id"); 
        String statusId = req.getParameter("status_id"); 

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Admin admin = (Admin) session.get(Admin.class, Integer.parseInt(adminId));
            if (admin == null) {
                responseJson.addProperty("message", "Admin not found");
                resp.getWriter().write(gson.toJson(responseJson));
                return;
            }

            Status newStatus = (Status) session.get(Status.class, Integer.parseInt(statusId));
            if (newStatus == null) {
                responseJson.addProperty("message", "Status not found");
                resp.getWriter().write(gson.toJson(responseJson));
                return;
            }

            admin.setStatus(newStatus);
            session.update(admin);
            
            transaction.commit();

            responseJson.addProperty("status", true);
            responseJson.addProperty("message", "Admin status updated successfully");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            responseJson.addProperty("message", "Error updating status: " + e.getMessage());
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
