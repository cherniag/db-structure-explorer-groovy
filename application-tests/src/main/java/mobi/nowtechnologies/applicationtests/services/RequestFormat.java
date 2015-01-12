package mobi.nowtechnologies.applicationtests.services;

public enum RequestFormat {
    JSON(".json"), XML("");

    private String ext;

    RequestFormat(String ext) {
        this.ext = ext;
    }

    public boolean json() {
        return JSON == this;
    }

    public String getExt() {
        return ext;
    }
}
