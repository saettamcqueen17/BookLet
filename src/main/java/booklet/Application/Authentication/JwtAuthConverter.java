package booklet.Application.Authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Set<String> clientIds;
    private final boolean includeScopes;


    public JwtAuthConverter(Collection<String> clientIds, boolean includeScopes) {
        this.clientIds = clientIds == null ? Set.of() : new HashSet<>(clientIds);
        this.includeScopes = includeScopes;
    }


    public JwtAuthConverter(String clientId) {
        this(clientId == null ? Set.of() : Set.of(clientId), true);
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        // usa preferred_username come nome utente “leggibile”
        String principalName = Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
                .orElse(jwt.getSubject());
        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

        Set<String> roles = new HashSet<>();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object rr = realmAccess.get("roles");
            if (rr instanceof Collection<?> coll) {
                coll.forEach(r -> roles.add(String.valueOf(r)));
            }
        }


        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && !clientIds.isEmpty()) {
            for (String clientId : clientIds) {
                Object clientObj = resourceAccess.get(clientId);
                if (clientObj instanceof Map<?, ?> m) {
                    Object clientRoles = m.get("roles");
                    if (clientRoles instanceof Collection<?> coll) {
                        coll.forEach(r -> roles.add(String.valueOf(r)));
                    }
                }
            }
        }


        Stream<GrantedAuthority> scopeAuth = Stream.empty();
        if (includeScopes) {
            String scope = jwt.getClaimAsString("scope");
            if (scope != null && !scope.isBlank()) {
                scopeAuth = Arrays.stream(scope.split("\\s+"))
                        .filter(s -> !s.isBlank())
                        .map(s -> new SimpleGrantedAuthority("SCOPE_" + s));
            }
        }


        Stream<GrantedAuthority> roleAuth = roles.stream()
                .filter(r -> r != null && !r.isBlank())
                .map(String::trim)
                .map(String::toUpperCase)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r));


        return Stream.concat(roleAuth, scopeAuth).collect(Collectors.toSet());
    }
}