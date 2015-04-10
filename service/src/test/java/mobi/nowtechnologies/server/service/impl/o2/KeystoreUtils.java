package mobi.nowtechnologies.server.service.impl.o2;

import java.io.File;

public class KeystoreUtils {

    public static void initKeystore() {
        System.setProperty("javax.net.ssl.keyStore", new File("service/src/main/resources/META-INF/o2_soa_keystore.jks").getAbsolutePath());
        System.setProperty("javax.net.ssl.keyStorePassword", "Fb320p");
    }
}
