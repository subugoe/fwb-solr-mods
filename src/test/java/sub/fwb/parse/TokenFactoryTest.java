package sub.fwb.parse;

import static org.junit.Assert.assertEquals;

import org.apache.solr.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TokenFactoryTest {
	
	private TokenFactory factory;
	private String expanded = "";

	@Before
	public void beforeEach() throws Exception {
		factory = new TokenFactory("lemma^1000 zitat^50");
	}

	@After
	public void afterEach() throws Exception {
		System.out.println(expanded);
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

}
