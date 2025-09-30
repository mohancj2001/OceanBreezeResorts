package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Booking;
import entity.Booking_Items;
import entity.Customer;
import entity.Payment_Status;
import entity.Rooms;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "PlaceBooking", urlPatterns = {"/PlaceBooking"})
public class PlaceBooking extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuilder jsonString = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        JsonObject jsonObject = JsonParser.parseString(jsonString.toString()).getAsJsonObject();
        Gson gson = new Gson();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Parse dates from String to Date format
            String fromDateString = jsonObject.get("from_date").getAsString();
            String toDateString = jsonObject.get("to_date").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateObj = dateFormat.parse(fromDateString);
            Date toDateObj = dateFormat.parse(toDateString);

            double totalPaymentItem = jsonObject.get("totalPaymentItem").getAsDouble();
            double totalPayment = jsonObject.get("totalPayment").getAsDouble();
            int customerId = jsonObject.get("customer_id").getAsInt();
            int roomId = jsonObject.get("rooms_id").getAsInt();
            
            int locationId = jsonObject.has("locations_id") ? jsonObject.get("locations_id").getAsInt() : -1;
            int paymentId = jsonObject.has("payment_id") ? jsonObject.get("payment_id").getAsInt() : -1;
            double perMemberPrice = jsonObject.has("per_member_price") ? jsonObject.get("per_member_price").getAsDouble() : 0.0;

            // Fetch Customer
            Customer customer = (Customer) session.get(Customer.class, customerId);
            if (customer == null) {
                throw new Exception("Customer not found.");
            }

            // Fetch Payment Status
            Payment_Status paymentStatus = (Payment_Status) session.get(Payment_Status.class, 2);
            if (paymentStatus == null) {
                throw new Exception("Payment status not found.");
            }

            // Save Booking
            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setPrice(totalPayment);
            booking.setDate(new Date());
            booking.setPayment_Status(paymentStatus);
            
            session.save(booking);
            session.flush();

            // Handle Quantity Parsing
            int quantity;
            try {
                quantity = jsonObject.get("qty").getAsInt();
            } catch (NumberFormatException e) {
                throw new Exception("Invalid quantity format.");
            }

            saveOrderItem(session, booking, roomId, quantity, totalPaymentItem, fromDateObj, toDateObj);

            transaction.commit();

            // Send success response
            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "Order placed successfully");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            responseJsonObject.addProperty("message", "Error processing order: " + e.getMessage());
        } finally {
            session.close();
        }

        // Send response
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(responseJsonObject.toString());
    }

    private void saveOrderItem(Session session, Booking booking, int roomId, int qty, double price, Date fromDate, Date toDate) {
        // Fetch Room
        Rooms room = (Rooms) session.get(Rooms.class, roomId);
        if (room == null) {
            throw new RuntimeException("Room not found.");
        }

        if (room.getQty() < qty) {
            throw new RuntimeException("Insufficient room availability.");
        }

        // Save Booking Item
        Booking_Items bookingItem = new Booking_Items();
        bookingItem.setBooking(booking);
        bookingItem.setFrom_date(fromDate);
        bookingItem.setTo_date(toDate);
        bookingItem.setPrice(price);
        bookingItem.setQty(qty);
        bookingItem.setRooms(room);
        
        session.save(bookingItem);

        // Update Room Quantity
        room.setQty(room.getQty() - qty);
        session.update(room);
    }
}
