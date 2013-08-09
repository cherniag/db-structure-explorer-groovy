package util;

import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.DigestUtils;

public class RememberMegenerator {
    static long TWO_WEEKS = 14L * 24L * 60L * 60L * 1000L;
    static String help = "Help:\n" +
            "Possible arguments:\n" +
            "\t\t[tb_users.userName] [tb_users.token] \n" +
            "\t\tor\n" +
            "\t\t[tb_users.userName] [tb_users.token] [expiredMillies]\n\n" +
            "\t\t by default expiredMillies = NOW() + TWO_WEEKS in milliseconds\n\n";
	private static String iphoneAgent="Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

    public static void main(String[] args) {
        try {
//            String username = "+447544259145";//CharMatcher.isNot('\'').retainFrom(args[0]);
            String username = "+447731293075";//CharMatcher.isNot('\'').retainFrom(args[0]);
//            String userToken = "de8e7cf99db049013c6b6ae770aeeb71";//CharMatcher.isNot('\'').retainFrom(args[1]);
            String userToken = "530cfe53f868ec33eab25e34d7fa4015";//CharMatcher.isNot('\'').retainFrom(args[1]);
            String expiredMillis = getExpiredMillis(args);
            String signature = makeTokenSignature(username, Long.parseLong(expiredMillis), userToken);

            String rememberMeToken = encodeCookie(new String[]{username, expiredMillis, signature});
            System.out.println("_REMEMBER_ME="+rememberMeToken);
            
        } catch (Exception e) {
        	e.printStackTrace();
            //System.out.println("args: "+arrayToString(args));
            System.out.println(help);
        }
    }


    private static String getExpiredMillis(String[] args) {
        String expiredMillis = Long.toString(System.currentTimeMillis() + TWO_WEEKS);
        if (args.length == 3)
            expiredMillis = args[2];
        return expiredMillis;
    }

    protected static String encodeCookie(String[] cookieTokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cookieTokens.length; i++) {
            sb.append(cookieTokens[i]);

            if (i < cookieTokens.length - 1) {
                sb.append(":");
            }
        }

        String value = sb.toString();

        sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

        while (sb.charAt(sb.length() - 1) == '=') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public static String makeTokenSignature(String username, long tokenExpiryTime, String password) {
        return DigestUtils.md5DigestAsHex((username + ":" + tokenExpiryTime + ":" + password + ":" + "web").getBytes());
    }
    
    public static String getRememberMeCookie(String username, String userToken) {
    	long expireTime = System.currentTimeMillis()+TimeUnit.DAYS.toMillis(30);
    	String signature = makeTokenSignature(username, expireTime, userToken);

        return encodeCookie(new String[]{username, Long.toString(expireTime), signature});
    }
}
