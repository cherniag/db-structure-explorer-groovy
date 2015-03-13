package mobi.nowtechnologies.server.trackrepo.utils;

import mobi.nowtechnologies.server.trackrepo.uits.UITS;

import java.io.IOException;

public class UITSCommandAdapter {

    private UITS uits;

    public void executeDownloadFiles(String sourceFileName, String tempFileName) throws IOException {
        uits.process(sourceFileName, tempFileName, null, null, true);
    }

    public void executeMobileFiles(String sourceFileName, String audFileName, String hdrFileName, String encFileName) throws IOException {
        uits.process(sourceFileName, audFileName, hdrFileName, encFileName, true);
    }

    public void setUits(UITS uits) {
        this.uits = uits;
    }
}
