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
import entity.Room_Types;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadRoomTable", urlPatterns = {"/LoadRoomTable"})
public class LoadRoomTable extends HttpServlet {

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria criteria = session.createCriteria(Rooms.class);
            criteria.addOrder(Order.desc("id"));

            List<Rooms> roomList = criteria.list();
            jsonObject.addProperty("allRoomCount", roomList.size());

            JsonArray roomJsonArray = new JsonArray();
            
            for (Rooms room : roomList) {
                JsonObject roomJson = new JsonObject();
                roomJson.addProperty("id", room.getId());
                roomJson.addProperty("hotel_id", room.getHotel().getId());
                roomJson.addProperty("room_type", room.getRoom_Types().getRoom_type());
                roomJson.addProperty("qty", room.getQty());
                roomJson.addProperty("total_qty", room.getTotal_qty());
                roomJson.addProperty("AC", room.getAc());
                roomJson.addProperty("price", room.getPrice());
                roomJson.addProperty("status", room.getStatus().getId());
                roomJson.addProperty("rating", room.getRating());
                roomJson.addProperty("title", room.getTitle());
                roomJson.addProperty("description", room.getDescription());

                roomJsonArray.add(roomJson);
            }

            jsonObject.add("roomList", roomJsonArray);
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
