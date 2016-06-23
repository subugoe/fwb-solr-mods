package sub.fwb;

import static org.junit.Assert.*;

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
	public void shouldRemoveAllPipes() {
		List<String> results = norm.createMappings("bar|tu|c|h");
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

	@Test
	public void shouldExtendPrefixedParentheses() {
		List<String> results = norm.createMappings("(sankt)gericht");
		System.out.println(results);
	}

	@Test
	public void shouldExtendPostfixedParentheses() {
		List<String> results = norm.createMappings("gericht(geld)");
		System.out.println(results);
	}

}
