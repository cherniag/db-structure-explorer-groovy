package mobi.nowtechnologies.server.trackrepo.dto;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: sanya Date: 7/15/13 Time: 2:52 PM
 */
public class DropDto {

    private String ingestor;
    private String name;
    private Date date;
    private List<DropTrackDto> tracks;
    private Boolean selected;

    public String getIngestor() {
        return ingestor;
    }

    public void setIngestor(String ingestor) {
        this.ingestor = ingestor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<DropTrackDto> getTracks() {
        return tracks;
    }

    public void setTracks(List<DropTrackDto> tracks) {
        this.tracks = tracks;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public boolean hasAnySelected() {
        for (DropTrackDto drop : tracks) {
            if (drop.getIngest() != null && drop.getIngest()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "DropDto{" +
               "ingestor='" + ingestor + '\'' +
               ", name='" + name + '\'' +
               ", date=" + date +
               ", tracks=" + tracks +
               ", selected=" + selected +
               "} " + super.toString();
    }
}
