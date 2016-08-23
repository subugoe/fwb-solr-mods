package sub.fwb.parse;

import org.apache.solr.parser.ParseException;

public class ParseUtil {

	public static void checkIfOneWord(String complexPhrase) throws ParseException {
		if (complexPhrase.split("[\" ]+").length == 2) {
			throw new ParseException("Phrasen mit * oder ? müssen mehrere Wörter enthalten: " + complexPhrase);
		}
	}

	public static void checkForLeadingWildcards(String complexPhrase) throws ParseException {
		String[] parts = complexPhrase.split("[\" ]");
		for (String part : parts) {
			if (part.startsWith("*") || part.startsWith("?")) {
				throw new ParseException("Bei Phrasen sind * und ? am Wortanfang nicht erlaubt: " + part);
			}
		}
	}

	public static String freeFromCircumflexAndDollar(String s) {
		if (s.startsWith("^")) {
			s = s.substring(1);
		}
		if (s.endsWith("$")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

}
