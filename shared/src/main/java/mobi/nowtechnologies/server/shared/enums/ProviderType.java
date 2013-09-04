package mobi.nowtechnologies.server.shared.enums;

public enum ProviderType {
	O2("o2"), NON_O2("non-o2");
	
	private String key;

    public static ProviderType valueOfKey(String key){
        if(O2.key.equals(key)){
            return O2;
        }else if (NON_O2.key.equals(key)){
            return NON_O2;
        }else{
            throw new IllegalArgumentException("Unknown key ["+ key +"]");
        }
    }

	private ProviderType(String key){
		this.key = key;
	}

	@Override
	public String toString(){
		return key;
	}
}
