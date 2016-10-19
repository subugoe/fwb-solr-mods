package sub.fwb.parse;

import java.util.List;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.tokens.QueryToken;
import sub.fwb.parse.tokens.QueryTokenPrefixed;
import sub.fwb.parse.tokens.QueryTokenSymbol;

public class ParseUtil {

	public static final String EXACT = "_exakt";

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

	public static String article(String ending) {
		return "artikel" + ending;
	}
	public static String articleText(String ending) {
		return "artikel_text" + ending;
	}
	public static String citation(String ending) {
		return "zitat" + ending;
	}
	public static String citationText(String ending) {
		return "zitat_text" + ending;
	}

}
