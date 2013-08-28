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

public class UpdatePhoneServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePhoneServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("start service");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String phone = request.getParameter("phone_number");

		SubsData subsData = new SubsData();
		readFlags(request, subsData);

		PhoneNumberManager.getInstance().setPhoneDetails(phone, subsData);

		LOGGER.info("phone " + phone + " " + subsData);

		printContent(request, out);
		LOGGER.info("completed" + phone);
	}

	private void readFlags(HttpServletRequest request, SubsData subsData) {

		subsData.setO2("true".equals(request.getParameter("o2")));
		subsData.setBusiness("true".equals(request.getParameter("business")));
		subsData.setPayAsYouGo("true".equals(request.getParameter("payAsYouGo")));
		subsData.setTariff4G("true".equals(request.getParameter("tariff4G")));
		subsData.setDirectChannel4G("true".equals(request.getParameter("directChannel4G")));

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