package com.rouchFirstCode.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String secret_key = "foobar_123456789_foobar_123456789_foobar_123456789";

    public String issueToken(String subject){
       return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String ...scopes){  //ceci c'est pour les claims extra(ceux qu'on avait pas ajouté dans la création du token)
        return issueToken(subject, Map.of("scopes",scopes));
    }

    public String issueToken(String subject, List<String> scopes){  //ceci c'est pour les claims extra(ceux qu'on avait pas ajouté dans la création du token)
        return issueToken(subject, Map.of("scopes",scopes));
    }

    public String issueToken(String subject, Map<String,Object> claims){

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://rouch-app.com")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(                            //lui c'est le temps de régéneration du nouveau token(il va imposer au user de se reconnecter après le délai fixé, ici c'est 15 jours)
                        Date.from(Instant.now()
                                .plus(15, ChronoUnit.DAYS))
                              )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret_key.getBytes()); //le secret utilisé pour la signature est en bytes
    }

    public String getSubject(String token){
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now()); //ici c'est today la variable
        return getClaims(jwt).getExpiration().before(today); //expiré si la date d'expiration est inférieur à la date
                                                   //d'aujoud8 i.e si la date actuelle a dépassé la date d'expiration
    }

    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }


}
