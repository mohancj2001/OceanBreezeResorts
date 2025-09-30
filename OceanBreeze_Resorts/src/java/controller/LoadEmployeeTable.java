/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entity.Admin;
import entity.Booking;
import entity.Booking_Items;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadEmployeeTable", urlPatterns = {"/LoadEmployeeTable"})
public class LoadEmployeeTable extends HttpServlet {

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // Important: Date formatting
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("success", false);

    Session session = HibernateUtil.getSessionFactory().openSession();

    try {
        Criteria criteria = session.createCriteria(Admin.class);
        criteria.addOrder(Order.desc("id"));

        List<Admin> adminList = criteria.list(); // Fetch all Admins
        jsonObject.addProperty("allAdminCount", adminList.size());

        // Serialize the entire list directly
        JsonElement adminJsonArray = gson.toJsonTree(adminList);

        jsonObject.add("adminList", adminJsonArray);
        jsonObject.addProperty("success", true);

    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging
        jsonObject.addProperty("success", false);
        jsonObject.addProperty("error", e.getMessage()); // Add error message to the JSON
    } finally {
        session.close();
    }

    response.setContentType("application/json");
    response.getWriter().write(gson.toJson(jsonObject));
}


}
