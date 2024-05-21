package us.puter.park.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonVariables {

    // spring
    public static String profile;
    // system
    public static String secretKey;
    public static long accessTokenValidSecond;
    public static long refreshTokenValidSecond;
    public static String title;
    public static int qrCodeSize;
    public static int shortenUriLength;

    @Value("${spring.profiles.active}")
    private void setProfile(String profile) {
        CommonVariables.profile = profile;
    }

    @Value("${system.secret-key}")
    private void setSecretKey(String secretKey) {
        CommonVariables.secretKey = secretKey;
    }

    @Value("${jwt.access-token-valid-second}")
    private void setAccessTokenValidSecond(long accessTokenValidSecond) {
        CommonVariables.accessTokenValidSecond = accessTokenValidSecond;
    }

    @Value("${jwt.refresh-token-valid-second}")
    private void setRefreshTokenValidSecond(long refreshTokenValidSecond) {
        CommonVariables.refreshTokenValidSecond = refreshTokenValidSecond;
    }

    @Value("${system.title}")
    private void setTitle(String title) {
        CommonVariables.title = title;
    }

    @Value("${system.qr-code-size}")
    private void setQrCodeSize(int qrCodeSize) {
        CommonVariables.qrCodeSize = qrCodeSize;
    }

    @Value("${system.shorten-uri.length}")
    private void setShortenUriLength(int shortenUriLength) {
        CommonVariables.shortenUriLength = shortenUriLength;
    }
}
