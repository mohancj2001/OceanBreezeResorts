/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entity.Admin;
import entity.Booking;
import entity.Customer;
import entity.Rooms;
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

/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadBoxes", urlPatterns = {"/LoadBoxes"})
public class LoadBoxes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria criteria = session.createCriteria(Customer.class);

            List<Customer> customerList = criteria.list();
            jsonObject.addProperty("allCustomerCount", customerList.size());

            Criteria criteria1 = session.createCriteria(Booking.class);
            List<Booking> bookingList = criteria1.list();

            jsonObject.addProperty("allBookingList", bookingList.size());

            JsonElement bookingJsonArray = gson.toJsonTree(bookingList);

            jsonObject.add("bookingList", bookingJsonArray);

            Criteria criteria2 = session.createCriteria(Rooms.class);
            List<Rooms> roomList = criteria2.list();
            jsonObject.addProperty("allRoomList", roomList.size());

            JsonElement roomJsonArray = gson.toJsonTree(roomList);

            jsonObject.add("roomList", roomJsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
    }

}
