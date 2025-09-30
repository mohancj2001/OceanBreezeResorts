/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Response_DTO; // Make sure you have this DTO
import entity.Admin;
import entity.Hotel;
import entity.Locations;
import entity.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil; // Make sure you have this utility class
import org.hibernate.Session;

@MultipartConfig
@WebServlet(name = "AddHotel", urlPatterns = {"/AddHotel"})
public class AddHotel extends HttpServlet {

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Response_DTO response_DTO = new Response_DTO();
//        Gson gson = new Gson();
//        
//        String location = request.getParameter("locationId");
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        
//        System.out.println(location);
//        Locations locations = (Locations) session.get(Locations.class, Integer.parseInt(location));
//        System.out.println(locations);
//        response.getWriter().write("Hello");
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO responseDTO = new Response_DTO();
        Gson gson = new Gson();

        String locationId = request.getParameter("locationId");
        System.out.println("Received locationId: " + locationId);

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction(); // Start a transaction

            Locations location = (Locations) session.get(Locations.class, Integer.parseInt(locationId));
            if (location == null) {
                throw new Exception("Location not found."); // Handle the case where the location doesn't exist.
            }

            Admin admin = (Admin) session.get(Admin.class, 1); // Assuming admin ID is 1. Adjust as needed.
            Status status = (Status) session.get(Status.class, 1); // Assuming status ID is 1. Adjust as needed.

            Hotel hotel = new Hotel();
            hotel.setAdmin(admin);
            hotel.setLocations(location);
            hotel.setStatus(status);

            session.save(hotel);
            session.getTransaction().commit(); // Commit the transaction
            session.close();

            responseDTO.setSuccess(true);
            responseDTO.setContent("Hotel added successfully.");

        } catch (Exception e) {
            e.printStackTrace(); // Log the error on the server console for debugging.
            responseDTO.setSuccess(false);
            responseDTO.setContent("Error adding hotel: " + e.getMessage()); // Send error details to the client.
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(responseDTO));
    }
    
    
}