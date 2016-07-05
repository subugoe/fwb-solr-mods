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
		assertEquals(1, results.size());
		assertEquals("imbis", results.get(0));
	}

	@Test
	public void shouldRemovePipe() {
		List<String> results = norm.createMappings("bar|tuch");
		assertEquals("bar|tuch", results.get(0));
		assertEquals("bartuch", results.get(1));
	}

	@Test
	public void shouldRemoveAllPipes() {
		List<String> results = norm.createMappings("bar|tu|c|h");
		assertEquals("bar|tu|c|h", results.get(0));
		assertEquals("bartuch", results.get(1));
	}

	@Test
	public void shouldExtendParentheses() {
		List<String> results = norm.createMappings("amt(s)gericht");
		assertEquals("amt(s)gericht", results.get(0));
		assertEquals("amtgericht", results.get(1));
		assertEquals("amtsgericht", results.get(2));
	}

	@Test
	public void shouldExtendTwoParentheses() {
		List<String> results = norm.createMappings("amt(s)gericht(en)geld");
		assertEquals("amt(s)gericht(en)geld", results.get(0));
		assertEquals("amtgerichtgeld", results.get(1));
		assertEquals("amtsgerichtgeld", results.get(2));
		assertEquals("amtgerichtengeld", results.get(3));
		assertEquals("amtsgerichtengeld", results.get(4));
	}

	@Test
	public void shouldExtendPrefixedParentheses() {
		List<String> results = norm.createMappings("(sankt)gericht");
		assertEquals("(sankt)gericht", results.get(0));
		assertEquals("gericht", results.get(1));
		assertEquals("sanktgericht", results.get(2));
	}

	@Test
	public void shouldExtendPostfixedParentheses() {
		List<String> results = norm.createMappings("gericht(geld)");
		assertEquals("gericht(geld)", results.get(0));
		assertEquals("gericht", results.get(1));
		assertEquals("gerichtgeld", results.get(2));
	}

	@Test
	public void shouldDealwithBothCases() {
		List<String> results = norm.createMappings("bar|gericht(geld)");
		assertEquals("bar|gericht(geld)", results.get(0));
		assertEquals("bargericht", results.get(1));
		assertEquals("bargerichtgeld", results.get(2));
	}

	@Test
	public void shouldExtendLittleParens() {
		List<String> results = norm.createMappings("b⁽ä⁾ren");
		assertEquals("b⁽ä⁾ren", results.get(0));
		assertEquals("bären", results.get(1));
	}

	@Test
	public void shouldExtendBrackets() {
		List<String> results = norm.createMappings("geld[los]");
		assertEquals("geld[los]", results.get(0));
		assertEquals("geld", results.get(1));
		assertEquals("geldlos", results.get(2));
	}

	@Test
	public void shouldRemoveOutsideBrackets() {
		List<String> results = norm.createMappings("[geld]");
		assertEquals("[geld]", results.get(0));
		assertEquals("geld", results.get(1));
	}

	@Test
	public void shouldRemoveOutsideParens() {
		List<String> results = norm.createMappings("(geld)");
		assertEquals("(geld)", results.get(0));
		assertEquals("geld", results.get(1));
	}

	@Test
	public void shouldRemoveLeftParen() {
		List<String> results = norm.createMappings("(geld");
		assertEquals("(geld", results.get(0));
		assertEquals("geld", results.get(1));
	}

	@Test
	public void shouldRemoveRightParen() {
		List<String> results = norm.createMappings("geld)");
		assertEquals("geld)", results.get(0));
		assertEquals("geld", results.get(1));
	}

}
