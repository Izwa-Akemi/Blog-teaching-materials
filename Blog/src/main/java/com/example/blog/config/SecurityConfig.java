package com.example.blog.config;

import com.example.blog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /* ==============================
       ★ ここから追加する（1〜2）
       ============================== */

    @Bean
    public SecurityContextRepository adminContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("ADMIN_SECURITY_CONTEXT");
        return repo;
    }

    @Bean
    public SecurityContextRepository userContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("USER_SECURITY_CONTEXT");
        return repo;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ==============================
       ★ 管理者セキュリティ（@Order1）
       ============================== */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {

        http.securityMatcher("/admin/**")
            .securityContext(ctx -> ctx
                .securityContextRepository(adminContextRepository())   // ★ 追加
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/admin/setup/**").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(login -> login
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .usernameParameter("email")
                .passwordParameter("password")
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }


    /* ==============================
       ★ 一般ユーザーセキュリティ（@Order2）
       ============================== */
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurity(HttpSecurity http) throws Exception {

        http.securityContext(ctx -> ctx
                .securityContextRepository(userContextRepository())   // ★ 追加
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/signup", "/login", "/blog/**",
                                 "/posts/**", "/uploads/**")
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/blog", true)
                .usernameParameter("email")
                .passwordParameter("password")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/blog")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

}

