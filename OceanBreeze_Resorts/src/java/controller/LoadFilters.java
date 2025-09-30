/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Locations;
import entity.Room_Types;
import entity.Status;
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
import org.hibernate.criterion.Restrictions;


/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadFilters", urlPatterns = {"/LoadFilters"})
public class LoadFilters extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", false);
        Gson gson = new Gson();

        try {
            
//            Criteria criteria0 = session.createCriteria(Status.class);
//            criteria0.add(Restrictions.eq("state", "Active"));
//            Status activeStatus = (Status) criteria0.uniqueResult();

            
            Criteria criteria1 = session.createCriteria(Room_Types.class);
//            criteria1.add(Restrictions.eq("status", activeStatus));
            List<Room_Types> eventCategoryList = criteria1.list();

            
            Criteria criteria2 = session.createCriteria(Locations.class);
//            criteria2.add(Restrictions.eq("status", activeStatus));
            List<Locations> eventLocationList = criteria2.list();

            if (eventCategoryList != null && !eventCategoryList.isEmpty()) {
                jsonObject.add("eventCategoryList", gson.toJsonTree(eventCategoryList));
            } else {
                jsonObject.addProperty("message", "No event categories found");
            }

            if (eventLocationList != null && !eventLocationList.isEmpty()) {
                jsonObject.add("eventLocationList", gson.toJsonTree(eventLocationList));
            } else {
                jsonObject.addProperty("message", "No event locations found");
            }

            jsonObject.addProperty("status", true);

        } catch (Exception e) {
            jsonObject.addProperty("status", false);
            jsonObject.addProperty("message", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }
}
