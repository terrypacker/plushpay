package com.payyourself.testing;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class SampleServlet extends HttpServlet
{
    public void saveToSession(HttpServletRequest request)
    {
    	String testparam = request.getParameter("testparam");
    	request.getSession().setAttribute("testAttribute", testparam);
    }
}
