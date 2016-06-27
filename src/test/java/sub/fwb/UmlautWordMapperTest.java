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
		Set<String> umlautMappings = new HashSet<>();
		umlautMappings.add("ä:a");
		umlautMappings.add("ß:ss");
		umlautMappings.add("ö:o,oe");
		
		mapperSut = new UmlautWordMapper(umlautMappings);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(mappings);
	}

	@Test
	public void shouldReplaceOneUmlaut() {
		mappings = mapperSut.createMappings("bär");
		
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
	public void shouldReplaceTwoUmlauts() {
		mappings = mapperSut.createMappings("läß");
		
		assertEquals("läß", mappings.get(0));
		assertEquals("läss", mappings.get(1));
		assertEquals("laß", mappings.get(2));
		assertEquals("lass", mappings.get(3));
	}

}
