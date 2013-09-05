package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Arrays;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
public class AccountCheckDto extends mobi.nowtechnologies.server.shared.dto.AccountCheckDTO{

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
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append("playlists", playlists)
                .append("lockedTracks", lockedTracks)
                .toString();
    }
}
