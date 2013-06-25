package mobi.nowtechnologies.ingestors.emi;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.util.Property;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class EmiTeleporter {
	
	public List<String> getDrops() {
	    FTPClient f = new FTPClient();
	    try {
			f.connect(Property.getInstance().getStringValue("ingest.emi.ftp.server"));
		    f.login(Property.getInstance().getStringValue("ingest.emi.ftp.user"), Property.getInstance().getStringValue("ingest.emi.ftp.password"));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getDrops(f,Property.getInstance().getStringValue("ingest.emi.ftp.root"));
	}
	
	protected List<String> getDrops(FTPClient f, String dir) {
	    List<String> result = new ArrayList<String>();
	    System.out.println("Listing "+dir);
	    try {
		    FTPFile[] files = f.listFiles(dir);
		    for (FTPFile file:files) {
		    	if (file.isDirectory()) {
		    	    System.out.println("Listing sub dir "+file.getName());
		    		result.addAll(getDrops(f, dir+"/"+file.getName()));
		    	} else {
		    		if (file.getName().endsWith(".xml")) {
		    		    System.out.println("Adding "+dir+" for "+file.getName());
		    			result.add(dir);
		    		}
		    	}
		    }
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void transferFiles(String dir) {
	    FTPClient f = new FTPClient();
	    try {
			f.connect(Property.getInstance().getStringValue("ingest.emi.ftp.server"));
		    f.login(Property.getInstance().getStringValue("ingest.emi.ftp.user"), Property.getInstance().getStringValue("ingest.emi.ftp.password"));
		    
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
