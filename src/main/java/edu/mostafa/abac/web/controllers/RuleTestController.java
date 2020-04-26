package edu.mostafa.abac.web.controllers;

import edu.mostafa.abac.security.spring.ContextAwarePolicyEnforcement;
import edu.mostafa.abac.web.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/sample-issue-tracker/projects")
public class RuleTestController {

    private static final Logger logger = LoggerFactory.getLogger(RuleTestController.class);

    @Autowired
    private ContextAwarePolicyEnforcement policy;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/abc/{task}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public void listRules(@PathVariable String task, WebRequest webRequest) {

        logger.info("Task "+task);
        policy.setContext(webRequest);
        policy.checkPermission(null, task);
        logger.info("ssssss");
    }
}
