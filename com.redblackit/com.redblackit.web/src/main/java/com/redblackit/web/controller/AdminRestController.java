/*
 * Copyright 2002-2011 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.redblackit.version.VersionInfo;

/**
 * @author djnorth
 *
 * MVC controller for admin and status REST requests
 */
@Controller
@RequestMapping("/rest")
public class AdminRestController {

	/**
	 * Version object
	 */
	private VersionInfo versionInfo;
	
	/**
	 * Constructor taking version info
	 * 
	 * @param versionInfo
	 */
	@Autowired
	public AdminRestController(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}
	
	/**
	 * Handle about request (designed for robots)
	 * 
	 * @return version string
	 */
	@RequestMapping(value="/version/summary", method=RequestMethod.GET)
	public @ResponseBody String getVersionSummary()
	{
		return versionInfo.getVersionString();
	}
	
	/**
	 * Handle about request (designed for robots)
	 * 
	 * @return object
	 */
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public @ResponseBody VersionInfo getVersion()
	{
		return versionInfo;
	}
	
	/**
	 * Handle about head request (designed for robots)
	 * 
	 * @return object
	 */
	@RequestMapping(value="/version", method=RequestMethod.HEAD)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void getVersionHead()
	{
		// That's it folks
	}

}
