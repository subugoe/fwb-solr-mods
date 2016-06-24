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
	public void shouldLeaveNormalWordAsIs() {
		List<String> results = norm.createMappings("imbis");
		assertEquals("imbis", results.get(0));
	}

	@Test
	public void shouldRemovePipe() {
		List<String> results = norm.createMappings("bar|tuch");
		assertEquals("bartuch", results.get(0));
	}

	@Test
	public void shouldRemoveAllPipes() {
		List<String> results = norm.createMappings("bar|tu|c|h");
		assertEquals("bartuch", results.get(0));
	}

	@Test
	public void shouldExtendParentheses() {
		List<String> results = norm.createMappings("amt(s)gericht");
		assertEquals("amtgericht", results.get(0));
		assertEquals("amtsgericht", results.get(1));
	}

	@Test
	public void shouldExtendTwoParentheses() {
		List<String> results = norm.createMappings("amt(s)gericht(en)geld");
		assertEquals("amtgerichtgeld", results.get(0));
		assertEquals("amtsgerichtgeld", results.get(1));
		assertEquals("amtgerichtengeld", results.get(2));
		assertEquals("amtsgerichtengeld", results.get(3));
	}

	@Test
	public void shouldExtendPrefixedParentheses() {
		List<String> results = norm.createMappings("(sankt)gericht");
		assertEquals("gericht", results.get(0));
		assertEquals("sanktgericht", results.get(1));
	}

	@Test
	public void shouldExtendPostfixedParentheses() {
		List<String> results = norm.createMappings("gericht(geld)");
		assertEquals("gericht", results.get(0));
		assertEquals("gerichtgeld", results.get(1));
	}

	@Test
	public void shouldDealwithBothCases() {
		List<String> results = norm.createMappings("bar|gericht(geld)");
		assertEquals("bargericht", results.get(0));
		assertEquals("bargerichtgeld", results.get(1));
	}

}
