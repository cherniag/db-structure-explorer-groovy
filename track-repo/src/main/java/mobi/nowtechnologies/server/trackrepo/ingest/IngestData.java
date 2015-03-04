package mobi.nowtechnologies.server.trackrepo.ingest;

import java.util.List;
import java.util.Map;

public class IngestData {

    Map<String, DropTrack> tracks;
    List<Track> data;
    Object drop;

    public Map<String, DropTrack> getTracks() {
        return tracks;
    }

    public void setTracks(Map<String, DropTrack> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

    public Object getDrop() {
        return drop;
    }

    public void setDrop(Object drop) {
        this.drop = drop;
    }

    public class Track {

        public String ISRC;
        public String title;
        public String artist;
        public Boolean exists;
        public Boolean ingest = true;
        public DropTrack.Type type;
        public String productCode;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productId) {
            this.productCode = productId;
        }

        public DropTrack.Type getType() {
            return type;
        }

        public void setType(DropTrack.Type type) {
            this.type = type;
        }

        public String getISRC() {
            return ISRC;
        }

        public void setISRC(String iSRC) {
            ISRC = iSRC;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public Boolean getExists() {
            return exists;
        }

        public void setExists(Boolean exists) {
            this.exists = exists;
        }

        public Boolean getIngest() {
            return ingest;
        }

        public void setIngest(Boolean ingest) {
            this.ingest = ingest;
        }

    }

}
