package com.payyourself.registration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.payyourself.persistence.HibernateUtil;
import com.payyourself.userManagement.role.Role;
import com.payyourself.userManagement.role.RoleHibernation;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;

/**
 * Servlet implementation class ConfirmRegistration
 */
public class ConfirmRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmRegistration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String newUserIDs = request.getParameter("confirm");
		
		String[] parts = newUserIDs.split("\\*");
		
		if(parts.length != 2){
			response.sendRedirect("register_fail.jsp");
		}
		HibernateUtil.getSessionFactory(); // Just call the static initializer of that class
		/*Collect Registration info */
		RegistrationInfoHibernation rih = new RegistrationInfoHibernation();
		Session session = rih.getSessionFactory().getCurrentSession();
		if(!session.isOpen()){
			session = rih.getSessionFactory().openSession();
		}
		session.beginTransaction();
		RegistrationInfo userRego = rih.findById(Integer.parseInt(parts[0]));
		
		if(userRego==null){
			response.sendRedirect("register_fail.jsp");
		}
		
		/*Make sure the username AND the id match (old records or something)*/
		if((!userRego.getUsername().equals(parts[1])||(userRego.getRegistrationid() != Integer.parseInt(parts[0])))){
			response.sendRedirect("register_fail.jsp");
			
		}
		
		/* Transfer the data */
		User newUser = new User();
		newUser.setFirstname(userRego.getFirstname());
		newUser.setLastname(userRego.getLastname());
		newUser.setPassword(userRego.getPassword());
		newUser.setUsername(userRego.getUsername());
		newUser.setEmail(userRego.getEmail());
		newUser.setCanTrade(true); //This is for inital use only, in the real system this will be set later.
		
		/* Put them into the Users Table*/
		UserHibernation userh = new UserHibernation();
		userh.attachDirty(newUser);
		
		/*Also have to give this user a user role */
		RoleHibernation rh = new RoleHibernation();
		Role newRole = new Role();
		newRole.setRole("user");
		newRole.setUsername(userRego.getUsername());
		rh.attachDirty(newRole);
		
		/*Remove from rego table*/
		rih.delete(userRego);
		
		response.sendRedirect("register_success.jsp");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Do Post");
	}

}
