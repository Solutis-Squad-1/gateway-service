package br.com.solutis.squad1.gateway.service;

import br.com.solutis.squad1.gateway.dto.UserDetailsDto;
import br.com.solutis.squad1.gateway.exception.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Value("${api.security.token.jwt.secret}")
    private String secret;

    @Value("${api.security.provider}")
    private String provider;

    /**
     * Verifies if the token is valid
     *
     * @param token
     */
    public void verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                    .withIssuer(provider)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    /**
     * Gets the user details from the token
     *
     * @param token
     * @return UserDetailsDto
     */
    public UserDetailsDto getUserDetails(String token) {
        DecodedJWT jwt = JWT.decode(token);

        String username = jwt.getSubject();
        String authorities = jwt.getClaim("authorities").asString();

        return new UserDetailsDto(username, authorities);
    }
}
