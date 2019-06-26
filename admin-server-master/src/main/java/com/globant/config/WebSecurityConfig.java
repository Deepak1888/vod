/**
 * 
 */
package com.globant.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.filter.CustomAuthenticationFilter;
import com.globant.model.CommonResponse;
import com.globant.model.LoginResponse;
import com.globant.service.impl.AdminUserDetailService;

/**
 * @author mangesh.pendhare
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final AdminUserDetailService userService;
	private final ObjectMapper objectMapper;
	private final PasswordEncoder passwordEncoder;

	public WebSecurityConfig(AdminUserDetailService userService, ObjectMapper objectMapper,
			PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.objectMapper = objectMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		builder.authenticationProvider(authProvider());
	}

	@Bean
	public CustomAuthenticationFilter authenticationFilter() throws Exception {
		CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(this::loginSuccessHandler);
		authenticationFilter.setAuthenticationFailureHandler(this::loginFailureHandler);
		authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/login", "POST"));
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFilter;
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsFilter filter = new CorsFilter();
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().addFilterBefore(corsFilter(), SessionManagementFilter.class).authorizeRequests()
				.anyRequest().authenticated().and()
				.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class).logout()
				.logoutUrl("/admin/logout").logoutSuccessHandler(this::logoutSuccessHandler).and().exceptionHandling()
				.authenticationEntryPoint(new RestAuthenticationEntryPoint());
	}

	private void loginSuccessHandler(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		objectMapper.writeValue(response.getWriter(), new LoginResponse(LoginResponse.SUCCESS, "Successful Login"));
	}

	private void loginFailureHandler(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		objectMapper.writeValue(response.getWriter(),
				new LoginResponse(LoginResponse.FAILURE, "Username or Password incorrect."));
	}

	private void logoutSuccessHandler(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		objectMapper.writeValue(response.getWriter(),
				new LoginResponse(LoginResponse.SUCCESS, "Successfully logged out."));
	}
}

/**
 * 
 * http
 * .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
 * .formLogin() .successHandler(ajaxSuccessHandler)
 * .failureHandler(ajaxFailureHandler) .loginProcessingUrl("/admin/login")
 * .passwordParameter("password") .usernameParameter("user") .and() .logout()
 * .deleteCookies("JSESSIONID") .invalidateHttpSession(true)
 * .logoutUrl("/admin/logout") .logoutSuccessUrl("/") .and() .csrf().disable()
 * .anonymous().disable() .authorizeRequests()
 * .antMatchers("/admin/login").permitAll()
 * .antMatchers("/admin/*").access("hasRole('ROLE_ADMIN')")
 * 
 **/