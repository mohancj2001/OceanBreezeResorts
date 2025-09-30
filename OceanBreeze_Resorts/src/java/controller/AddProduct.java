package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.*;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024, // 2MB
    maxFileSize = 10 * 1024 * 1024, // 10MB per file
    maxRequestSize = 50 * 1024 * 1024 // 50MB total
)
@WebServlet(name = "AddProduct", urlPatterns = {"/AddProduct"})
public class AddProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        // Retrieve parameters from the request
        String locationId = request.getParameter("hotelId");
        String roomId = request.getParameter("roomId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        String quantity = request.getParameter("quantity");

        // Retrieve file parts
        Part image1 = request.getPart("image1");
        Part image2 = request.getPart("image2");
        Part image3 = request.getPart("image3");

        // Validate request parameters
        if (!Validations.isInteger(locationId)) {
            response_DTO.setContent("Invalid Hotel");
        } else if (!Validations.isInteger(roomId)) {
            response_DTO.setContent("Invalid Room");
        } else if (title == null || title.trim().isEmpty()) {
            response_DTO.setContent("Please fill Title");
        } else if (description == null || description.trim().isEmpty()) {
            response_DTO.setContent("Please fill Description");
        } else if (price == null || price.trim().isEmpty()) {
            response_DTO.setContent("Please fill Price");
        } else if (!Validations.isDouble(price) || Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Invalid Price");
        } else if (quantity == null || quantity.trim().isEmpty()) {
            response_DTO.setContent("Please fill Quantity");
        } else if (!Validations.isInteger(quantity) || Integer.parseInt(quantity) <= 0) {
            response_DTO.setContent("Invalid Quantity");
        } else if (image1 == null || image1.getSubmittedFileName().isEmpty()) {
            response_DTO.setContent("Please upload Image 1");
        } else if (image2 == null || image2.getSubmittedFileName().isEmpty()) {
            response_DTO.setContent("Please upload Image 2");
        } else if (image3 == null || image3.getSubmittedFileName().isEmpty()) {
            response_DTO.setContent("Please upload Image 3");
        } else {
            try  {
                Session session = HibernateUtil.getSessionFactory().openSession();
                // Retrieve Location entity
                Locations flocations = (Locations) session.get(Locations.class, Integer.parseInt(locationId));

                if (flocations == null) {
                    response_DTO.setContent("Invalid Location");
                } else {
                    // Retrieve Hotel entity associated with Location
                    Criteria criteria = session.createCriteria(Hotel.class);
                    criteria.add(Restrictions.eq("locations", flocations));
                    Hotel hotel = (Hotel) criteria.uniqueResult();

                    if (hotel == null) {
                        response_DTO.setContent("No Hotel found for this Location");
                    } else {
                        // Retrieve Room Type entity
                        Room_Types room_Types = (Room_Types) session.get(Room_Types.class, Integer.parseInt(roomId));

                        if (room_Types == null) {
                            response_DTO.setContent("Please Select a Valid Room Type");
                        } else {
                            // Create new Room entity
                            Rooms rooms = new Rooms();
                            Status status = (Status) session.load(Status.class, 1);
                            rooms.setDescription(description);
                            rooms.setPrice(Double.parseDouble(price));
                            rooms.setStatus(status);
                            rooms.setQty(Integer.parseInt(quantity));
                            rooms.setTotal_qty(Integer.parseInt(quantity));
                            rooms.setTitle(title);
                            rooms.setHotel(hotel);
                            rooms.setRoom_Types(room_Types);

                            session.beginTransaction();
                            int pid = (int) session.save(rooms);
                            session.getTransaction().commit();

                            // Save images to RoomImages directory
                            saveImages(request, image1, image2, image3, pid);

                            response_DTO.setSuccess(true);
                            response_DTO.setContent("New Room Added");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response_DTO.setSuccess(false);
                response_DTO.setContent("An error occurred: " + e.getMessage());
            }
        }

        // Return JSON response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }

    private void saveImages(HttpServletRequest request, Part image1, Part image2, Part image3, int pid) throws IOException {
        // Determine image save path
        String applicationPath = request.getServletContext().getRealPath("");
        String newApplicationPath = applicationPath.replace("build//web", "web");

        // Create folder for storing images
        File folder = new File(newApplicationPath + "//RoomImages//" + pid);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Save images
        saveImage(image1, folder, pid+"image1.jpg");
        saveImage(image2, folder, pid+"image2.jpg");
        saveImage(image3, folder, pid+"image3.jpg");
    }

    private void saveImage(Part image, File folder, String fileName) throws IOException {
        if (image != null && image.getSize() > 0) {
            File file = new File(folder, fileName);
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
