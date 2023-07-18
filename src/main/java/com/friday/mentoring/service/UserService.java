//package com.friday.mentoring.service;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Collections;
//todo delete - unnecessary
//public class UserService implements UserDetailsService {
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        if ("root".equals(username)) {
//            return new User("root", "pass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
//        }
//        throw new UsernameNotFoundException("No user found with username: " + username);
//    }
//}
