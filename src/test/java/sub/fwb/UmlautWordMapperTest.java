package sub.fwb;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UmlautWordMapperTest {

	private UmlautWordMapper mapperSut = new UmlautWordMapper();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		List<String> mappings = mapperSut.createMappings("bar");
		
		for (String mapping : mappings) {
			System.out.println(mapping);
		}
	}

}
