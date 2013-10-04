package mobi.nowtechnologies.server.persistence.domain.enums;

public enum ProviderType {
    O2("o2"), NON_O2("non-o2"), OFF_NET("off-net"), ON_NET("on-net");
	
	private String value;
	
	private ProviderType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
