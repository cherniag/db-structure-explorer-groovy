package mobi.nowtechnologies.mvc.controller;

import java.util.List;

import mobi.nowtechnologies.domain.Territory;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.server.persistence.domain.Artist;

public class PublishData {
	private Track track;
	private Artist artist;
	private boolean highRate;
	private String publishArtist;
	private String publishTitle;
	private String iTunesUrl;
	private String editiTunesUrl;
	private List<Territory> territories;
	
	public Track getTrack() {
		return track;
	}
	public void setTrack(Track track) {
		this.track = track;
	}
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	public boolean isHighRate() {
		return highRate;
	}
	public void setHighRate(boolean highRate) {
		this.highRate = highRate;
	}
	public String getPublishArtist() {
		return publishArtist;
	}
	public void setPublishArtist(String publishArtist) {
		this.publishArtist = publishArtist;
	}
	public String getPublishTitle() {
		return publishTitle;
	}
	public void setPublishTitle(String publishTitle) {
		this.publishTitle = publishTitle;
	}
	public String getiTunesUrl() {
		return iTunesUrl;
	}
	public void setiTunesUrl(String iTunesUrl) {
		this.iTunesUrl = iTunesUrl;
	}
	public String getEditiTunesUrl() {
		return editiTunesUrl;
	}
	public void setEditiTunesUrl(String editiTunesUrl) {
		this.editiTunesUrl = editiTunesUrl;
	}
	public List<Territory> getTerritories() {
		return territories;
	}
	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}
	
}
