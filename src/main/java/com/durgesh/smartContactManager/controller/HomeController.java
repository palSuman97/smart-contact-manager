package com.durgesh.smartContactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.durgesh.smartContactManager.dao.UserRepository;
import com.durgesh.smartContactManager.entity.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller()
public class HomeController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Smart Contact Manager Signup Page");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult br, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		
		System.out.println("===info==\n"+user+"\n"+br+"\n"+model+"\n"+session+"\n"+redirectAttributes);
		
		try {
		
		if(br.hasErrors()) {
			model.addAttribute("user",user);
			System.out.println("br"+ br.toString());
			return "signup";
		}
			
		if(!agreement) {
			throw new Exception();
		}
			
		model.addAttribute("user", new User());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		System.out.println(user);
		return "signup";
		}
		catch (Exception e) {
			model.addAttribute("user", user);
			return "signup";
		}
	}
	
	@RequestMapping("/signin")
	public String signIn() {
		return "signin";
	}
}
