/**
 * 
 */
package com.globant.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.model.AdminUserCredentials;
import com.globant.service.impl.AmazonS3ClientServiceImpl;

/**
 * @author mangesh.pendhare
 *
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);

	private static final String ERROR_MESSAGE = "Something went wrong while parsing /admin/login request body";

	private final ObjectMapper objectMapper = new ObjectMapper();

	public CustomAuthenticationFilter() {
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String requestBody;
		try {
			requestBody = IOUtils.toString(request.getReader());
			AdminUserCredentials creds = objectMapper.readValue(requestBody, AdminUserCredentials.class);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword());

			setDetails(request, token);

			if (!request.getMethod().equals("OPTIONS")) {
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
				response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
				response.setHeader("Access-Control-Allow-Headers", "*");
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setHeader("Access-Control-Max-Age", "180");
			}

			if (request.getMethod().equals("OPTIONS")) {
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				// return null;
			}

			return this.getAuthenticationManager().authenticate(token);

		} catch (IOException e) {
			LOGGER.info(ERROR_MESSAGE);
			throw new AuthenticationServiceException(ERROR_MESSAGE, e);
		}
	}
}