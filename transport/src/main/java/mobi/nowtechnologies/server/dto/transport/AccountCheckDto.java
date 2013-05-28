package mobi.nowtechnologies.server.dto.transport;

import java.util.Arrays;

import javax.xml.bind.annotation.*;

/**
 * AccountCheck
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
public class AccountCheckDto extends mobi.nowtechnologies.server.shared.dto.AccountCheckDTO{
	
	@XmlAnyElement
	private SelectedPlaylistDto[] playlists;
	
	public AccountCheckDto(){
		
	}
	
	public AccountCheckDto(mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO) {
		super(accountCheckDTO);
	}

	public SelectedPlaylistDto[] getPlaylists() {
		return playlists;
	}

	public void setPlaylists(SelectedPlaylistDto[] playlists) {
		this.playlists = playlists;
	}

	@Override
	public String toString() {
		return "AccountCheckDTO [playlists=" + Arrays.toString(playlists) + "]" + super.toString();
	}
}
