/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Admin_DTO;
import dto.Response_DTO;
import entity.Admin;
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
@WebServlet(name = "AdminSignIn", urlPatterns = {"/AdminSignIn"})
public class AdminSignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Admin_DTO admin_DTO = gson.fromJson(request.getReader(), Admin_DTO.class);

        try {
            if (admin_DTO.getEmail().isEmpty()) {
                response_DTO.setContent("Please enter your Email");

            } else if (admin_DTO.getPassword().isEmpty()) {
                response_DTO.setContent("Please enter your Password");
            } else {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Criteria criteria1 = session.createCriteria(Admin.class);
                criteria1.add(Restrictions.eq("email", admin_DTO.getEmail()));
                criteria1.add(Restrictions.eq("password", admin_DTO.getPassword()));

                if (!criteria1.list().isEmpty()) {
                    Admin admin = (Admin) criteria1.list().get(0);
                    admin_DTO.setId(admin.getId());
                    admin_DTO.setFirst_name(admin.getFirst_name());
                    admin_DTO.setLast_name(admin.getLast_name());
                    admin_DTO.setPassword(null);
                    request.getSession().setAttribute("user", admin_DTO);

                    response_DTO.setSuccess(true);
                    response_DTO.setContent("Sign In Success");
                }
            }
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
            System.out.println(gson.toJson(response_DTO));
        } catch (Exception e) {
        }
    }

}
