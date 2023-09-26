package com.durgesh.smartContactManager.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

	UserDetailsManager theUserDetailsManager;
	
	@Bean
	UserDetailsManager userDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager theUserDetailsManager= new JdbcUserDetailsManager(dataSource);
		
		theUserDetailsManager.setUsersByUsernameQuery("select name, password, enabled from user where name=?");
		theUserDetailsManager.setAuthoritiesByUsernameQuery("select name, role user from user where name=?");
		this.theUserDetailsManager=theUserDetailsManager;
		return theUserDetailsManager;
	}
	
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	DaoAuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//		daoAuthenticationProvider.setUserDetailsService(this.theUserDetailsManager);
//		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//		
//		return daoAuthenticationProvider;
//	}
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	http.authorizeHttpRequests(configurer -> configurer
    			.requestMatchers("/home").permitAll()
    			.anyRequest().authenticated())
    			.formLogin(form -> form.loginPage("/signin")
    					.loginProcessingUrl("/authenticateTheUser").permitAll().defaultSuccessUrl("/user/dashboard"))
    			.logout(logout -> logout.permitAll());
    	
    	return http.build();
    }
}
