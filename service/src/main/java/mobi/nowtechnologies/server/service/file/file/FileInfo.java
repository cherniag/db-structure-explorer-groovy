package mobi.nowtechnologies.server.service.file.file;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class FileInfo implements Comparable<FileInfo> {
    private String path;
    private String name;
    private boolean file;
    private Set<FileInfo> childs = new TreeSet<FileInfo>();

    public FileInfo(File file) {
        this.path = file.getPath();
        this.name = file.getName();
        this.file = file.isFile();
    }

    public void addChild(FileInfo fileInfo) {
        childs.add(fileInfo);
    }

    public Set<FileInfo> getChildren() {
        return new HashSet<FileInfo>(childs);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (!path.equals(fileInfo.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", path)
                .append("name", name)
                .append("file", file)
                .toString();
    }

    @Override
    public int compareTo(FileInfo o) {
        return compareAlphabetically(name, o.name);
    }

    private int compareAlphabetically(String first, String second) {
        int min = Math.min(first.length(), second.length());

        String firstCut = first.substring(0, min);
        String secondCut = second.substring(0, min);

        return firstCut.compareTo(secondCut);
    }
}
