package sub.fwb;

import static org.junit.Assert.assertEquals;

import org.apache.solr.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryModifierTest {

	private QueryModifier modifier;
	private String expanded = "";
	private String hlQuery = "";

	@Before
	public void setUp() throws Exception {
		modifier = new QueryModifier("lemma^1000 zitat^50");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(expanded);
		System.out.println(hlQuery);
	}

	@Test
	public void shouldAcceptTwoParensInOneWord() throws Exception {
		expanded = modifier.expandQuery("imbi(s)")[0];
		// no exception
	}

	@Test(expected = ParseException.class)
	public void shouldRejectRightParenOnly() throws Exception {
		expanded = modifier.expandQuery("imbi(s))")[0];
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeftParenOnly() throws Exception {
		expanded = modifier.expandQuery("(imbis")[0];
	}

	@Test
	public void shouldKeepParens() throws Exception {
		expanded = modifier.expandQuery("NOT (lemma:imbis)")[0];
		assertEquals("NOT (+lemma:(imbis imbis* *imbis*)^1000 )", expanded);
	}

	@Test
	public void shouldExpandNotOperator() throws Exception {
		expanded = modifier.expandQuery("NOT lemma:imbis")[0];
		assertEquals("(NOT (+lemma:(imbis imbis* *imbis*)^1000 ) )", expanded);
	}

	@Test
	public void shouldExpandOrOperator() throws Exception {
		expanded = modifier.expandQuery("lemma:imbis OR lemma:bla")[0];
		assertEquals("(+lemma:(imbis imbis* *imbis*)^1000 ) OR (+lemma:(bla bla* *bla*)^1000 )", expanded);
	}

	@Test
	public void shouldIgnoreSeveralSpaces() throws Exception {
		expanded = modifier.expandQuery(" a  b ")[0];
		assertEquals("a a* *a* +(artikel:*a* zitat:*a*) b b* *b* +(artikel:*b* zitat:*b*)", expanded);
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

	@Test
	public void shouldExpandTwoWords() throws Exception {
		expanded = modifier.expandQuery("imb is")[0];
		assertEquals("imb imb* *imb* +(artikel:*imb* zitat:*imb*) is is* *is* +(artikel:*is* zitat:*is*)", expanded);
	}

}
