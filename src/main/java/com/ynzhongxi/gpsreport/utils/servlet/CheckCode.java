package com.ynzhongxi.gpsreport.utils.servlet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class CheckCode  {
	@GetMapping("/CheckCode")
	public void CheckCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		String rand = (String) session.getAttribute("rand") ;
		System.out.println("++++++");
		if (rand == null || "".equals(rand)) {
			response.getWriter().print(false);
		} else {
			response.getWriter().print(rand.equalsIgnoreCase(request.getParameter("code")));
		}
	}
}