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
		alternativeChars.add("U+0364:"); // combining letter e

		mapperSut = new UmlautWordMapper(alternativeChars);
	}

	@After
	public void tearDown() throws Exception {
		// System.out.println(mappings);
	}

	// @Test
	public void shouldFindAllCombiningLetters() throws Exception {

		String all = "v́:v ÿ:y ů:u ï:i ŷ:y ǔ:u ı:i ē:e ý:y ã:a õ:o v̈:v u̇:u ŭ:u ā:a ō:o ē:e ī:i ū:u ė:e m̃:m ŭ:u ẽ:e ũ:u ś:s ŏ:o ǒ:o ǎ:a ǔ:u ẅ:w ẹ:e ǹ:n ă:a ṣ:s ẏ:y ẙ:y ẹ:e σ:o ĕ:e ĩ:i ẃ:w å:a g̮:g ń:n ỹ:y ě:e ṅ:n ȳ:y ň:n ṡ:s ć:c ę:e č:c ẘ:w ị:i ǧ:g ḥ:h ṁ:m ạ:a ṙ:r ľ:l Γ:g γ:g";
		all += " ú:u ñ:n Ø:0 ó:o à:a ê:e ë:e â:a ô:o î:i û:u æ:ae á:a é:e ò:o œ:oe è:e ù:u ì:i í:i ç:c ъ:b";
		String[] allSplit = all.split(" ");
		for (String mapping : allSplit) {
			String s = mapping.split(":")[0];
			if (s.length() > 1) {
				System.out.println(s);
				for (int i = 0; i < s.length(); i++) {
					System.out.println("\\u0" + Integer.toHexString(Character.codePointAt(s, i)));
				}
				System.out.println();
			}
		}

	}

	@Test
	public void shouldRemoveCombiningLetter() throws Exception {
		mappings = mapperSut.createMappings("svͤlen");

		assertEquals("svͤlen", mappings.get(0));
		assertEquals("svlen", mappings.get(1));
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
