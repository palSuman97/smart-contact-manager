package com.durgesh.smartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.durgesh.smartContactManager.dao.ContactRepository;
import com.durgesh.smartContactManager.dao.UserRepository;
import com.durgesh.smartContactManager.entity.Contact;
import com.durgesh.smartContactManager.entity.User;
import com.durgesh.smartContactManager.helper.Message;

@Controller()
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactRepository contactRepository;
	
	@ModelAttribute
	public void addUserToModel(Model model, Principal principal) {
		System.out.println("===user== "+ principal.getName());
		User user =  userRepository.getUserByName(principal.getName());
		System.out.println(user);
		model.addAttribute("user",user);
	}
	
	@GetMapping("/dashboard")
	public String dashBoard(Model model, Principal principal) {
		
		return "normal/user_dashBoard";
	}
	
	@GetMapping("/add_contact")
	public String addContact(Model model) {
		model.addAttribute("contact", new Contact());
		model.addAttribute("message", new Message());
		return "normal/add_contact";
	}
	
	@PostMapping("/process-contact")
	public String processContact(Model model, @ModelAttribute Contact contact, @RequestParam("profilePicture") MultipartFile file, Principal principal) {
		
		try {
			
			if(file.isEmpty()) {
				contact.setImage("defaultContactPic.png");	
			}
			else {
				String fileName= file.getOriginalFilename();
				contact.setImage(fileName);	
				File saveFile= new ClassPathResource("/static/image").getFile();				
				Path path=  Paths.get(saveFile.getAbsolutePath()+File.separator+fileName);			
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
						
			System.out.println("===Contact== "+ contact);
			String userName= principal.getName();
			User user= userRepository.getUserByName(userName);
			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);
			if(contact != null)
				model.addAttribute("message", new Message("Contact Added Successfully!!","success"));
			//session.setAttribute("message", new Message("Contact Added!!","success"));

		} catch (Exception e) {
			System.out.println("error "+ e.getMessage());
			e.printStackTrace();
			//session.setAttribute("message", new Message("Something went wrong!! try again..","danger"));
		}
				return "normal/add_contact";
	}
	
	@GetMapping("/view_contacts/{currentPage}")
	public String viewContacts(@PathVariable("currentPage") Integer currentPage, Model model, Principal pricipal) {
		
		String userName= pricipal.getName();
		User user= userRepository.getUserByName(userName);
		Pageable pageable= PageRequest.of(currentPage, 3);
		Page<Contact> contacts= contactRepository.getContactsByUserId(user.getId(),pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/view_contacts";
	}
	
	@GetMapping("/view_singleContact/{cId}")
	public String viewSingleContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		Optional<Contact> optionalContact = contactRepository.findById(cId);
		Contact contact= optionalContact.get();
		
		String userName= principal.getName();
		User user = userRepository.getUserByName(userName);
		
		if(contact.getUser()!=null && user.getId()==contact.getUser().getId())
			model.addAttribute("contact", contact);

		return "normal/view_singleContact";
	}
	
	@GetMapping("/delete_contact/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, RedirectAttributes redirAttrs, Principal principal) {
		
		Contact contact = contactRepository.findById(cId).get();

		User user = userRepository.getUserByName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);
		
		redirAttrs.addFlashAttribute("success", "Contact ID: "+cId+" deleted successfully!!");
		
		return "redirect:/user/view_contacts/0";
	}
	
	@GetMapping("/update_singleContact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId, Principal principal, Model model) {
		
		Optional<Contact> optionalContact = contactRepository.findById(cId);
		Contact contact= optionalContact.get();
		
		String userName= principal.getName();
		User user = userRepository.getUserByName(userName);
		
		if(contact.getUser()!=null && user.getId()==contact.getUser().getId())
			model.addAttribute("contact", contact);
		
		
		return "normal/update_singleContact";	
	}
	
	@PostMapping("/process-updatedContact")
	public String processUpdatedContact(Model model, @ModelAttribute Contact contact, 
			@RequestParam("profilePicture") MultipartFile file, Principal principal, RedirectAttributes redirAttrs) {
		
		try {
			
			Contact oldContactDetails= contactRepository.findById(contact.getCid()).get(); 
			
			if(!file.isEmpty()) {
				
				File classPath= new ClassPathResource("/static/image").getFile();
				File tobeDeletedfile= new File(classPath, oldContactDetails.getImage());
				tobeDeletedfile.delete();
				
				String fileName= file.getOriginalFilename();
				contact.setImage(fileName);		
				Path path=  Paths.get(classPath.getAbsolutePath()+File.separator+fileName);			
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				contact.setImage(oldContactDetails.getImage());
			}
						
			System.out.println("===Contact== "+ contact);
			String userName= principal.getName();
			User user= userRepository.getUserByName(userName);
			contact.setUser(user);
			contactRepository.save(contact);
			redirAttrs.addFlashAttribute("success", "Contact ID: "+contact.getCid()+" Updated successfully!!");

		} catch (Exception e) {
			System.out.println("error "+ e.getMessage());
			e.printStackTrace();
			//session.setAttribute("message", new Message("Something went wrong!! try again..","danger"));
		}
		
		return "redirect:/user/view_singleContact/"+contact.getCid();
	}
	
	@GetMapping("/profile")
	public String profile() {
		
		//user already added for all Model above
		return "normal/user_profile";
	}
	
	@GetMapping("/settings")
	public String settings() {
		
		return "normal/settings";
	}
	
	@PostMapping("/process-password")
	public String processPassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, RedirectAttributes redirectAttributes) {
		
		User user = userRepository.getUserByName(principal.getName());
		
		if(bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			redirectAttributes.addFlashAttribute("success", "Password Changed Successfully :)");
		}
		else {
			redirectAttributes.addFlashAttribute("failure", "invalid Credentials :(");
		}
		
		return "redirect:/user/settings";
	}
	
	
	
	
	
	
	
	
	
}
