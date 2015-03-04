package mobi.nowtechnologies.server.dto.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

public class BadgeInfoDto {

    private String file;

    @Length(min = 0, max = FilenameAlias.NAME_ALIAS_MAX_LENGTH)
    private String title;

    @Min(1)
    private int width;

    @Min(1)
    private int height;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "BadgeInfoDto{" +
               "file='" + file + '\'' +
               ", title='" + title + '\'' +
               ", width=" + width +
               ", height=" + height +
               '}';
    }
}
