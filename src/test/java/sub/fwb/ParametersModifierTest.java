package sub.fwb;

import static org.junit.Assert.assertEquals;

import org.apache.solr.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ParametersModifierTest {

	private ParametersModifier modifier;
	private String expanded = "";
	private String hlQuery = "";

	@Rule
	public ExpectedException inTest = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		modifier = new ParametersModifier("lemma^1000 zitat^50", "zitat_text,artikel_text");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(expanded);
		System.out.println(hlQuery);
	}

	@Test
	public void shouldNotAllowQuoteAndColonInTerm() throws Exception {
	    inTest.expect(ParseException.class);
	    inTest.expectMessage("Anführungszeichen dürfen nur für Phrasen");
	    expanded = modifier.changeParamsForQuery("lemmaimme\":\"ss").q;
	}

	@Test
	public void shouldNotAllowQuoteInTermSearch() throws Exception {
	    inTest.expect(ParseException.class);
	    inTest.expectMessage("Anführungszeichen dürfen nur für Phrasen");
	    expanded = modifier.changeParamsForQuery("imme\"ss").q;
	}

	@Test
	public void shouldNotAllowQuoteInLemmaSearch() throws Exception {
	    inTest.expect(ParseException.class);
	    inTest.expectMessage("Anführungszeichen dürfen nur für Phrasen");
	    expanded = modifier.changeParamsForQuery("lemma:imme\"ss").q;
	}

	@Test
	public void shouldNotAllowTwoColons() throws Exception {
	    inTest.expect(ParseException.class);
	    inTest.expectMessage("Doppelpunkt nur einmal erlaubt");
	    expanded = modifier.changeParamsForQuery("lemma:imme\":\"ss").q;
	}

	@Test
	public void shouldConstructQfForExactSearch() throws Exception {
		String queryFields = modifier.changeParamsForQuery("zitat:Imbis EXAKT").qf;
		assertEquals("lemma_exakt^1000 zitat_exakt^50", queryFields);
	}

	@Test
	public void shouldConstructHlFlForExactSearch() throws Exception {
		hlQuery = modifier.changeParamsForQuery("zitat:Imbis EXAKT").hlFl;
		assertEquals("zitat_text_exakt,artikel_text_exakt", hlQuery);
	}

	@Test
	public void shouldHighlightInExactCitation() throws Exception {
		hlQuery = modifier.changeParamsForQuery("zitat:Imbis EXAKT").hlQ;
		assertEquals("zitat_text_exakt:*Imbis*", hlQuery);
	}

	@Test
	public void shouldHighlightLemmaExact() throws Exception {
		hlQuery = modifier.changeParamsForQuery("lemma:Imbis EXAKT").hlQ;
		assertEquals("lemma_text_exakt:*Imbis*", hlQuery);
	}

	@Test
	public void shouldSearchInExactLemma() throws Exception {
		expanded = modifier.changeParamsForQuery("lemma:Imbis EXAKT").q;
		assertEquals("+lemma_exakt:(Imbis Imbis* *Imbis*)^1000", expanded);
	}

	@Test
	public void shouldSearchExactly() throws Exception {
		expanded = modifier.changeParamsForQuery("ImBis EXAKT").q;
		assertEquals("ImBis ImBis* *ImBis* +(artikel_exakt:*ImBis* zitat_exakt:*ImBis*)", expanded);
	}

	@Test
	public void shouldAcceptTwoParensInOneWord() throws Exception {
		expanded = modifier.changeParamsForQuery("imbi(s)").q;
		// no exception
	}

	@Test(expected = ParseException.class)
	public void shouldRejectRightParenOnly() throws Exception {
		expanded = modifier.changeParamsForQuery("imbi(s))").q;
	}

	@Test(expected = ParseException.class)
	public void shouldRejectLeftParenOnly() throws Exception {
		expanded = modifier.changeParamsForQuery("(imbis").q;
	}

	@Test
	public void shouldAddParensForNot() throws Exception {
		expanded = modifier.changeParamsForQuery("NOT lemma:imbis OR (bla)").q;
		assertEquals("NOT (+lemma:(imbis imbis* *imbis*)^1000 ) OR (bla bla* *bla* +(artikel:*bla* zitat:*bla*) )",
				expanded);
	}

	@Test
	public void shouldKeepParens() throws Exception {
		expanded = modifier.changeParamsForQuery("NOT (lemma:imbis)").q;
		assertEquals("NOT (+lemma:(imbis imbis* *imbis*)^1000 )", expanded);
	}

	@Test
	public void shouldExpandNotOperator() throws Exception {
		expanded = modifier.changeParamsForQuery("NOT lemma:imbis").q;
		assertEquals("(NOT (+lemma:(imbis imbis* *imbis*)^1000 ) )", expanded);
	}

	@Test
	public void shouldExpandOrOperator() throws Exception {
		expanded = modifier.changeParamsForQuery("lemma:imbis OR lemma:bla").q;
		assertEquals("(+lemma:(imbis imbis* *imbis*)^1000 ) OR (+lemma:(bla bla* *bla*)^1000 )", expanded);
	}

	@Test
	public void shouldIgnoreSeveralSpaces() throws Exception {
		expanded = modifier.changeParamsForQuery(" a  b ").q;
		assertEquals("a a* *a* +(artikel:*a* zitat:*a*) b b* *b* +(artikel:*b* zitat:*b*)", expanded);
	}

	@Test
	public void shouldExpandWordAndPhrase() throws Exception {
		expanded = modifier.changeParamsForQuery("test \"my imbis\"").q;
		assertEquals(
				"test test* *test* +(artikel:*test* zitat:*test*) \"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\")",
				expanded);
	}

	@Test
	public void shouldExpandTwoSimplePhrases() throws Exception {
		expanded = modifier.changeParamsForQuery("\"my imbis\" \"your imbs\"").q;
		assertEquals(
				"\"my imbis\" +(artikel:\"my imbis\" zitat:\"my imbis\") \"your imbs\" +(artikel:\"your imbs\" zitat:\"your imbs\")",
				expanded);
	}

	@Test
	public void shouldExpandTwoWords() throws Exception {
		expanded = modifier.changeParamsForQuery("imb is").q;
		assertEquals("imb imb* *imb* +(artikel:*imb* zitat:*imb*) is is* *is* +(artikel:*is* zitat:*is*)", expanded);
	}

}
