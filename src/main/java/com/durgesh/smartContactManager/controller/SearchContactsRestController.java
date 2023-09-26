package com.durgesh.smartContactManager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.durgesh.smartContactManager.dao.ContactRepository;
import com.durgesh.smartContactManager.dao.UserRepository;
import com.durgesh.smartContactManager.entity.Contact;
import com.durgesh.smartContactManager.entity.User;

@RestController
public class SearchContactsRestController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/searchContacts/{query}")
	public ResponseEntity<?> searchContacts(@PathVariable("query") String query, Principal principal){
		
		User user = userRepository.getUserByName(principal.getName());
		
		List<Contact> contacts= contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
}
