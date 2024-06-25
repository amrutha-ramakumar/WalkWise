package com.ecommerce.customer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {
    @Autowired
    CustomSuccessHandler customSuccessHandler;



    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests(configurer->
                        configurer
                                .requestMatchers("/shop/oauth2/authorization/google").permitAll()
                                .requestMatchers("/css/**", "/imgs/**", "/js/**", "/fonts/**", "/sass/**", "/**","/address","/login","/forgot-Password","/forgot_password").permitAll()
                                .requestMatchers("/shop/**").hasAuthority("CUSTOMER")
                                .anyRequest().authenticated()
                )

                .formLogin(form->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/home")
                                .successHandler(customSuccessHandler)
                                .permitAll()

                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .successHandler(customSuccessHandler)
                        .defaultSuccessUrl("/home",true)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)

                )
                .logout(LogoutConfigurer->
                                LogoutConfigurer
                                        .invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                        .logoutSuccessUrl("/login?logout")
                                        .permitAll()
//                                .logoutSuccessUrl("/login")

                )
                .csrf(AbstractHttpConfigurer::disable);



        return http.build();
    }


}











//package com.ecommerce.customer.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class CustomerConfiguration {
//    @Autowired
//    CustomSuccessHandler customSuccessHandler;
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//
//        http.authorizeHttpRequests(configurer->
//                        configurer
//                                .requestMatchers("/shop/oauth2/authorization/google").permitAll()
//                                .requestMatchers("/css/**", "/imgs/**", "/js/**", "/fonts/**", "/sass/**", "/**","/address","/login","/forgot-Password","/forgot_password").permitAll()
//                                .requestMatchers("/shop/**").hasAuthority("CUSTOMER")
//                                .anyRequest().authenticated()
//                )
//
//                       .formLogin(form->
//                        form
//                                .loginPage("/login")
//                                .loginProcessingUrl("/authenticateTheUser")
//                                .defaultSuccessUrl("/home")
//                                .successHandler(customSuccessHandler)
//                                .permitAll()
//
//                )
//                .oauth2Login(oauth2Login ->
//                        oauth2Login
//                                .loginPage("/login")  // Use the same login page for both methods
//                                .defaultSuccessUrl("/home")  // Redirect to home after successful login
//                                .userInfoEndpoint(userInfoEndpoint ->
//                                        userInfoEndpoint.oidcUserService(oidcUserService())
//                                )
//                )
////                .oauth2Login(oauth2Login -> oauth2Login
////                        .loginPage("/login")
////                        .successHandler(customSuccessHandler)
////                        .defaultSuccessUrl("/home",true)
////                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                        .invalidSessionUrl("/")
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(false)
//
//                )
//                .logout(LogoutConfigurer->
//                        LogoutConfigurer
//                                .invalidateHttpSession(true)
//                                .clearAuthentication(true)
//                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                .logoutSuccessUrl("/login?logout")
//                                .permitAll()
////                                .logoutSuccessUrl("/login")
//
//                )
//                .csrf(AbstractHttpConfigurer::disable);
//
//
//
//        return http.build();
//    }
//    @Bean
//    public OidcUserService oidcUserService() {
//        return new OidcUserService();
//    }
//
//}
