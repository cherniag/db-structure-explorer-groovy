package o2stub;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedeemServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedeemServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("start service");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String phone = request.getParameter("otac_phone_number");
		String otac = request.getParameter("otac");
		LOGGER.info("phone " + phone + " " + otac);
		
		response.setHeader("SET-COOKIE", "otac_auth_code=" + phone);

		printContent(request, out);
		LOGGER.info("completed" + phone);
	}

	private void printContent(HttpServletRequest request, PrintWriter out) {
		out.println("<html><head><title>RedeemServlet stub</title></head><body>");

		printParams(request, out);
		out.println("</body>");
		out.println("</html>");
	}

	private void printParams(HttpServletRequest request, PrintWriter out) {
		out.println("Parameters received ");
		Enumeration<String> enm = request.getParameterNames();
		while (enm.hasMoreElements()) {
			String parameterName = enm.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			out.println("<b>" + parameterName);
			for (int i = 0; i < parameterValues.length; i++) {
				out.println(parameterValues);
			}
			out.println("</b>");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}