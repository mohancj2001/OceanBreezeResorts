/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Locations;
import entity.Room_Types;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "FilterRooms", urlPatterns = {"/FilterRooms"})
public class FilterRooms extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("status", false);
//        Gson gson = new Gson();
//
//        try {
//            String event = req.getParameter("event");
//            String location = req.getParameter("location");
//
//            Room_Types eventCategory = null;
//            Locations eventLocation = null;
//
//            if (!"Select".equals(event)) {
//                Criteria eventCriteria = session.createCriteria(Room_Types.class)
//                        .add(Restrictions.eq("room_type", event));
//                eventCategory = (Room_Types) eventCriteria.uniqueResult();
//
//                if (eventCategory == null) {
//                    jsonObject.addProperty("status", false);
//                    jsonObject.addProperty("message", "Event category not found.");
//                    resp.setContentType("application/json");
//                    resp.getWriter().write(gson.toJson(jsonObject));
//                    return;
//                }
//
//                criteria.createAlias("eventPackages", "ep")
//                        .add(Restrictions.eq("ep.eventCategories", eventCategory));
//            }
//
//            // Fetch event location if location parameter is not "Select"
//            if (!"Select".equals(location)) {
//                Criteria locationCriteria = session.createCriteria(EventLocations.class)
//                        .add(Restrictions.eq("location", location));
//                eventLocation = (EventLocations) locationCriteria.uniqueResult();
//
//                if (eventLocation == null) {
//                    jsonObject.addProperty("status", false);
//                    jsonObject.addProperty("message", "Event location not found.");
//                    resp.setContentType("application/json");
//                    resp.getWriter().write(gson.toJson(jsonObject));
//                    return;
//                }
//
//                criteria.add(Restrictions.eq("locations", eventLocation));
//            }
//
//            // Use DISTINCT to avoid duplicates in the result
//            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
//
//            // Execute the query to get the list of event packages
//            List<Locations_has_event_packages> locationsHasEventPackages = criteria.list();
//
//            if (!locationsHasEventPackages.isEmpty()) {
//                jsonObject.addProperty("status", true);
//                jsonObject.add("locations_has_event_packages", gson.toJsonTree(locationsHasEventPackages));
//            } else {
//                jsonObject.addProperty("status", false);
//                jsonObject.addProperty("message", "No event packages found.");
//            }
//
//        } catch (Exception e) {
//            jsonObject.addProperty("status", false);
//            jsonObject.addProperty("message", "Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // Send the response
//        resp.setContentType("application/json");
//        resp.getWriter().write(gson.toJson(jsonObject));

    }
}
