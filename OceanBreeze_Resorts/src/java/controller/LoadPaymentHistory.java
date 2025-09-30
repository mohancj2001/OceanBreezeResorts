package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Booking;
import entity.Booking_Items;
import entity.Customer;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LoadPaymentHistory", urlPatterns = {"/LoadPaymentHistory"})
public class LoadPaymentHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        String cid = req.getParameter("id");
        if (cid == null || cid.isEmpty()) {
            jsonObject.addProperty("status", false);
            jsonObject.addProperty("message", "Customer ID is required.");
            resp.getWriter().write(gson.toJson(jsonObject));
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            int customerId = Integer.parseInt(cid);  // Handle number format issues
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Customer customer = (Customer) session.get(Customer.class, customerId);
            if (customer == null) {
                jsonObject.addProperty("status", false);
                jsonObject.addProperty("message", "Customer not found.");
                resp.getWriter().write(gson.toJson(jsonObject));
                return;
            }

            // Fetch bookings for the customer
            List<Booking> bookingList = session.createCriteria(Booking.class)
                    .add(Restrictions.eq("customer", customer))
                    .list();

            // Fetch booking items associated with those bookings
            List<Booking_Items> bookingItemsList = session.createCriteria(Booking_Items.class)
                    .add(Restrictions.in("booking", bookingList))
                    .list();

            jsonObject.addProperty("status", true);
            jsonObject.add("bookingList", gson.toJsonTree(bookingList));
            jsonObject.add("bookingItemsList", gson.toJsonTree(bookingItemsList));

            transaction.commit();
        } catch (NumberFormatException e) {
            jsonObject.addProperty("status", false);
            jsonObject.addProperty("message", "Invalid customer ID format.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            jsonObject.addProperty("status", false);
            jsonObject.addProperty("message", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();  // Ensure session is closed
            }
        }

        resp.getWriter().write(gson.toJson(jsonObject));
    }
}
