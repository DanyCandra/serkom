package com.serkom.site;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.serkom.site.entity.User;
import com.serkom.site.export.UserCSVExporter;
import com.serkom.site.export.UserExcelExporter;
import com.serkom.site.export.UserPDFExporter;
import com.serkom.site.repository.UserNotFoundException;
import com.serkom.site.repository.UserService;



@Controller
public class MainController {
	
	@Autowired
	private UserService userService;

	
	@GetMapping("")
	public String viewHomePage(Model model) {
		return ListByPage(1, model, "id", "asc","");
	}
	
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return ListByPage(1, model, "id", "asc","");
	}


	@GetMapping("/users/page/{pageNum}")
	public String ListByPage(@PathVariable(name="pageNum") int pageNum,
			Model model,
			@Param("sortFiled") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {

		System.out.println(sortField);
		System.out.println(sortDir);


		Page<User> pageuser=userService.listByPage(pageNum,sortField,sortDir,keyword);
		List<User> listUsers= pageuser.getContent();

//		System.out.println("PageNum ="+pageNum);
//		System.out.println("Total Element ="+pageuser.getTotalElements());
//		System.out.println("Total Pages ="+pageuser.getTotalPages());

		long startCount=(pageNum -1) * UserService.USER_PER_PAGE +1;
		long endCount=startCount + UserService.USER_PER_PAGE - 1;
		if (endCount > pageuser.getTotalElements()) {
			endCount = pageuser.getTotalElements();
		}

		String reverseSortDir=sortDir.equals("asc") ? "desc" : "asc";


		model.addAttribute("currentPage",pageNum);
		model.addAttribute("totalPages",pageuser.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems",pageuser.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "users/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers=userService.listAll();
		UserCSVExporter exporter=new UserCSVExporter();
		exporter.export(listUsers, response);

	}

	@GetMapping("/users/export/excel")
	public void exportExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers=userService.listAll();
		UserExcelExporter exporter =new UserExcelExporter();
		exporter.export(listUsers, response);

	}

	@GetMapping("/users/export/pdf")
	public void exportPdf(HttpServletResponse response) throws IOException {
		List<User> listUsers=userService.listAll();
		UserPDFExporter exporter =new UserPDFExporter();
		exporter.export(listUsers, response);

	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("pageTittle", "Create Contact");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		System.out.println(user);
		System.out.println(multipartFile.getOriginalFilename());

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			String uploadDir = "user-photos/"+user.getEmail();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			userService.save(user);
		}else {
			if (user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			userService.save(user);
		}

		
		redirectAttributes.addFlashAttribute("message", "The User has been saved successfully");
		return getRedirectURLtoAffectedUser(user);
	}
	
	private String getRedirectURLtoAffectedUser(User user) {
		String firstPartOfEmail=user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = userService.get(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTittle", "Edit Contact (ID : " + user.getId() + ")");
			return "users/user_form";
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			userService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The User ID " + id + " has been deleted");
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}

	
	
}
