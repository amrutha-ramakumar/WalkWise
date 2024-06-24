package com.ecommerce.admin.config;
// Import statements
import com.ecommerce.library.repository.AdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class AdminConfiguration {

    private final AdminRepository adminRepository;

    public AdminConfiguration(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Bean to provide user details service
    @Bean
    public UserDetailsService userDetailsService(){
        return new AdminServiceConfig(adminRepository);
    }

    // Bean to provide password encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Obtain AuthenticationManagerBuilder from HttpSecurity
        AuthenticationManagerBuilder authenticationManagerBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Configure authentication manager with user details service and password encoder
        authenticationManagerBuilder
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());

        // Build AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // HTTP security configuration
        http
                // Disable CSRF protection
                .csrf(AbstractHttpConfigurer::disable)
                // Configure authorization rules
                .authorizeHttpRequests(author ->
                        author
                                // Permit access to static resources
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                // Permit access to specific resource paths
                                .requestMatchers("/css/**","/dist/**","/img/**","/js/**","/less/**","/pages/**","/scss/**","/vendor/**").permitAll()
                                // Require ADMIN authority for paths under "/admin"
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                // Permit access to registration endpoints
                                .requestMatchers("/register", "/register-new").permitAll()
                                // Require authentication for any other requests
                                .anyRequest().authenticated()
                )
                // Configure form login
                .formLogin(login ->
                        login
                                .loginPage("/login")
                                .loginProcessingUrl("/do-login")
                                .defaultSuccessUrl("/index", true)
                                .permitAll()
                )
                // Configure logout
                .logout(logout ->
                        logout
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )
                // Set authentication manager
                .authenticationManager(authenticationManager)
                // Configure session management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                );

        // Build and return the SecurityFilterChain
        return http.build();
    }
}
