package mobi.nowtechnologies.mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.mvc.controller.EditPushData.PushProperty;
import mobi.nowtechnologies.util.Constants;
import mobi.nowtechnologies.util.Property;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ListPushController extends SimpleFormController implements Constants {

	private class messageComparator implements Comparator<EditPushData.PushProperty> {

		public int compare(PushProperty o1, PushProperty o2) {
			// TODO Auto-generated method stub
			return o1.getKey().compareTo(o2.getKey());
		}
		
	}
	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		
		Properties messageProps = new Properties();
		FileInputStream inStream = new FileInputStream(Property.getInstance().getStringValue("push.message"));
	    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
		messageProps.load (reader);
		inStream.close();
		reader.close();
		
		EditPushData data = new EditPushData();
		
		List<EditPushData.PushProperty> messages = new ArrayList<EditPushData.PushProperty>();
		data.setMessageData(messages);
		
		JsonParser parser = new JsonParser();
		for (Entry<Object, Object> entry: messageProps.entrySet()) {
//			System.out.println(" prop "+entry.getValue());
			JsonElement e = parser.parse((String)entry.getValue());
			JsonObject o = e.getAsJsonObject();
			JsonElement eData = o.getAsJsonObject("data");
			
			//PushProperty
			EditPushData.PushProperty prop = data.new PushProperty();
			messages.add(prop);
			prop.setKey((String)entry.getKey());
			prop.setTicker(eData.getAsJsonObject().get("tickerText").getAsString());
			prop.setTitle(eData.getAsJsonObject().get("title").getAsString());
			prop.setBody(eData.getAsJsonObject().get("text").getAsString());
			System.out.println("Ticker "+prop.getTicker());
			System.out.println("Body "+prop.getBody());
		}
		Collections.sort(messages, new messageComparator());

		return data;

	}

	@Override
	protected ModelAndView onSubmit(Object arg0) throws Exception {
		
		
		return super.onSubmit(arg0);
	}

}
