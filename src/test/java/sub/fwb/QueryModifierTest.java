package sub.fwb;

import static org.junit.Assert.*;

import org.apache.solr.parser.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryModifierTest {

	private QueryModifier modifier;
	private String expanded;

	@Before
	public void setUp() throws Exception {
		modifier = new QueryModifier();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeadingWildcardsInPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imbis ?ard\"");
	}

	@Test
	public void shouldExpandComplexQuery() throws Exception {
		expanded = modifier.expandQuery("\"imb*s ward\"");
		assertEquals(
				"_query_:\"{!complexphrase}\\\"imb*s ward\\\"\" +_query_:\"{!complexphrase}artikel:\\\"imb*s ward\\\"\"",
				expanded);
	}

	@Test
	public void shouldExpandOneWordPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("lemma:\"imbis\"");
		assertEquals("+lemma:\"imbis\"", expanded);
	}

	@Test
	public void shouldExpandOneWordPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imbis\"");
		assertEquals("\"imbis\" +artikel:\"imbis\"", expanded);
	}

	@Test
	public void shouldEscapeBrackets() throws Exception {
		expanded = modifier.expandQuery("imb[i]s");
		assertEquals("imb\\[i\\]s imb\\[i\\]s* *imb\\[i\\]s* +artikel:*imb\\[i\\]s*", expanded);
	}

	@Test
	public void shouldEscapeParentheses() throws Exception {
		expanded = modifier.expandQuery("imb(i)s");
		assertEquals("imb\\(i\\)s imb\\(i\\)s* *imb\\(i\\)s* +artikel:*imb\\(i\\)s*", expanded);
	}

	@Test
	public void shouldEscapePipe() throws Exception {
		expanded = modifier.expandQuery("bar|tuch");
		assertEquals("bar\\|tuch bar\\|tuch* *bar\\|tuch* +artikel:*bar\\|tuch*", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectTwoColons() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis:bla");
	}

	@Test
	public void shouldExpandPrefixedSearch() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis");
		assertEquals("+lemma:(imbis imbis* *imbis*)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectIncompletePhrase() throws Exception {
		expanded = modifier.expandQuery("my imbis\"");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectUnfinishedPhrase() throws Exception {
		expanded = modifier.expandQuery("\"my imbis");
	}

	@Test
	public void shouldExpandWordAndPhrase() throws Exception {
		expanded = modifier.expandQuery("test \"my imbis\"");
		assertEquals("test test* *test* +artikel:*test* \"my imbis\" +artikel:\"my imbis\"", expanded);
	}

	@Test
	public void shouldExpandTwoSimplePhrases() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\" \"your imbs\"");
		assertEquals("\"my imbis\" +artikel:\"my imbis\" \"your imbs\" +artikel:\"your imbs\"", expanded);
	}

	@Test
	public void shouldExpandSimplePhrase() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\"");
		assertEquals("\"my imbis\" +artikel:\"my imbis\"", expanded);
	}

	@Test
	public void shouldExpandTwoWords() throws Exception {
		expanded = modifier.expandQuery("imb is");
		assertEquals("imb imb* *imb* +artikel:*imb* is is* *is* +artikel:*is*", expanded);
	}

	@Test
	public void shouldExpandOneWord() throws Exception {
		expanded = modifier.expandQuery("imbis");
		assertEquals("imbis imbis* *imbis* +artikel:*imbis*", expanded);
	}

}
