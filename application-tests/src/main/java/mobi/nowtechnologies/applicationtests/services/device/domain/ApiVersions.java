package mobi.nowtechnologies.applicationtests.services.device.domain;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ApiVersions {
    public static enum SubSetType {
        BELOW, ABOVE
    }

    private List<String> versions = new ArrayList<>();

    private ApiVersions() {
    }

    public static ApiVersions from(Collection<String> versions) {
        ApiVersions apiVersions = new ApiVersions();
        apiVersions.versions.addAll(versions);
        Collections.sort(apiVersions.versions);
        return apiVersions;
    }

    public List<String> above(String of) {
        int indexOf = versions.indexOf(of);
        Assert.isTrue(indexOf >= 0, "Not found version " + of + " in (" + versions + ")");
        return new ArrayList<>(versions.subList(indexOf, versions.size()));
    }

    public List<String> bellow(String of) {
        int indexOf = versions.indexOf(of);
        return new ArrayList<>(versions.subList(0, indexOf));
    }

    public List<String> of(String of, SubSetType subSetType) {
        return (SubSetType.ABOVE == subSetType) ? above(of) : bellow(of);
    }
}
