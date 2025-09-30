/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
@WebServlet(name = "LoadBookingTable", urlPatterns = {"/LoadBookingTable"})
public class LoadBookingTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria criteria = session.createCriteria(Booking.class);
            criteria.addOrder(Order.desc("id"));

            jsonObject.addProperty("allBookingCount", criteria.list().size());

            List<Booking> bookingList = criteria.list();

            JsonArray bookingJsonArray = new JsonArray();
            for (Booking booking : bookingList) {
                JsonObject bookingJson = gson.toJsonTree(booking).getAsJsonObject();

                Criteria bookingItemsCriteria = session.createCriteria(Booking_Items.class);
                bookingItemsCriteria.add(Restrictions.eq("booking", booking));
                List<Booking_Items> bookingItemsList = bookingItemsCriteria.list();

                bookingJson.add("booking_items", gson.toJsonTree(bookingItemsList));

                bookingJsonArray.add(bookingJson);
            }
            jsonObject.add("bookingList", bookingJsonArray);

            jsonObject.addProperty("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("error", e.getMessage());
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
    }

}
