package o2stub;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckPhoneServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckPhoneServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("start service");
		PrintWriter out = response.getWriter();
		String phone = request.getParameter("phone_number");

		SubsData subsData = PhoneNumberManager.getInstance().getData(phone);
		LOGGER.info("phone " + phone + " " + subsData);

		printContent(request, out, subsData);
		LOGGER.info("completed" + phone);
	}

	private void printContent(HttpServletRequest request, PrintWriter out, SubsData subsData) {
		out.println("<html><head><title>Check phone " + subsData.getPhoneNumberWithCode() + "</title></head><body>");
		out.println("  " + subsData);
		out.println("</body>");
		out.println("</html>");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}