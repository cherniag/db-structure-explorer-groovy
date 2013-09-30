package mobi.nowtechnologies.server.persistence.domain.enums;

public enum ProviderType {
	O2("o2"), NON_O2("non-o2"), VF("vf"), NON_VF("non-vf");
	
	private String value;
	
	private ProviderType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
