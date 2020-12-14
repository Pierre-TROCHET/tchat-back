package fr.pierre.tchatback.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import fr.pierre.tchatback.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipalFactory {

    public static UserPrincipal build(User user){
        List<GrantedAuthority> authorities =
        		user.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.getRoleName().name())).collect(Collectors.toList());
        return new UserPrincipal(user.getProviderId(), user.getPassword(), authorities);
    }
}