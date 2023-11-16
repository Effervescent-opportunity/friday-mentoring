package com.friday.mentoring.security;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.sql.DataSource;

/**
 * Бины для аутентификации, авторизации, аудита
 */
@Configuration
@EnableMethodSecurity
public class CustomSecurityConfig {
    /*
     * select * from auth_event order by event_time desc;
     *
     * update users set password ='{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG' where username = 'root';
     * update users set password ='$2a$04$h2h/1yUrMk/1s1z8qVLTouoW./Mgd10Yr2.XBGhNDuVRsDCh756km' where username = 'root';
     * JSESSIONID=947FBDC297F3D61AEACC41D3CBD44F7B
     * https://localhost:8443/auth/change-password
     *
     * curl -k -v -X POST https://localhost:8443/auth/change-password -H "Cookie: JSESSIONID=947FBDC297F3D61AEACC41D3CBD44F7B" -H "Content-Type: application/json" -d '{"user": "root", "oldPassword": "password", "newPassword": "password1"}'
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutUrl("/auth/logout"))
//                .passwordManagement(manager -> manager.changePasswordPage("/auth/change-password"))//todo - now it's not working
                .exceptionHandling(authorizeHttpRequests -> authorizeHttpRequests
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.NOT_FOUND)))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);//the lowest strength - по умолчанию 10ка и занимает секунду на проверку, надеюсь с 4 будет поменьше
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
//
////        userBuilder.username()
////"{noop}password todo run and check/ and change only it's password
//        //old
//        UserDetails rootUser = User.withUsername("root")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(rootUser);
//    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }

    /**
     * Включает события AUTHORIZATION_FAILURE
     */
    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }

}
