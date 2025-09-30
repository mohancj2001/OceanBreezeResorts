/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Locations;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.HibernateUtil;

/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadHotel2", urlPatterns = {"/LoadHotel2"})
public class LoadHotel2 extends HttpServlet {

  @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

       
        List<Locations> locationsList = session.createQuery("SELECT l FROM Locations l WHERE l.id NOT IN (SELECT h.locations.id FROM Hotel h)").list();


        JsonObject jsonObject = new JsonObject();
        jsonObject.add("hotelList", gson.toJsonTree(locationsList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

        session.close();
    }


}
