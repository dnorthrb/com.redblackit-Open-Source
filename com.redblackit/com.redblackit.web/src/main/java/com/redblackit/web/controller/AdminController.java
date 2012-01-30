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

import com.redblackit.version.CompositeVersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redblackit.version.VersionInfo;

/**
 * @author djnorth
 *
 * MVC controller for admin and status requests
 */
@Controller
public class AdminController {

	/**
	 * Version object
	 */
	private CompositeVersionInfo versionInfo;
	
	/**
	 * Constructor taking version info
	 * 
	 * @param versionInfo
	 */
	@Autowired
	public AdminController(CompositeVersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}
	
	/**
	 * Handle about request (designed for humans)
	 * 
	 * @param model to set attribute for view
	 */
	@RequestMapping("/about")
	public void about(Model model)
	{
		model.addAttribute("versionInfo", versionInfo);
	}

}
