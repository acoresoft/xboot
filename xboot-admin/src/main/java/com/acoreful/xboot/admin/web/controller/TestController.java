package com.acoreful.xboot.admin.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping({"","/"})
	public String home() {
		return "Hello World!";
	}
}
