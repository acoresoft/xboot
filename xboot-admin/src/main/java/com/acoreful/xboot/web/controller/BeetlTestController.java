package com.acoreful.xboot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("beetltest")
public class BeetlTestController {
	@GetMapping
	public String index() {
		return "beetltest.btl";
	}
}
