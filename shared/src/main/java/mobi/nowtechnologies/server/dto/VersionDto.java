package mobi.nowtechnologies.server.dto;

public class VersionDto {
	
	private String build;
	private String version;
	private String revision;
	private String info;
    private String branchName;


    public VersionDto() {
	}
	
	public String getBuild() {
		return build;
	}
	
	public void setBuild(String build) {
		this.build = build;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
		this.info = "Version: "+this.version;
	}
	
	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getInfo() {
		return info;
	}

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

}