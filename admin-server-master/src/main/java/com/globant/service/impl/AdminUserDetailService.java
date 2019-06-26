/**
 * 
 */
package com.globant.service.impl;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author mangesh.pendhare
 *
 */
@Service
public class AdminUserDetailService implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;

	public AdminUserDetailService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if ("admin".equals(username)) {
			return new User(username, passwordEncoder.encode("password"), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with login: " + username);
		}
	}
}
