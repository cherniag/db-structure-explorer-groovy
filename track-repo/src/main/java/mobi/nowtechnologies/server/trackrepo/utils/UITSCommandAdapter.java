package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import mobi.nowtechnologies.java.server.uits.UITS;

public class UITSCommandAdapter {

	private Resource privateKey;
	
	public void executeDownloadFiles(String sourceFileName, String tempFileName) throws IOException {
		
		UITS.main(new String [] {privateKey.getFile().getAbsolutePath(), sourceFileName, tempFileName});
	}

	public void executeMobileFiles(String sourceFileName, String audFileName, String hdrFileName, String encFileName) throws IOException {
		
		UITS.main(new String [] {privateKey.getFile().getAbsolutePath(), sourceFileName, audFileName, hdrFileName, encFileName});
	}
	
	public static void main(String[] args) throws IOException {
		
		FileUtils.moveFileToDirectory(new File("f:\\Works\\Projects\\musicqubed\\tmp\\beans.txt"), new File("f:\\Works\\Projects\\musicqubed\\tmp\\trackhttp"), true);
	}

	public void setPrivateKey(Resource privateKey) {
		this.privateKey = privateKey;
	}
}
