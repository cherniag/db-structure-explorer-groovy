package mobi.nowtechnologies.server.service.file.file;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileService implements InitializingBean {
    private File rootDir;

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public boolean createRoot(String dirName) {
        Assert.notNull(dirName);

        File target = new File(rootDir, dirName);

        return target.mkdir();
    }

    public boolean create(String path, String dirName) {
        Assert.notNull(dirName);

        File newDirToCreate = new File(getFile(path), dirName);
        return newDirToCreate.mkdir();
    }


    public boolean remove(String path) {
        return getFile(path).delete();
    }

    public List<FileInfo> getRootDirContent() {
        List<FileInfo> infos = new ArrayList<FileInfo>();

        File[] list = rootDir.listFiles();
        if(list != null) {
            for (File file : Arrays.asList(list)) {
                infos.add(convertToInfo(file));
            }
        }

        return infos;
    }

    public void upload(String path, File file) {
        File dest = new File(getFile(path), file.getName());

        Assert.isTrue(file.exists(), "File to upload does not exist");
        Assert.isTrue(!dest.exists(), "File with such name exists");

        try {
            FileUtils.copyFile(file, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile(String path) {
        File target = new File(path);
        Assert.isTrue(target.exists(), "File by path: " + path + " does not exist");
        return target;
    }

    private FileInfo convertToInfo(File file) {
        FileInfo fileInfo = new FileInfo(file);

        File[] filesAndDirs = file.listFiles();

        if(filesAndDirs != null) {
            for (File f : Arrays.asList(filesAndDirs)) {
                fileInfo.addChild(convertToInfo(f));
            }
        }

        return fileInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rootDir);
        Assert.isTrue(rootDir.exists(), "File " + rootDir + " does not exist");
        Assert.isTrue(rootDir.isDirectory(), "File " + rootDir + " is not directory");
    }
}
