package com.hanghae99.sulmocco.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hanghae99.sulmocco.dto.token.TokenDto;

import java.util.Date;

public final class JwtTokenUtils {

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 토큰의 유효기간: 3일 (단위: seconds)
    private static final int JWT_TOKEN_VALID_SEC = 3 * HOUR;
    // JWT 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;
//    private static final int JWT_TOKEN_VALID_MILLI_SEC = DAY ;

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 *     7;  // 7일
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 2 * DAY;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_USER_NAME = "USER_NAME";
    public static final String CLAIM_NICK_NAME = "NICK_NAME";
    public static final String JWT_SECRET = "${{ secrets.jwt_secret }}";

    public static TokenDto generateJwtAndRefreshToken(String loginId, String nickname) {
        String accessToken = null;
        try {
            accessToken = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_NAME, loginId)  // 사용자 로그인 ID
                    .withClaim(CLAIM_NICK_NAME, nickname)  // 사용자 닉네임
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String refreshToken = null;
        Date refreshTokenExpiresIn = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME);
        try {
            refreshToken = JWT.create()
                    .withIssuer("spartaRefresh")
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, refreshTokenExpiresIn)
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        TokenDto tokenDto = TokenDto.builder()
                .grantType("BEARER")
                .accessToken("BEARER" + " " + accessToken)
                .status(true)
                .message("리프레쉬 로그인 성공")
                .accessTokenExpiresIn(refreshTokenExpiresIn)
                .refreshToken(refreshToken)
                .build();

        return tokenDto;
    }

    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
