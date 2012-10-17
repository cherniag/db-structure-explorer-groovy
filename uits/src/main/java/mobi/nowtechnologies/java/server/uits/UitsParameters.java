package mobi.nowtechnologies.java.server.uits;

import java.security.PrivateKey;

public class UitsParameters {
	
	private String distributor;
	private String user;
	private String productId;
	private String isrc;
	private String url;
	private String copyright;
	private PrivateKey key;
	
	public String getDistributor() {
		return distributor;
	}
	public void setDistributor(String distributor) {
		this.distributor = distributor;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getIsrc() {
		return isrc;
	}
	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public PrivateKey getKey() {
		return key;
	}
	public void setKey(PrivateKey key) {
		this.key = key;
	}
	
	

}
