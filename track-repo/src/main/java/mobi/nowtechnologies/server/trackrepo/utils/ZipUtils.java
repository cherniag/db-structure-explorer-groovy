package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class ZipUtils {

    public final void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    public final void unzip(String filepath) {
        unzip(filepath, null);
    }

    @SuppressWarnings("rawtypes")
    public final void unzip(String filepath, Boolean isAllExecutable) {
        Enumeration entries;
        ZipFile zipFile;
        try {
            File file = new File(filepath);
            String dir = file.getParent();
            String filename = file.getName();
            String unzipFolderName = filename.substring(0, filename.indexOf("."));
            File unzipFolder = new File(dir + File.separator + unzipFolderName);
            if (unzipFolder.exists()) {
                FileUtils.deleteDirectory(unzipFolder);
            }

            zipFile = new ZipFile(file);
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
                    System.err.println("Extracting directory: " + entry.getName());
                    // This is not robust, just for demonstration purposes.
                    (new File(dir + File.separator + entry.getName())).mkdir();
                    continue;
                }
                System.err.println("Extracting file: " + entry.getName());
                File unzipFile = new File(dir + File.separator + entry.getName());
                copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(unzipFile)));
                if (isAllExecutable != null) {
                    unzipFile.setExecutable(isAllExecutable);
                }
            }
            zipFile.close();
        }
        catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }
}