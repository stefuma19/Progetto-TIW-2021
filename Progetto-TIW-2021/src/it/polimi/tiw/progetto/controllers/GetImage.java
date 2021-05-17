package it.polimi.tiw.progetto.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetFile
 */
@WebServlet("/GetImage/*")
public class GetImage extends HttpServlet { //TODO: CLASSE DA TOGLIERE
	private static final long serialVersionUID = 1L;

	String folderPath = "";

	public void init() throws ServletException {
		// get folder path from webapp init parameters inside web.xml
		folderPath = getServletContext().getInitParameter("outputpath");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		// PathInfo: The part of the request path that is not part of the Context Path
		// or the Servlet Path.
		// It is either null if there is no extra path, or is a string with a leading
		// ‘/’

		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
			return;
		}

		// substring(1) useful to remove first "/" in path info
		// because it is not part of the filename
		String filename = URLDecoder.decode(pathInfo.substring(1), "UTF-8");

		File file = new File(folderPath, filename); //folderPath inizialized in init
		System.out.println(filename);

		if (!file.exists() || file.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not present");
			return;
		}

		// set headers for browser
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		//TODO: test what happens  if you change inline by  attachment
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
																									
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());
	}
}
