package mobi.nowtechnologies.server.trackrepo.ingest.emi;

import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;

public class EmiUmgParser extends EmiParser {
    
	public EmiUmgParser(String root) throws FileNotFoundException {
		super(root);
	}
    
    protected String parseProprietaryId(String proprietaryId){
    	if(StringUtils.isEmpty(proprietaryId))
    		return null;
    	
    	String ret = StringUtils.split(proprietaryId, '_')[0];
    	LOGGER.info("Parsed ProprietaryId from {} -> {}", proprietaryId, ret);
    	return ret;
    }
    
    public static void main(String[] args) throws Exception{
		System.out.println(new EmiUmgParser("").parseProprietaryId("00724383702855_USCA28800117_01_001"));
	}

}