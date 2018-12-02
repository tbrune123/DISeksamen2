package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;

public final class Token {


    private static Date expirationDate() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.MINUTE, 10);
        Date expirationDate = c.getTime();

        return expirationDate;
    }


    public static String CreateToken() {


        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);


            return token;
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }

        return null;


    }

    public static DecodedJWT verifyToken (String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .acceptExpiresAt(expirationDate().getTime())
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        } catch (JWTVerificationException exception){

        } return null;
    }

}


