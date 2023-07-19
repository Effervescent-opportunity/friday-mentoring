package com.friday.mentoring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/auth/login1").permitAll()
                                .requestMatchers("/auth/**").authenticated()
                                .requestMatchers("/time/**").hasAuthority("ADMIN"))
                .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
                        .alwaysRemember(true).key("clock"))//todo wtf? how to send cookie? why curl does't remember it?
                .exceptionHandling(authorizeHttpRequests -> authorizeHttpRequests
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

        return NoOpPasswordEncoder.getInstance();
//        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {//todo passport encoding
        // PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //  // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
        //  // remember the password that is printed out and use in the next step
        //  System.out.println(encoder.encode("password"));
        //
        //    UserDetails user = User.withUsername("user")
        //      .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
        //      .roles("USER")
        //      .build();

        UserDetails rootUser = User.withUsername("root")
                .password("password")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(rootUser);
    }

//    @Autowired todo wtf
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("user1")
//                .password(passwordEncoder().encode("user1Pass"))
//                .authorities("ROLE_USER");
//    }

//  todo wtf
//   @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/securityNone")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .httpBasic()
//                .authenticationEntryPoint(authenticationEntryPoint);
//        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
//        return http.build();
//    }


}
