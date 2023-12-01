package gr.hcg.spapas.user;

import com.zaxxer.hikari.HikariDataSource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Serafeim Papastefanos
 */
public class DjangoUserStorageProviderTest {
	DjangoRepository dr;

	@BeforeEach
	public void beforeEach() {
		HikariDataSource ds = new HikariDataSource();

		String url = System.getenv("REGUSR_JDBC_URL");
		if(url == null  || url.isEmpty()) {
			System.out.println("REGUSR_JDBC_URL Not found!");
			url = "jdbc:mysql://user:user@127.0.0.1:3306/db";
		}
		ds.setJdbcUrl(url);

		dr = new DjangoRepository(ds);
	}

	@Test
	public void testCountUsers() {

		int count = dr.getUsersCount();
		System.out.println("User count: " + count);
		assertTrue(count > 100);

	}

	@Test
	public void testFindUser() {
		DjangoUser user = dr.findUserById("spapas@hcg.gr");
		assertEquals(user.username, "spapas@hcg.gr");
		assertEquals(user.firstName, "S");
		assertEquals(user.lastName, "s");

		System.out.println(user);
	}


}
