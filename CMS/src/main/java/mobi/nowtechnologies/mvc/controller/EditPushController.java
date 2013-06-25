package mobi.nowtechnologies.mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.util.Constants;
import mobi.nowtechnologies.util.Property;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EditPushController extends SimpleFormController implements Constants {

	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {

		String id = req.getParameter("id");
		EditPushData.PushProperty editData = null;

		Properties messageProps = new Properties();
		FileInputStream inStream = new FileInputStream(Property.getInstance().getStringValue("push.message"));
		InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
		messageProps.load(reader);
		inStream.close();
		reader.close();

		EditPushData data = new EditPushData();

		List<EditPushData.PushProperty> messages = new ArrayList<EditPushData.PushProperty>();
		data.setMessageData(messages);
		List<EditPushData.PushProperty> weekly = new ArrayList<EditPushData.PushProperty>();
		data.setWeeklyData(weekly);

		JsonParser parser = new JsonParser();
		for (Entry<Object, Object> entry : messageProps.entrySet()) {
			JsonElement e = parser.parse((String) entry.getValue());
			JsonObject o = e.getAsJsonObject();
			JsonElement eData = o.getAsJsonObject("data");

			// PushProperty
			EditPushData.PushProperty prop = data.new PushProperty();
			messages.add(prop);
			prop.setKey((String) entry.getKey());
			prop.setTicker(eData.getAsJsonObject().get("tickerText").getAsString());
			prop.setTitle(eData.getAsJsonObject().get("title").getAsString());
			prop.setBody(eData.getAsJsonObject().get("text").getAsString());
			if (id.equals(prop.getKey())) {
				editData = prop;
			}
			System.out.println("Ticker " + prop.getTicker());
		}

		data.setEditData(editData);

		req.getSession().setAttribute("PushMessages", data);
		return data;

	}

	@Override
	protected ModelAndView onSubmit(Object arg0) throws Exception {
		EditPushData data = (EditPushData) arg0;
		EditPushData.PushProperty editData = data.getEditData();

		FileInputStream inStream = new FileInputStream(Property.getInstance().getStringValue("push.message"));
		InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
		Properties messageProps = new Properties();
		messageProps.load(reader);
		inStream.close();
		reader.close();

		Date date = new Date();
		File messageFile = new File(Property.getInstance().getStringValue("push.message"));
		File messageFileBU = new File(Property.getInstance().getStringValue("push.message") + date.getTime());

		messageFile.renameTo(messageFileBU);

		FileOutputStream out = new FileOutputStream(messageFile);
		System.out.println(editData.getBody().replaceAll("\\r\\n|\\r|\\n", " "));
		System.out.println(editData.getBody());

		String line = null;
		line = "{\"type\":\"NotificationDataText\",\"data\":{\"tickerText\":\"" + editData.getTicker().replaceAll("\\r\\n|\\r|\\n", " ")
				+ "\", \"title\":\"" + editData.getTitle().replaceAll("\\r\\n|\\r|\\n", " ") + "\", \"text\":\""
				+ editData.getBody().replaceAll("\\r\\n|\\r|\\n", " ") + "\" , \"action\":\"actionD\"}}";
		messageProps.put(editData.getKey(), line);

		OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
		messageProps.store(writer, "");
		out.flush();
		out.close();

		return super.onSubmit(arg0);
	}

}
