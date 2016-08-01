package sub.fwb;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.parser.ParseException;

public class QueryModifier {

	private List<String> qTerms = new ArrayList<>();
	private List<String> qPhrases = new ArrayList<>();
	private String expandedQuery = "";

	public String expandQuery(String origQuery) throws ParseException {
		splitInTermsAndPhrases(origQuery);

		processTerms();
		processPhrases();

		return expandedQuery.trim();
	}

	private void splitInTermsAndPhrases(String origQuery) throws ParseException {
		String[] qParts = origQuery.split(" ");
		String currentPhrase = "";
		for (String q : qParts) {
			if (startingAPhrase(q) || insideAPhrase(currentPhrase, q)) {
				currentPhrase += q + " ";
				continue;
			} else if (finishingAPhrase(q)) {
				if (currentPhrase.isEmpty()) {
					throw new ParseException("Phrase ohne Anfang: " + q);
				}
				qPhrases.add(currentPhrase + q);
				currentPhrase = "";
				continue;
			}
			qTerms.add(q);
		}
		if (!currentPhrase.isEmpty()) {
			throw new ParseException("Phrase nicht komplett: " + currentPhrase);
		}
	}

	private void processTerms() throws ParseException {
		for (String term : qTerms) {
			String escapedTerm = escapeSpecialChars(term);
			if (escapedTerm.contains(":")) {
				String[] prePost = escapedTerm.split(":");
				if (prePost.length > 2) {
					throw new ParseException("Doppelpunkt nur einmal erlaubt: " + term);
				}
				String prefix = prePost[0];
				String postfix = prePost[1];
				expandedQuery += String.format("+(%s:%s %s:%s* %s:*%s*) ", prefix, postfix, prefix, postfix, prefix,
						postfix);
			} else {
				expandedQuery += String.format("%s %s* *%s* +artikel:*%s* ", escapedTerm, escapedTerm, escapedTerm,
						escapedTerm, escapedTerm);
			}
		}
	}

	private String escapeSpecialChars(String term) {
		term = term.replaceAll("\\|", "\\\\|");
		term = term.replaceAll("\\(", "\\\\(");
		term = term.replaceAll("\\)", "\\\\)");
		term = term.replaceAll("\\[", "\\\\[");
		term = term.replaceAll("\\]", "\\\\]");
		return term;
	}

	private void processPhrases() {
		for (String phrase : qPhrases) {
			expandedQuery += String.format("%s +artikel:%s ", phrase, phrase);
		}
	}

	private boolean startingAPhrase(String q) {
		return q.contains("\"") && !q.endsWith("\"");
	}

	private boolean insideAPhrase(String currentPhrase, String q) {
		return !currentPhrase.isEmpty() && !q.endsWith("\"");
	}

	private boolean finishingAPhrase(String q) {
		return q.endsWith("\"");
	}

}
