package mobi.nowtechnologies.server.dto.transport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
@JsonTypeName("user")
public class AccountCheckDto extends mobi.nowtechnologies.server.shared.dto.AccountCheckDTO {

    @XmlAnyElement
    public SelectedPlaylistDto[] playlists;

    @XmlAnyElement
    public LockedTrackDto[] lockedTracks;

    public AccountCheckDto() {}

    public AccountCheckDto(mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO) {
        super(accountCheckDTO);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("playlists", playlists).append("lockedTracks", lockedTracks).toString();
    }
}
