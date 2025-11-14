package booklet.Application.Authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String CLIENT_ID = "booklet-client_backend"; //
    private static final String ROLES = "roles";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 🔹 Realm roles
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            roles.addAll((Collection<String>) realmAccess.get(ROLES));
        }

        // 🔹 Client roles
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess != null && resourceAccess.containsKey(CLIENT_ID)) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
            if (clientAccess.containsKey(ROLES)) {
                roles.addAll((Collection<String>) clientAccess.get(ROLES));
            }
        }

        // 🔹 Conversione finale
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());

        // 🔹 Crea il token autenticato
        return new JwtAuthenticationToken(jwt, authorities, getUsername(jwt));
    }

    private String getUsername(Jwt jwt) {
        // Puoi usare "preferred_username" o "sub" o "email" in base al tuo token Keycloak
        return jwt.getSubject();
    }
}
