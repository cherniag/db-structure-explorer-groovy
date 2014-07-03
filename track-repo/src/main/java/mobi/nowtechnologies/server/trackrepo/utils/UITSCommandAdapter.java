package mobi.nowtechnologies.server.trackrepo.utils;

import mobi.nowtechnologies.java.server.uits.UITS;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class UITSCommandAdapter {

	private Resource privateKey;


    private UITS uitc;

    public void setUitc(UITS uitc) {
        this.uitc = uitc;
    }

    public void executeDownloadFiles(String sourceFileName, String tempFileName) throws IOException {

        uitc.main(new String [] {privateKey.getFile().getAbsolutePath(), sourceFileName, tempFileName});
	}

	public void executeMobileFiles(String sourceFileName, String audFileName, String hdrFileName, String encFileName) throws IOException {

        uitc.main(new String [] {privateKey.getFile().getAbsolutePath(), sourceFileName, audFileName, hdrFileName, encFileName});
	}

	public void setPrivateKey(Resource privateKey) {
		this.privateKey = privateKey;
	}
}
