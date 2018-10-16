package com.java.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CacheDemoController {

	@RequestMapping("/caching")
	public String data(HttpServletResponse response, Model model) {
		System.out.println("In controller");
		model.addAttribute("message", "From caching controller");
		return "data";
	}
}
