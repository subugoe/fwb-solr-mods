package sub.fwb.parse;

import static org.junit.Assert.assertEquals;

import org.apache.solr.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TokenFactoryTest {
	
	private TokenFactory factory;
	private String expanded = "";
	private String hlQuery = "";

	@Before
	public void beforeEach() throws Exception {
		factory = new TokenFactory("lemma^1000 zitat^50");
	}

	@After
	public void afterEach() throws Exception {
		System.out.println(expanded);
		System.out.println(hlQuery);
	}

	// TODO
	// @Test
	public void shouldAddHlQueryToPhrase() throws Exception {
		hlQuery = hlQueryFrom("zitat:\"imbis ward\"");
		assertEquals("zitat_text:\"imbis ward\" ", hlQuery);
	}

	@Test
	public void shouldAddHlQuery() throws Exception {
		hlQuery = hlQueryFrom("zitat:imbis");
		assertEquals("zitat_text:*imbis* ", hlQuery);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectIncomplete() throws Exception {
		expanded = expandOneTokenString("zitat:");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectEndingWithColon() throws Exception {
		expanded = expandOneTokenString("zitat:bla:");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectTwoColons() throws Exception {
		expanded = expandOneTokenString("lemma:imbis:bla");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhraseWithPrefix() throws Exception {
		expanded = expandOneTokenString("zitat:\"imb?s\"");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeadingWildcardsInPhrase() throws Exception {
		expanded = expandOneTokenString("\"imbis ?ard\"");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhrase() throws Exception {
		expanded = expandOneTokenString("\"imb?s\"");
	}

	@Test
	public void shouldExpandComplexPhraseWithPrefix() throws Exception {
		expanded = expandOneTokenString("zitat:\"imb*s ward\"");
		assertEquals("+_query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\" ", expanded);
	}

	@Test
	public void shouldExpandComplexPhrase() throws Exception {
		expanded = expandOneTokenString("\"imb*s ward\"");
		assertEquals(
				"_query_:\"{!complexphrase}\\\"imb*s ward\\\"\" +(_query_:\"{!complexphrase}artikel:\\\"imb*s ward\\\"\" _query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\") ",
				expanded);
	}

	@Test
	public void shouldExpandRegexWithPrefix() throws Exception {
		expanded = expandOneTokenString("lemma:/imbis/");
		assertEquals("+lemma:/imbis/ ", expanded);
	}

	@Test
	public void shouldExpandRegex() throws Exception {
		expanded = expandOneTokenString("/imbis/");
		assertEquals("+(artikel:/imbis/ zitat:/imbis/) ", expanded);
	}

	@Test
	public void shouldExpandWithDash() throws Exception {
		expanded = expandOneTokenString("-lach");
		assertEquals("\\-lach \\-lach* *\\-lach* +(artikel:*\\-lach* zitat:*\\-lach*) ", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectUnknownFieldName() throws Exception {
		expanded = expandOneTokenString("lemma2:imbis");
	}

	@Test
	public void shouldExpandPrefixedSearch() throws Exception {
		expanded = expandOneTokenString("lemma:imbis");
		assertEquals("+lemma:(imbis imbis* *imbis*)^1000 ", expanded);
	}

	@Test
	public void shouldExpandOneWordPhraseWithPrefix() throws Exception {
		expanded = expandOneTokenString("lemma:\"imbis\"");
		assertEquals("+lemma:\"imbis\" ", expanded);
	}

	@Test
	public void shouldExpandOneWordPhrase() throws Exception {
		expanded = expandOneTokenString("\"imbis\"");
		assertEquals("\"imbis\" +(artikel:\"imbis\" zitat:\"imbis\") ", expanded);
	}

	@Test
	public void shouldEscapeBrackets() throws Exception {
		expanded = expandOneTokenString("imb[i]s");
		assertEquals("imb\\[i\\]s imb\\[i\\]s* *imb\\[i\\]s* +(artikel:*imb\\[i\\]s* zitat:*imb\\[i\\]s*) ", expanded);
	}

	@Test
	public void shouldEscapeParentheses() throws Exception {
		expanded = expandOneTokenString("imb(i)s");
		assertEquals("imb\\(i\\)s imb\\(i\\)s* *imb\\(i\\)s* +(artikel:*imb\\(i\\)s* zitat:*imb\\(i\\)s*) ", expanded);
	}

	@Test
	public void shouldEscapePipe() throws Exception {
		expanded = expandOneTokenString("bar|tuch");
		assertEquals("bar\\|tuch bar\\|tuch* *bar\\|tuch* +(artikel:*bar\\|tuch* zitat:*bar\\|tuch*) ", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectIncompletePhrase() throws Exception {
		expanded = expandOneTokenString("my imbis\"");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectUnfinishedPhrase() throws Exception {
		expanded = expandOneTokenString("\"my imbis");
	}

	@Test
	public void shouldExpandSimplePhrase() throws Exception {
		expanded = expandOneTokenString("\"my imbis\"");
		assertEquals("\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\") ", expanded);
	}

	@Test
	public void shouldExpandOneWord() throws Exception {
		expanded = expandOneTokenString("imbis");
		assertEquals("imbis imbis* *imbis* +(artikel:*imbis* zitat:*imbis*) ", expanded);
	}
	
	private String expandOneTokenString(String ts) throws Exception {
		return factory.createTokens(ts).get(0).getModifiedQuery();
	}

	private String hlQueryFrom(String query) throws Exception {
		return factory.createTokens(query).get(0).getHlQuery();
	}
}
