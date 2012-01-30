/*
 * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redblackit.web.controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author djnorth
 * 
 * MVC controller handling security specific URLs (where necessary)
 *
 */
@Controller
public class SecurityController {
	
	/**
	 * Logger
	 */
	Logger logger = Logger.getLogger("security");
	
	
	/**
	 * Authentication error handling.
	 * 
	 * For now we log, then fire up the login page with an appropriate message parameter
	 * 
	 * @param model
	 */
	@RequestMapping("/loginError")
	@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
	public String loginError(Model model)
	{
		logger.warn("login error");
		model.addAttribute("login_error", "login.unsuccessful");
		return "login";
	}
	
	
	/**
	 * Access denied handling.
	 * 
	 * For now we log, then fire up the login page with an appropriate message parameter
	 * 
	 * @param principal
	 * @param model
	 */
	@RequestMapping("/accessDenied")
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	public String accessDenied(Principal principal, Model model)
	{
		logger.error("access denied to " + principal.getName());
		model.addAttribute("login_error", "login.access.denied");
		return "login";
	}

}
