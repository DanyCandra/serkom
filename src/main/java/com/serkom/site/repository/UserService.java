package com.serkom.site.repository;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.serkom.site.entity.User;



@Service
@Transactional
public class UserService {
	
	public static final int USER_PER_PAGE=4;

	@Autowired
	private UserRepository userRepository;
	
	public List<User> listAll() {
		return (List<User>) userRepository.findAll(Sort.by("id"));
	}
	
	public Page<User> listByPage(int pageNumber,String sortField, String sorDir, String keyword){
		Sort sort=Sort.by(sortField);
		sort=sorDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNumber -1 , USER_PER_PAGE, sort);

		if (keyword!=null) {
			return userRepository.findAll(keyword,pageable);
		}

		return userRepository.findAll(pageable);
	}
	
	public User save(User user) {
		boolean isUpdateingUser = (user.getId() != null);

		if (isUpdateingUser) {
			User existingUser=userRepository.findById(user.getId()).get();
		}else {
			
		}
		return userRepository.save(user);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.getUserByEmail(email);
		
		if (userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if (isCreatingNew) {
			if (userByEmail != null) return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		
		return true;
	}
	
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		
		userRepository.deleteById(id);
	}
	
	


}
