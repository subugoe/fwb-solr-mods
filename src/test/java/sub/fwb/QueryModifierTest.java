package sub.fwb;

import static org.junit.Assert.*;

import org.apache.solr.parser.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryModifierTest {

	private QueryModifier modifier;
	private String expanded;
	private String hlQuery;

	@Before
	public void setUp() throws Exception {
		modifier = new QueryModifier("lemma^1000 zitat^50");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(expanded);
		System.out.println(hlQuery);
	}

	// TODO
	// @Test
	public void shouldAddHlQueryToPhrase() throws Exception {
		hlQuery = modifier.expandQuery("zitat:\"imbis ward\"")[1];
		assertEquals("zitat_text:\"imbis ward\"", hlQuery);
	}

	@Test
	public void shouldAddHlQuery() throws Exception {
		hlQuery = modifier.expandQuery("zitat:imbis")[1];
		assertEquals("zitat_text:*imbis*", hlQuery);
	}

	@Test
	public void shouldExpandOrOperator() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis OR lemma:bla")[0];
		assertEquals("lemma:(imbis imbis* *imbis*)^1000 OR lemma:(bla bla* *bla*)^1000", expanded);
	}

	// remove
	@Test
	public void shouldExpandWithDash() throws Exception {
		expanded = modifier.expandQuery("-lach")[0];
		assertEquals("\\-lach \\-lach* *\\-lach* +(artikel:*\\-lach* zitat:*\\-lach*)", expanded);
	}

	@Test
	public void shouldIgnoreSeveralSpaces() throws Exception {
		expanded = modifier.expandQuery(" a  b ")[0];
		assertEquals("a a* *a* +(artikel:*a* zitat:*a*) b b* *b* +(artikel:*b* zitat:*b*)", expanded);
	}

	// remove
	@Test(expected = ParseException.class)
	public void shouldRejectUnknownFieldName() throws Exception {
		expanded = modifier.expandQuery("lemma2:imbis")[0];
	}

	@Test(expected = ParseException.class)
	public void shouldRejectIncomplete() throws Exception {
		expanded = modifier.expandQuery("zitat:")[0];
	}

	@Test(expected = ParseException.class)
	public void shouldRejectEndingWithColon() throws Exception {
		expanded = modifier.expandQuery("zitat:bla:")[0];
	}

	@Test
	public void shouldExpandRegexWithPrefix() throws Exception {
		expanded = modifier.expandQuery("lemma:/imbis/")[0];
		assertEquals("+lemma:/imbis/", expanded);
	}

	@Test
	public void shouldExpandRegex() throws Exception {
		expanded = modifier.expandQuery("/imbis/")[0];
		assertEquals("+(artikel:/imbis/ zitat:/imbis/)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("zitat:\"imb?s\"")[0];
	}

	@Test
	public void shouldExpandComplexPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("zitat:\"imb*s ward\"")[0];
		assertEquals("+_query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\"", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeadingWildcardsInPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imbis ?ard\"")[0];
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imb?s\"")[0];
	}

	@Test
	public void shouldExpandComplexPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imb*s ward\"")[0];
		assertEquals(
				"_query_:\"{!complexphrase}\\\"imb*s ward\\\"\" +(_query_:\"{!complexphrase}artikel:\\\"imb*s ward\\\"\" _query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\")",
				expanded);
	}

	// remove
	@Test
	public void shouldExpandOneWordPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("lemma:\"imbis\"")[0];
		assertEquals("+lemma:\"imbis\"", expanded);
	}

	// remove
	@Test
	public void shouldExpandOneWordPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imbis\"")[0];
		assertEquals("\"imbis\" +(artikel:\"imbis\" zitat:\"imbis\")", expanded);
	}

	// remove
	@Test
	public void shouldEscapeBrackets() throws Exception {
		expanded = modifier.expandQuery("imb[i]s")[0];
		assertEquals("imb\\[i\\]s imb\\[i\\]s* *imb\\[i\\]s* +(artikel:*imb\\[i\\]s* zitat:*imb\\[i\\]s*)", expanded);
	}

	// remove
	@Test
	public void shouldEscapeParentheses() throws Exception {
		expanded = modifier.expandQuery("imb(i)s")[0];
		assertEquals("imb\\(i\\)s imb\\(i\\)s* *imb\\(i\\)s* +(artikel:*imb\\(i\\)s* zitat:*imb\\(i\\)s*)", expanded);
	}

	// remove
	@Test
	public void shouldEscapePipe() throws Exception {
		expanded = modifier.expandQuery("bar|tuch")[0];
		assertEquals("bar\\|tuch bar\\|tuch* *bar\\|tuch* +(artikel:*bar\\|tuch* zitat:*bar\\|tuch*)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectTwoColons() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis:bla")[0];
	}

	// remove
	@Test
	public void shouldExpandPrefixedSearch() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis")[0];
		assertEquals("+lemma:(imbis imbis* *imbis*)^1000", expanded);
	}

	// remove
	@Test(expected = ParseException.class)
	public void shouldRejectIncompletePhrase() throws Exception {
		expanded = modifier.expandQuery("my imbis\"")[0];
	}

	// remove
	@Test(expected = ParseException.class)
	public void shouldRejectUnfinishedPhrase() throws Exception {
		expanded = modifier.expandQuery("\"my imbis")[0];
	}

	@Test
	public void shouldExpandWordAndPhrase() throws Exception {
		expanded = modifier.expandQuery("test \"my imbis\"")[0];
		assertEquals(
				"test test* *test* +(artikel:*test* zitat:*test*) \"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\")",
				expanded);
	}

	@Test
	public void shouldExpandTwoSimplePhrases() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\" \"your imbs\"")[0];
		assertEquals(
				"\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\") \"your imbs\" +(artikel:\"your imbs\" zitat:\"your imbs\")",
				expanded);
	}

	// remove
	@Test
	public void shouldExpandSimplePhrase() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\"")[0];
		assertEquals("\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\")", expanded);
	}

	@Test
	public void shouldExpandTwoWords() throws Exception {
		expanded = modifier.expandQuery("imb is")[0];
		assertEquals("imb imb* *imb* +(artikel:*imb* zitat:*imb*) is is* *is* +(artikel:*is* zitat:*is*)", expanded);
	}

	// remove
	@Test
	public void shouldExpandOneWord() throws Exception {
		expanded = modifier.expandQuery("imbis")[0];
		assertEquals("imbis imbis* *imbis* +(artikel:*imbis* zitat:*imbis*)", expanded);
	}

}
