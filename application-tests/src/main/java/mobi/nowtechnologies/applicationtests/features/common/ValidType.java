package mobi.nowtechnologies.applicationtests.features.common;

import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.hash.Hashing;
import org.apache.commons.lang.StringUtils;

import org.springframework.util.Assert;

public enum ValidType {
    Valid, NotValid;

    public String decide(String data) {
        if (this == NotValid) {
            return spoil(data);
        }
        else {
            return data;
        }
    }

    private String spoil(String data) {
        Assert.hasText(data);

        Charset utf8 = Charset.forName("UTF-8");

        String random = data + UUID.randomUUID().toString();

        String md5 = Hashing.md5().hashString(random, utf8).toString();

        return StringUtils.substring(md5, 0, data.length());
    }
}
