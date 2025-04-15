package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/logout", "/register").permitAll()
                .antMatchers("/users/admin").hasRole("ADMIN") // автоматически превращается в ROLE_ADMIN
                .antMatchers("/users/user").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successUserHandler)
                .and()
                .logout().permitAll()    ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}