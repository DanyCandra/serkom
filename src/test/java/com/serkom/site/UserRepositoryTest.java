package com.serkom.site;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.serkom.site.entity.User;
import com.serkom.site.repository.UserRepository;



@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Test
	public void testCreateUser() {
		User userNamHM=new User("name@codejava.net", "Nam", "Ha Minh");
		userNamHM.setAddress("Purwokerto");
		userNamHM.setPhone("089667194026");
		userNamHM.setPhotos("sssss.jpg");
		
		User savedUser= repository.save(userNamHM);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	

}
