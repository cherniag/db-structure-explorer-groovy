package mobi.nowtechnologies.mvc.controller;

import java.util.List;

public class RemoteFiles {
	
	public class RemoteFile {
	private String name;
	private boolean selected;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	}
	
	private List<RemoteFile> files;

	public List<RemoteFile> getFiles() {
		return files;
	}

	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}
	
	
	

}
