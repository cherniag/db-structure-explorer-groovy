package mobi.nowtechnologies.server.trackrepo.ingest;


public class IngestWizardData {

    private String suid;
	private DropsData dropdata;
	private int size;
	
	public DropsData getDropdata() {
		return dropdata;
	}
	public void setDropdata(DropsData dropdata) {
		this.dropdata = dropdata;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }
}
