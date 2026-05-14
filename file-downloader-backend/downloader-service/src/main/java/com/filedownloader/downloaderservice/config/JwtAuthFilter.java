package com.filedownloader.downloaderservice.config;

import com.filedownloader.downloaderservice.model.dto.UserContextDTO;
import com.filedownloader.downloaderservice.model.enums.UserRole;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

                UserContextDTO userContext = extractUserContext(claimsSet);

                List<SimpleGrantedAuthority> authorities = extractAuthorities(claimsSet);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userContext, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private UserContextDTO extractUserContext(JWTClaimsSet claimsSet) throws java.text.ParseException {
        String id = claimsSet.getSubject();

        String fullName = claimsSet.getStringClaim("name");
        if (fullName == null) {
            String givenName = claimsSet.getStringClaim("given_name");
            String familyName = claimsSet.getStringClaim("family_name");
            if (givenName != null && familyName != null) {
                fullName = givenName + " " + familyName;
            } else if (givenName != null) {
                fullName = givenName;
            } else if (familyName != null) {
                fullName = familyName;
            } else {
                fullName = claimsSet.getStringClaim("preferred_username");
            }
        }

        String email = claimsSet.getStringClaim("email");

        Set<UserRole> roles = extractRoles(claimsSet);

        return UserContextDTO.builder()
                .id(id)
                .fullName(fullName)
                .email(email)
                .roles(roles)
                .build();
    }

    @SuppressWarnings("unchecked")
    private Set<UserRole> extractRoles(JWTClaimsSet claimsSet) {
        Set<UserRole> roles = new HashSet<>();
        try {
            var realmAccess = claimsSet.getJSONObjectClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roleStrings = (List<String>) realmAccess.get("roles");
                for (String roleStr : roleStrings) {
                    String enumName = roleStr.toUpperCase(Locale.ROOT).replace("-", "_");
                    try {
                        UserRole role = UserRole.valueOf(enumName);
                        roles.add(role);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return roles;
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> extractAuthorities(JWTClaimsSet claimsSet) {
        try {
            var realmAccess = claimsSet.getJSONObjectClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roles = (List<String>) realmAccess.get("roles");
                return roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
        } catch (Exception ignored) {
        }
        return List.of();
    }
}
