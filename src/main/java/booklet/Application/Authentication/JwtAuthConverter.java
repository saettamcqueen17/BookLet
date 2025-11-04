package booklet.Application.Authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // 1️⃣ Scopes / authorities di base
        authorities.addAll(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream()
                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toSet())
        );

        // 2️⃣ Ruoli del Realm
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?>) {
            ((Collection<?>) realmAccess.get("roles")).forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
            );
        }

        // 3️⃣ Ruoli del client backend
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.get("booklet-client_backend") instanceof Map<?, ?> clientAccess) {
            Object rolesObj = ((Map<?, ?>) clientAccess).get("roles");
            if (rolesObj instanceof Collection<?>) {
                ((Collection<?>) rolesObj).forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                );
            }
        }

        // 4️⃣ Token con authorities effettive
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
