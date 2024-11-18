package com.Fern.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	public CustomAuthSucessHandler sucessHandler;
	
	@Autowired
	public CustomFailureHandler failureHandler;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService getDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
//	{
//		http.csrf().disable()
//		.authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
//		.requestMatchers("/admin/**").hasRole("ADMIN")
//		.requestMatchers("/**").permitAll().and()
//		.formLogin().loginPage("/signin").loginProcessingUrl("/userLogin")
//		.failureHandler(failureHandler)
//		.successHandler(sucessHandler)
//		.permitAll();
//
//		return http.build();
//
//	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/employee/**").hasRole("EMPLOYEE")
						.requestMatchers("/**").permitAll()
				)
				.formLogin((form) -> form
						.loginPage("/signin")
						.loginProcessingUrl("/userLogin")
						.failureHandler(failureHandler)
						.successHandler(sucessHandler)
						.permitAll()
				)
				.logout((logout) -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/signin?logout")
						.permitAll()
				)
				.sessionManagement((session) -> session
						.maximumSessions(1)
						.expiredUrl("/signin?expired=true")
				);

		return http.build();
	}


}
