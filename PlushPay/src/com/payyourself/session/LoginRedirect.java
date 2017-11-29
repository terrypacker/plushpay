package com.payyourself.session;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class LoginRedirect
 * 
 * This will redirect the user to the appropriate area
 * 
 */
public class LoginRedirect extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginRedirect() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if(request.isUserInRole("administrator")){
			response.sendRedirect("/PayYourself/app/admin_area/administration.jsp");
		}else if(request.isUserInRole("manager")){
			response.sendRedirect("/PayYourself/app/management_area/management.jsp");
		}else if(request.isUserInRole("accounting")){
			response.sendRedirect("/PayYourself/app/accounting_area/accounting.jsp");
		}else if(request.isUserInRole("user")){
			response.sendRedirect("/PayYourself/app/user_area/user.jsp");
		}else{
			response.sendRedirect("/PayYourself/app/error/error.jsp");
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
