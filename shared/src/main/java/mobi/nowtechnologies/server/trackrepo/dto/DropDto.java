package mobi.nowtechnologies.server.trackrepo.dto;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DropDto {
    private String name;
    private Date date;
    private List<DropTrackDto> tracks;
    private boolean selected;

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "DropDto{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", tracks=" + tracks +
                ", selected=" + selected +
                "} " + super.toString();
    }
}
