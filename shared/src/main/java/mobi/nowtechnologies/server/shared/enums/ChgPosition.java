package mobi.nowtechnologies.server.shared.enums;


public enum ChgPosition {
	NONE,
	UP,
	DOWN,
	UNCHANGED;
	
	public String getLabel(){
		return this.name();
	}
}
