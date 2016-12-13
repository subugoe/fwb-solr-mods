package sub.fwb.parse;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParseUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldTrimSpecialCharsFromSnippets() {
		String snippet = "/‹),]-. test /-[(,";
		String changedSnippet = ParseUtil.trimSpecialChars(snippet);
		// System.out.println(changedSnippet);
		assertEquals("test", changedSnippet);
	}

}
