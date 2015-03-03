package mobi.nowtechnologies.server.trackrepo.ingest.emi;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class EmiTeleporter {

    private String server;
    private String user;
    private String password;
    private String root;

    public List<String> getDrops() {
        FTPClient f = new FTPClient();
        try {
            f.connect(server);
            f.login(user, password);
        }
        catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getDrops(f, root);
    }

    protected List<String> getDrops(FTPClient f, String dir) {
        List<String> result = new ArrayList<String>();
        System.out.println("Listing " + dir);
        try {
            FTPFile[] files = f.listFiles(dir);
            for (FTPFile file : files) {
                if (file.isDirectory()) {
                    System.out.println("Listing sub dir " + file.getName());
                    result.addAll(getDrops(f, dir + "/" + file.getName()));
                }
                else {
                    if (file.getName().endsWith(".xml")) {
                        System.out.println("Adding " + dir + " for " + file.getName());
                        result.add(dir);
                    }
                }
            }
        }
        catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public void transferFiles(String dir) {
        FTPClient f = new FTPClient();
        try {
            f.connect(server);
            f.login(user, password);

        }
        catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
