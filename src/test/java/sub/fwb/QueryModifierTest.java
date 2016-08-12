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
		modifier = new QueryModifier("lemma^1000 zitat^50");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(expanded);
	}

	@Test
	public void shouldExpand() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis OR lemma:bla");
		assertEquals("lemma:(imbis imbis* *imbis*)^1000 OR lemma:(bla bla* *bla*)^1000", expanded);
	}

	@Test
	public void shouldExpandWithDash() throws Exception {
		expanded = modifier.expandQuery("-lach");
		assertEquals("\\-lach \\-lach* *\\-lach* +(artikel:*\\-lach* zitat:*\\-lach*)", expanded);
	}

	@Test
	public void shouldIgnoreSeveralSpaces() throws Exception {
		expanded = modifier.expandQuery(" a  b ");
		assertEquals("a a* *a* +(artikel:*a* zitat:*a*) b b* *b* +(artikel:*b* zitat:*b*)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectUnknownFieldName() throws Exception {
		expanded = modifier.expandQuery("lemma2:imbis");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectIncomplete() throws Exception {
		expanded = modifier.expandQuery("zitat:");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectEndingWithColon() throws Exception {
		expanded = modifier.expandQuery("zitat:bla:");
	}

	@Test
	public void shouldExpandRegexWithPrefix() throws Exception {
		expanded = modifier.expandQuery("lemma:/imbis/");
		assertEquals("+lemma:/imbis/", expanded);
	}

	@Test
	public void shouldExpandRegex() throws Exception {
		expanded = modifier.expandQuery("/imbis/");
		assertEquals("+(artikel:/imbis/ zitat:/imbis/)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("zitat:\"imb?s\"");
	}

	@Test
	public void shouldExpandComplexPhraseWithPrefix() throws Exception {
		expanded = modifier.expandQuery("zitat:\"imb*s ward\"");
		assertEquals("+_query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\"", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeadingWildcardsInPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imbis ?ard\"");
	}

	@Test(expected = ParseException.class)
	public void shouldRejectOneWordInComplexPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imb?s\"");
	}

	@Test
	public void shouldExpandComplexPhrase() throws Exception {
		expanded = modifier.expandQuery("\"imb*s ward\"");
		assertEquals(
				"_query_:\"{!complexphrase}\\\"imb*s ward\\\"\" +(_query_:\"{!complexphrase}artikel:\\\"imb*s ward\\\"\" _query_:\"{!complexphrase}zitat:\\\"imb*s ward\\\"\")",
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
		assertEquals("\"imbis\" +(artikel:\"imbis\" zitat:\"imbis\")", expanded);
	}

	@Test
	public void shouldEscapeBrackets() throws Exception {
		expanded = modifier.expandQuery("imb[i]s");
		assertEquals("imb\\[i\\]s imb\\[i\\]s* *imb\\[i\\]s* +(artikel:*imb\\[i\\]s* zitat:*imb\\[i\\]s*)", expanded);
	}

	@Test
	public void shouldEscapeParentheses() throws Exception {
		expanded = modifier.expandQuery("imb(i)s");
		assertEquals("imb\\(i\\)s imb\\(i\\)s* *imb\\(i\\)s* +(artikel:*imb\\(i\\)s* zitat:*imb\\(i\\)s*)", expanded);
	}

	@Test
	public void shouldEscapePipe() throws Exception {
		expanded = modifier.expandQuery("bar|tuch");
		assertEquals("bar\\|tuch bar\\|tuch* *bar\\|tuch* +(artikel:*bar\\|tuch* zitat:*bar\\|tuch*)", expanded);
	}

	@Test(expected = ParseException.class)
	public void shouldRejectTwoColons() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis:bla");
	}

	@Test
	public void shouldExpandPrefixedSearch() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis");
		assertEquals("+lemma:(imbis imbis* *imbis*)^1000", expanded);
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
		assertEquals(
				"test test* *test* +(artikel:*test* zitat:*test*) \"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\")",
				expanded);
	}

	@Test
	public void shouldExpandTwoSimplePhrases() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\" \"your imbs\"");
		assertEquals(
				"\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\") \"your imbs\" +(artikel:\"your imbs\" zitat:\"your imbs\")",
				expanded);
	}

	@Test
	public void shouldExpandSimplePhrase() throws Exception {
		expanded = modifier.expandQuery("\"my imbis\"");
		assertEquals("\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\")", expanded);
	}

	@Test
	public void shouldExpandTwoWords() throws Exception {
		expanded = modifier.expandQuery("imb is");
		assertEquals("imb imb* *imb* +(artikel:*imb* zitat:*imb*) is is* *is* +(artikel:*is* zitat:*is*)", expanded);
	}

	@Test
	public void shouldExpandOneWord() throws Exception {
		expanded = modifier.expandQuery("imbis");
		assertEquals("imbis imbis* *imbis* +(artikel:*imbis* zitat:*imbis*)", expanded);
	}

}
