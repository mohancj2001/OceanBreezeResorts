/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Booking;
import entity.Payment_Status;
import entity.Rooms;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.System.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.weld.logging.LoggerFactory;

/**
 *
 * @author mohan
 */
@WebServlet(name = "BookingSettle", urlPatterns = {"/BookingSettle"})
public class BookingSettle extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null; // Initialize transaction

        try {
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(Booking.class);
            criteria.add(Restrictions.eq("id", Integer.parseInt(id)));
            Booking booking = (Booking) criteria.uniqueResult();

            if (booking != null) {
                String psid = "3";
                Payment_Status payment_Status = (Payment_Status) session.get(Payment_Status.class, Integer.parseInt(psid));

//                Criteria criteria1 = session.createCriteria(Rooms.class);
//                criteria1.add(Restrictions.eq("hotel_id", Integer.parseInt(id)));
//                Rooms rooms = (Rooms) criteria1.uniqueResult();
                

                if (payment_Status != null) {
                    booking.setPayment_Status(payment_Status);
                    session.update(booking);
                    transaction.commit();
                    responseJson.addProperty("status", true);
                    responseJson.addProperty("message", "Update successfully.");
                } else {
                    responseJson.addProperty("message", "Payment Status not found.");
                }

            } else {
                responseJson.addProperty("message", "Booking not found.");
            }

        } catch (NumberFormatException e) {
            responseJson.addProperty("message", "Invalid booking ID.");

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {
            responseJson.addProperty("message", "An error occurred.");

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
