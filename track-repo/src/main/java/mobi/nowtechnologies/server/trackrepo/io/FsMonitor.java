package mobi.nowtechnologies.server.trackrepo.io;

import mobi.nowtechnologies.server.trackrepo.ingest.IParser;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FsMonitor extends org.apache.commons.io.monitor.FileAlterationListenerAdaptor {

    Map<String, IParser> parserMap = new HashMap<String, IParser>();

    public FsMonitor(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory())
            throw new RuntimeException("Folder does not exist: " + folder.getAbsolutePath());
    }

    public FsMonitor addParser(String folderPath, IParser parser) {
        parserMap.put(folderPath, parser);
        return this;
    }

    @Override
    public void onFileCreate(File file) {
        IParser parser = parserMap.get(file.getPath());
        //todo
    }

    @Override
    public void onFileChange(File file) {
       //todo
    }

    @Override
    public void onFileDelete(File file) {
        //todo
    }

    public FileAlterationMonitor scan(File folder) throws Exception {
        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor = new FileAlterationMonitor(5000);
        observer.addListener(this);
        monitor.addObserver(observer);
        return monitor;
        //monitor.start();
    }
}
