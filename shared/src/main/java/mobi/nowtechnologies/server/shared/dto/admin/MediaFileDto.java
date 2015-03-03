package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.trackrepo.enums.FileType;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MediaFileDto {

    private int id;

    private String filename;

    private FileType fileType;

    private int size;

    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFormattedDuration() {
        if (duration > 0) {
            return "" + (duration / 60000) + ":" + String.format("%02d", (duration / 1000) % 60);
        }

        return "0:00";
    }

    public void setFormattedDuration(String value) {

    }

    @Override
    public String toString() {
        return "MediaFileDto [fileType=" + fileType + ", filename=" + filename + ", id=" + id + ", size=" + size + "]";
    }

}
