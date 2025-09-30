/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Room_Types;
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
@WebServlet(name = "LoadRoomTypes", urlPatterns = {"/LoadRoomTypes"})
public class LoadRoomTypes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        System.out.println("Okayy hriiiii");
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria criteria1 = session.createCriteria(Room_Types.class);
        criteria1.addOrder(Order.asc("room_type"));
        List<Room_Types> roomList = criteria1.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("roomList", gson.toJsonTree(roomList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        session.close();
    }
    
}
