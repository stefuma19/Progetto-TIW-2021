package it.polimi.tiw.progetto.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ShowImage
 */
@WebServlet("/ShowImage")
public class ShowImage extends HttpServlet { //TODO: CLASSE DA TOGLIERE
	private static final long serialVersionUID = 1L;

	String folderPath = "";

	public void init() throws ServletException {
		// get folder path from webapp init parameters inside web.xml
		folderPath = getServletContext().getInitParameter("outputpath");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//String fileName = request.getParameter("filename");
		String fileName = "norwegianwood.jpg";

		if (fileName == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
			return;
		}

		String outputPath = folderPath + fileName;

		System.out.println("Output path: " + outputPath);

		File f = new File(outputPath);
		if (!f.exists() || f.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not present");
			return;
		} else {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			out.println("<HTML><BODY>");
			out.println("<HEAD><TITLE>Show image</TITLE></HEAD>");

			out.println("<P>" + "Image uploaded correctly!" + "</P>");

			// use another servlet to get file from disk
			out.println("Click on <a href=GetImage/" + URLEncoder.encode(fileName, "utf-8") + ">" + fileName
					+ "</a> to view your image in a new tab!");

			// when uploading an image, you can directly show it inside an img tag
			// TODO: uncomment following line to test what happens
			// out.println("<br><img src=GetImage/" + URLEncoder.encode(fileName, "utf-8") + " height=100 width=100>" + "</img>");

			out.println("</HTML></BODY>");
			out.close();
		}

	}

}
