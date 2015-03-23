package mobi.nowtechnologies.server.trackrepo.ingest;

import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DropAssetFile {

    public String file;
    public FileType type;
    public String md5;
    public String isrc;
    public Integer duration;

    @Override
    public int hashCode() {
        return Objects.hash(file, type, md5, isrc, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DropAssetFile other = (DropAssetFile) obj;
        return Objects.equals(this.file, other.file) && Objects.equals(this.type, other.type) && Objects.equals(this.md5, other.md5) && Objects.equals(this.isrc, other.isrc) &&
               Objects.equals(this.duration, other.duration);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("file", file).append("type", type).append("md5", md5).append("isrc", isrc).append("duration", duration).toString();
    }
}
