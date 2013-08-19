package mobi.nowtechnologies.server.trackrepo.ingest;

import org.apache.commons.lang.builder.ToStringBuilder;

import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;

public class DropAssetFile {
	public String file;
	public FileType type;
	public String md5;
	public String isrc;
    public Integer duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DropAssetFile)) return false;

        DropAssetFile that = (DropAssetFile) o;

        if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (isrc != null ? !isrc.equals(that.isrc) : that.isrc != null) return false;
        if (md5 != null ? !md5.equals(that.md5) : that.md5 != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (isrc != null ? isrc.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("file", file)
                .append("type", type)
                .append("md5", md5)
                .append("isrc", isrc)
                .append("duration", duration)
                .toString();
    }
}
