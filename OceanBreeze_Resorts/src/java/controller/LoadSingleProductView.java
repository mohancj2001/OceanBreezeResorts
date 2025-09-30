/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Customer;
import entity.Rooms;
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
@WebServlet(name = "LoadSingleProductView", urlPatterns = {"/LoadSingleProductView"})
public class LoadSingleProductView extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String roomIdParam = request.getParameter("room_id");
        System.out.println(roomIdParam);

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        try {
            // Convert roomId to Integer
            int roomId = Integer.parseInt(roomIdParam);

            Session session = HibernateUtil.getSessionFactory().openSession();
            Rooms rooms = (Rooms) session.createQuery("FROM Rooms WHERE id = :id")
                    .setParameter("id", roomId) // Now using an integer
                    .uniqueResult();

            if (rooms == null) {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Room not found.");
            } else {
                responseJson.addProperty("status", true);
                responseJson.add("rooms", gson.toJsonTree(rooms));
            }
        } catch (NumberFormatException e) {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "Invalid room ID format.");
        } catch (Exception e) {
            responseJson.addProperty("status", false);
            responseJson.addProperty("message", "Database error: " + e.getMessage());
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(responseJson));
            out.flush();
        }
    }

}
