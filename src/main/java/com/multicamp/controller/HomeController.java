package com.multicamp.controller;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.multicamp.mapper.SampleMapper;

@Controller
public class HomeController {
	
	@Inject
	private SampleMapper sMapper;
	
	
	@GetMapping("/home")
	public String showHome(Model m) {		
		Date today=new Date();
		int cnt=sMapper.getTableCount();
		
		m.addAttribute("today", today.toString());
		m.addAttribute("tableCount", cnt);
		
		return "home";
		//WEB-INF/views/home.jsp
	}

}
