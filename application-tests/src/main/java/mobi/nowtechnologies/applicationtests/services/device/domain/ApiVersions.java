package mobi.nowtechnologies.applicationtests.services.device.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.Assert;

public class ApiVersions {

    private List<String> versions = new ArrayList<>();

    private ApiVersions() {
    }

    public static ApiVersions from(Collection<String> versions) {
        ApiVersions apiVersions = new ApiVersions();
        apiVersions.versions.addAll(versions);
        Collections.sort(apiVersions.versions, new Comparator<String>() {
            @Override
            public int compare(String v1, String v2) {
                v1 = v1.replaceAll("\\s", "");
                v2 = v2.replaceAll("\\s", "");
                String[] a1 = v1.split("\\.");
                String[] a2 = v2.split("\\.");
                List<String> l1 = Arrays.asList(a1);
                List<String> l2 = Arrays.asList(a2);


                int i=0;
                while(true){
                    Double d1 = null;
                    Double d2 = null;

                    try{
                        d1 = Double.parseDouble(l1.get(i));
                    }catch(IndexOutOfBoundsException e){
                    }

                    try{
                        d2 = Double.parseDouble(l2.get(i));
                    }catch(IndexOutOfBoundsException e){
                    }

                    if (d1 != null && d2 != null) {
                        if (d1.doubleValue() > d2.doubleValue()) {
                            return 1;
                        } else if (d1.doubleValue() < d2.doubleValue()) {
                            return -1;
                        }
                    } else if (d2 == null && d1 != null) {
                        if (d1.doubleValue() > 0) {
                            return 1;
                        }
                    } else if (d1 == null && d2 != null) {
                        if (d2.doubleValue() > 0) {
                            return -1;
                        }
                    } else {
                        break;
                    }
                    i++;
                }
                return 0;
            }
        });
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
        return (SubSetType.ABOVE == subSetType) ?
               above(of) :
               bellow(of);
    }

    public static enum SubSetType {
        BELOW, ABOVE
    }
}
