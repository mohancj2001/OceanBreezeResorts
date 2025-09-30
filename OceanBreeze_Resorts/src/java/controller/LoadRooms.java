package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Hotel;
import entity.Locations;
import entity.Room_Images;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author mohan
 */
@WebServlet(name = "LoadRooms", urlPatterns = {"/LoadRooms"})
public class LoadRooms extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", false);
        Gson gson = new Gson();

        String selectLocation = req.getParameter("selectLocation");
        String selectRoomType = req.getParameter("selectCategory");

        System.out.println(selectRoomType);
        System.out.println(selectLocation);

        try {
            Criteria citycriteria = session.createCriteria(Locations.class);
            citycriteria.add(Restrictions.eq("city", selectLocation));

            Criteria roomtypecriteria = session.createCriteria(Room_Types.class);
            roomtypecriteria.add(Restrictions.eq("room_type", selectRoomType));

            Criteria hotelcriteria = session.createCriteria(Hotel.class);

            Criteria criteria1 = session.createCriteria(Rooms.class);
            if (!selectLocation.equals("Select")) {
                Locations locations = (Locations) citycriteria.uniqueResult();

                hotelcriteria.add(Restrictions.eq("locations", locations));
                Hotel hotel = (Hotel) hotelcriteria.uniqueResult();
                criteria1.add(Restrictions.eq("hotel", hotel));
            }
            
            
            if (!selectRoomType.equals("Select")) {
                Room_Types room_Types = (Room_Types) roomtypecriteria.uniqueResult();
                
                criteria1.add(Restrictions.eq("room_Types", room_Types));
            }

            List<Rooms> roomList = criteria1.list();

            System.out.println(roomList);

            Criteria criteria2 = session.createCriteria(Room_Images.class);
            List<Room_Images> roomImagesList = criteria2.list();

            System.out.println(roomImagesList);

            if (roomList != null && !roomList.isEmpty()) {
                jsonObject.add("roomList", gson.toJsonTree(roomList));
            } else {
                jsonObject.addProperty("message", "No Rooms found");
            }

            if (roomImagesList != null && !roomImagesList.isEmpty()) {
                jsonObject.add("eventLocationList", gson.toJsonTree(roomImagesList));
            } else {
                jsonObject.addProperty("message", "No Image List found");
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
