package mobi.nowtechnologies.server.dto.transport;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * AccountCheck
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@XmlRootElement(name = "user")
public class AccountCheckDTO extends mobi.nowtechnologies.server.shared.dto.AccountCheckDTO{
	
	@XmlAnyElement
	private SelectedPlaylistDto[] playlists;
	
	public AccountCheckDTO(){
		
	}
	
	public AccountCheckDTO(mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO) {
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
