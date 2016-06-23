package sub.fwb;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LemmaNormalizerTest {

	private LemmaNormalizer norm = new LemmaNormalizer();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldRemovePipe() {
		List<String> results = norm.createMappings("bar|tuch");
		System.out.println(results);
	}

	@Test
	public void shouldExtendParentheses() {
		List<String> results = norm.createMappings("bla(s)gericht");
		System.out.println(results);
	}

	@Test
	public void shouldExtendTwoParentheses() {
		List<String> results = norm.createMappings("bla(s)gericht(en)geld");
		System.out.println(results);
	}

}
