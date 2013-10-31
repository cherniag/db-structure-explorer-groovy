package mobi.nowtechnologies.server.shared.enums;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

public enum ProviderType {
	O2("o2"), NON_O2("non-o2"), VF("vf"), NON_VF("non-vf");

	private String key;

    public static ProviderType valueOfKey(String key){
        if (isNull(key)){
            return null;
        }else if(O2.key.equals(key)){
            return O2;
        }else if (NON_O2.key.equals(key)){
            return NON_O2;
        }else if(VF.key.equals(key)){
            return VF;
        }else if (NON_VF.key.equals(key)){
            return NON_VF;
        }else{
            throw new IllegalArgumentException("Unknown key ["+ key +"]");
        }
    }

    private ProviderType(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }

	@Override
	public String toString(){
			return name();
	}
}
