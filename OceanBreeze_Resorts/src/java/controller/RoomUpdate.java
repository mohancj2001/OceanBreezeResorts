package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Room_Types;
import entity.Rooms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet(name = "RoomUpdate", urlPatterns = {"/RoomUpdate"})
public class RoomUpdate extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        Transaction transaction = null;

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            // Read JSON from request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = req.getReader();

            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            String jsonString = jsonBuffer.toString();
            System.out.println("Received JSON: " + jsonString);

            // Ensure JSON is not empty
            if (jsonString.isEmpty()) {
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "error");
                errorResponse.addProperty("message", "Empty JSON request");
                out.print(new Gson().toJson(errorResponse));
                return;
            }

            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            int id = json.get("id").getAsInt();

            String title = json.has("title") ? json.get("title").getAsString() : null;
            String description = json.has("description") ? json.get("description").getAsString() : null;
            String qty = json.has("qty") ? json.get("qty").getAsString() : null;
            String price = json.has("price") ? json.get("price").getAsString() : null;
            String totalqty = json.has("totalqty") ? json.get("totalqty").getAsString() : null;
            Integer rating = json.has("rating") ? json.get("rating").getAsInt() : null;
            Integer roomType = json.has("roomType") ? json.get("roomType").getAsInt() : null;

            Gson gson = new Gson();
            JsonObject responseJson = new JsonObject();

            transaction = session.beginTransaction();
            Rooms rooms = (Rooms) session.get(Rooms.class, id);

            if (rooms == null) {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Room not found");
                out.print(gson.toJson(responseJson));
                return;
            }

            Room_Types room_Types = (Room_Types) session.get(Room_Types.class, roomType);

            if (title != null) {
                rooms.setTitle(title);
            }
            if (description != null) {
                rooms.setDescription(description);
            }
            if (qty != null) {
                try {
                    rooms.setQty(Integer.parseInt(qty));
                } catch (NumberFormatException e) {
                    responseJson.addProperty("status", "error");
                    responseJson.addProperty("message", "Invalid quantity format");
                    out.print(gson.toJson(responseJson));
                    return;
                }
            }
            if (totalqty != null) {
                try {
                    rooms.setTotal_qty(Integer.parseInt(totalqty));
                } catch (NumberFormatException e) {
                    responseJson.addProperty("status", "error");
                    responseJson.addProperty("message", "Invalid total quantity format");
                    out.print(gson.toJson(responseJson));
                    return;
                }
            }
            if (rating != null) {
                rooms.setRating(rating);
            }
            if (room_Types != null) {
                rooms.setRoom_Types(room_Types);
            }
            if (price != null) {
                try {
                    rooms.setPrice(Double.parseDouble(price));
                } catch (NumberFormatException e) {
                    responseJson.addProperty("status", "error");
                    responseJson.addProperty("message", "Invalid price format");
                    out.print(gson.toJson(responseJson));
                    return;
                }
            }

            session.update(rooms);
            transaction.commit();

            responseJson.addProperty("status", "success");
            responseJson.addProperty("message", "Updated successfully");
            responseJson.add("data", json);

            out.print(gson.toJson(responseJson));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "An error occurred while updating the room.");
            out.print(new Gson().toJson(errorResponse));
        }
    }
}
