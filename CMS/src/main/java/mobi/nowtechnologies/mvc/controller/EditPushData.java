package mobi.nowtechnologies.mvc.controller;

import java.util.List;
import java.util.Map;

public class EditPushData {
	
	public class PushProperty {
		private String key;
		private String ticker;
		private String title;
		private String body;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getTicker() {
			return ticker;
		}
		public void setTicker(String ticker) {
			this.ticker = ticker;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		
	}

	private List<PushProperty> weeklyData;
	private List<PushProperty> messageData;
	private PushProperty editData;
	public List<PushProperty> getWeeklyData() {
		return weeklyData;
	}
	public void setWeeklyData(List<PushProperty> weeklyData) {
		this.weeklyData = weeklyData;
	}
	public List<PushProperty> getMessageData() {
		return messageData;
	}
	public void setMessageData(List<PushProperty> messageData) {
		this.messageData = messageData;
	}
	public PushProperty getEditData() {
		return editData;
	}
	public void setEditData(PushProperty editdata) {
		this.editData = editdata;
	}
	
	
	
	
	
	
	
	
}
