package codes.rootheart.sudoku.security;

import java.util.concurrent.TimeUnit;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(7);
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SIGN_UP_URL = "/user/sign-up";

}
