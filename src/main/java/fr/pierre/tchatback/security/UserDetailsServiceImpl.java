package fr.pierre.tchatback.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pierre.tchatback.entity.User;
import fr.pierre.tchatback.service.UserService;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
        User user = userService.getByProviderId(providerId).orElseThrow(()-> new UsernameNotFoundException("providerId not found"));
        return UserPrincipalFactory.build(user);
    }
}
