package com.sliit.safelocker.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class JwtTokenUtil {

    private final static int RESPONSE_MAX_LENGTH = 1000;

    private final static int REQUEST_MAX_LENGTH = 700;
    private final static  String SECRET = "secret";
    private final static  int ACCESS_TOKEN_EXPIRES = 1440; // mintues
    private final static  int REFRESH_TOKEN_EXPIRES = 34560; // mintues
    public final static  String  ACCESS = "accessToken";
    public final static  String  REFRESH = "refreshToken";

    public static  String createJwtToken(String username , String role, String issuer, String tokenType) {

        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

        if (tokenType == ACCESS) {


            return JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES * 60 * 1000))
                    .withIssuer(issuer)
                    .withClaim("role", role)
                    .withClaim("type", tokenType)
                    .sign(algorithm);

        } else {

            return JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES * 60 * 1000))
                    .withIssuer(issuer)
                    .withClaim("type", tokenType)
                    .sign(algorithm);

        }
    }


    public static String getUsernameByJwtToken(String jwtToken) {

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(jwtToken);
        String username = decodedJWT.getSubject();
        return username;
    }
    public static boolean isTokenExpired(String token) {
        log.info("check token for expire");
        DecodedJWT jwt = JWT.decode(token);
        return  jwt.getExpiresAt().before(new Date());

    }

    public static Map<String,String> truncateRequestAndResponse(String stringRequest,String stringResponse) {
        Map<String, String> result = new HashMap<>();


            if (stringResponse.length() > RESPONSE_MAX_LENGTH) {
                String limitResponse = stringResponse.substring(0, 1000);
                result.put("limitResponse", limitResponse);
            } else {
                result.put("stringResponse", stringResponse);
            }
            if (stringRequest.length() > REQUEST_MAX_LENGTH) {
                String limitRequest = stringRequest.substring(0, 700);
                result.put("limitRequest", limitRequest);
            } else {
                result.put("stringRequest", stringRequest);
            }
        return result;


    }

}
