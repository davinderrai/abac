package edu.mostafa.abac.security.policy;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Component;

@Component
public class BasicPolicyEnforcement implements PolicyEnforcement {
	private static final Logger logger = LoggerFactory.getLogger(BasicPolicyEnforcement.class);
	
	@Autowired
	@Qualifier("jsonFilePolicyDefinition")
	private PolicyDefinition policyDefinition;
	
	/* (non-Javadoc)
	 * @see edu.mostafa.abac.security.policy.PolicyEnforcement#check(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean check(Object subject, Object resource, Object action, Object environment) {
		//Get all policy rules
		List<PolicyRule> allRules = policyDefinition.getAllPolicyRules();
		//Wrap the context
		SecurityAccessContext cxt = new SecurityAccessContext(subject, resource, action, environment);
		//Filter the rules according to context.
		List<PolicyRule> matchedRules = filterRules(allRules, cxt);
		//finally, check if any of the rules are satisfied, otherwise return false.
		return checkRules(matchedRules, cxt);
	}
	
	private List<PolicyRule> filterRules(List<PolicyRule> allRules, SecurityAccessContext cxt) {
		List<PolicyRule> matchedRules = new ArrayList<>();
		for(PolicyRule rule : allRules) {
			try {
				logger.info("");
				logger.info("Rule Target: " + rule.getTarget().getExpressionString().toString());
				logger.info("Rule Condition: " + rule.getCondition().getExpressionString().toString());
				logger.info("CXT: " + cxt.toString());

				if (rule.getTarget().getValue(cxt, Boolean.class)) {
					logger.info("matches ");


					matchedRules.add(rule);
				}
			} catch (SpelEvaluationException spex) {
				logger.info("An error occurred while evaluating PolicyRule.", spex.getMessage());
			}
		}
		return matchedRules;
	}

	private boolean checkRules(List<PolicyRule> matchedRules, SecurityAccessContext cxt) {
		for(PolicyRule rule : matchedRules) {
			try {

				if (rule.getCondition().getValue(cxt, Boolean.class)) {
					return true;
				}
			} catch (SpelEvaluationException spex) {
				logger.info("An error occurred while evaluating PolicyRule.", spex.getMessage());
			}
		}
		return false;
	}
}
