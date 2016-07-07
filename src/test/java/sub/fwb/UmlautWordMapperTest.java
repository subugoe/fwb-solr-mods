package sub.fwb;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UmlautWordMapperTest {

	private UmlautWordMapper mapperSut;
	private List<String> mappings;

	@Before
	public void setUp() throws Exception {
		Set<String> alternativeChars = new HashSet<>();
		alternativeChars.add("ä:a");
		alternativeChars.add("ß:ss");
		alternativeChars.add("ö:o,oe");
		alternativeChars.add("u:v");
		alternativeChars.add("d:t");
		alternativeChars.add("sz:s,ß");

		mapperSut = new UmlautWordMapper(alternativeChars);
	}

	@After
	public void tearDown() throws Exception {
		// System.out.println(mappings);
	}

	@Test
	public void shouldReplaceDoubleCharAndSingleChar() {
		mappings = mapperSut.createMappings("fusz");

		assertEquals("fusz", mappings.get(0));
		assertEquals("fus", mappings.get(1));
		assertEquals("fuß", mappings.get(2));
		assertEquals("fvsz", mappings.get(3));
		assertEquals("fvs", mappings.get(4));
		assertEquals("fvß", mappings.get(5));
	}

	@Test
	public void shouldReplaceDoubleChar() {
		mappings = mapperSut.createMappings("fasz");

		assertEquals("fasz", mappings.get(0));
		assertEquals("fas", mappings.get(1));
		assertEquals("faß", mappings.get(2));
	}

	@Test
	public void shouldReplaceOneChar() {
		mappings = mapperSut.createMappings("bär");

		assertEquals(2, mappings.size());
		assertEquals("bär", mappings.get(0));
		assertEquals("bar", mappings.get(1));
	}

	@Test
	public void shouldReplaceWithTwoChars() {
		mappings = mapperSut.createMappings("faß");

		assertEquals("faß", mappings.get(0));
		assertEquals("fass", mappings.get(1));
	}

	@Test
	public void shouldReplaceWithTwoMappings() {
		mappings = mapperSut.createMappings("gedöns");

		assertEquals("gedöns", mappings.get(0));
		assertEquals("gedons", mappings.get(1));
		assertEquals("gedoens", mappings.get(2));
	}

	@Test
	public void shouldReplaceTwoDifferentChars() {
		mappings = mapperSut.createMappings("läß");

		assertEquals("läß", mappings.get(0));
		assertEquals("läss", mappings.get(1));
		assertEquals("laß", mappings.get(2));
		assertEquals("lass", mappings.get(3));
	}

}
