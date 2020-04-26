package edu.mostafa.abac.security.spring;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mostafa.abac.web.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import edu.mostafa.abac.security.policy.PolicyEnforcement;
import org.springframework.web.context.request.WebRequest;

/**
 * Use this class in any of the Spring Beans to evaluate security policy.
 * @author <a href="mailto:mostafa.mahmoud.eltaher@gmail.com">Mostafa Eltaher</a>
 *
 */
@Component
public class ContextAwarePolicyEnforcement {
	@Autowired
	protected PolicyEnforcement policy;

	private UserInfo userInfo = new UserInfo();

	public void setContext(WebRequest webRequest) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();

			UserInfo user = objectMapper.readValue(webRequest.getHeader("User-Info"), UserInfo.class);
			userInfo.setName(user.getName());
			userInfo.setRole(user.getRole());
		} catch (JsonProcessingException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void checkPermission(Object resource, String permission) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Map<String, Object> environment = new HashMap<>();
		
		Object authDetails = auth.getDetails();
		if(authDetails != null) {
			if(authDetails instanceof WebAuthenticationDetails) {
				environment.put("remoteAddress", ((WebAuthenticationDetails) authDetails).getRemoteAddress());
			}
		}

		environment.put("time", new Date());

		if (!policy.check(userInfo, resource, permission, environment))
			throw new AccessDeniedException("Access is denied");
	}
}
