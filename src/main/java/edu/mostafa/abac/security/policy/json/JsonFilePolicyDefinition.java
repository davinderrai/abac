package edu.mostafa.abac.security.policy.json;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import edu.mostafa.abac.DemoApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import edu.mostafa.abac.security.policy.PolicyDefinition;
import edu.mostafa.abac.security.policy.PolicyRule;

@Component("jsonFilePolicyDefinition")
public class JsonFilePolicyDefinition implements PolicyDefinition {
	private static Logger logger = LoggerFactory.getLogger(JsonFilePolicyDefinition.class);
	
	private static String DEFAULT_POLICY_FILE_NAME = "custom.json";
	private String policyFilePath;

	private List<PolicyRule> rules;
	
	@PostConstruct
	private void init(){

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Expression.class, new SpelDeserializer());
		mapper.registerModule(module);
		try {
			PolicyRule[] rulesArray = null;
			logger.debug("[init] Checking policy file at: {}", policyFilePath);
					logger.info("[init] Custom policy file not found. Loading default policy");
				rulesArray = mapper.readValue(getFileFromResources(DEFAULT_POLICY_FILE_NAME), PolicyRule[].class);
			this.rules = (rulesArray != null? Arrays.asList(rulesArray) : null);
			logger.info("[init] Policy loaded successfully.");
		} catch (JsonMappingException e) {
			logger.error("An error occurred while parsing the policy file.", e);
		} catch (IOException e) {
			logger.error("An error occurred while reading the policy file.", e);
		}
	}

	// get file from classpath, resources folder
	public File getFileFromResources(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}
	
	@Override
	public List<PolicyRule> getAllPolicyRules() {
		return rules;
	}
}
