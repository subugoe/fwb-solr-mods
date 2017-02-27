package sub.fwb.parse;

import org.apache.solr.parser.ParseException;

public class ParseUtil {

	public static final String EXACT = "_exakt";

	// This is not supposed to happen anymore
	@Deprecated
	public static void checkIfOneWord(String complexPhrase) throws ParseException {
		if (complexPhrase.split("[\" ]+").length == 2) {
			throw new ParseException("Phrasen mit * oder ? müssen mehrere Wörter enthalten: " + complexPhrase);
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

	public static void checkForProhibitedCharsInTerm(String term) throws ParseException {
		if (term.contains("\"")) {
			throw new ParseException("Anführungszeichen dürfen nur für Phrasen verwendet werden, z. B. \"der imbis\"");
		}
	}

	public static String removeSpecialChars(String str) {
		return str.replaceAll("[‒&<>′`″”∣%«»‛⅓⅙⅔·⅕#˄˚{}¼¾©@‚°=½§…℔₰¶⸗˺˹„“+–!;›‹\\.,’·‘'%]+", "");
	}

	public static String removeParensAndPipe(String str) {
		String removed = str.replace("(", " ");
		removed = removed.replace(")", " ");
		removed = removed.replace("[", " ");
		removed = removed.replace("]", " ");
		removed = removed.replace("|", " ");
		return removed;
	}

	public static String trimSpecialChars(String snippet) {
		String[] front = { ",", ")", "/", "‹", ".", "]", "-", " ", ";", ":" };
		String[] back = { "(", ",", "/", "[", "-", " ", ";", ":" };
		frontwhile:
		while (true) {
			for (String f : front) {
				if (snippet.startsWith(f)) {
					snippet = snippet.substring(1);
				}
			}
			for (String f : front) {
				if (snippet.startsWith(f)) {
					continue frontwhile;
				}
			}
			break;
		}
		backwhile:
		while (true) {
			for (String b : back) {
				if (snippet.endsWith(b)) {
					snippet = snippet.substring(0, snippet.length() - 1);
				}
			}
			for (String b : back) {
				if (snippet.endsWith(b)) {
					continue backwhile;
				}
			}
			break;
		}
		return snippet;
	}
}
