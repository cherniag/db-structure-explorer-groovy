package utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
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

    public static void main(String[] args) {
        try {
            String username = CharMatcher.isNot('\'').retainFrom(args[0]);
            String userToken = CharMatcher.isNot('\'').retainFrom(args[1]);
            String expiredMillis = getExpiredMillis(args);
            String signature = makeTokenSignature(username, Long.parseLong(expiredMillis), userToken);

            String rememberMeToken = encodeCookie(new String[]{username, expiredMillis, signature});
            System.out.println("_REMEMBER_ME="+rememberMeToken);
        } catch (Exception e) {
            System.out.println("args: "+arrayToString(args));
            System.out.println(help);
        }
    }

    private static String arrayToString(String[] args) {
        return Joiner.on(", ").join(args).toString();
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
}
