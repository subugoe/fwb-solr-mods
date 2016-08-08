package sub.fwb;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.parser.ParseException;

public class QueryModifier {

	private List<String> qTerms = new ArrayList<>();
	private List<String> qPhrases = new ArrayList<>();
	private List<String> qRegexes = new ArrayList<>();
	private String expandedQuery = "";

	public String expandQuery(String origQuery) throws ParseException {
		splitIntoTermsAndPhrasesAndRegexes(origQuery);

		processTerms();
		processPhrases();
		processRegexes();

		return expandedQuery.trim();
	}

	private void splitIntoTermsAndPhrasesAndRegexes(String origQuery) throws ParseException {
		String[] qParts = origQuery.split(" ");
		String currentPhrase = "";
		for (String q : qParts) {
			if (isARegex(q)) {
				qRegexes.add(q);
			} else if (isAPhrase(q)) {
				qPhrases.add(q);
			} else if (startingAPhrase(q) || insideAPhrase(currentPhrase, q)) {
				currentPhrase += q + " ";
			} else if (finishingAPhrase(q)) {
				if (currentPhrase.isEmpty()) {
					throw new ParseException("Phrase ohne Anfang: " + q);
				}
				qPhrases.add(currentPhrase + q);
				currentPhrase = "";
			} else {
				qTerms.add(q);
			}
		}
		if (!currentPhrase.isEmpty()) {
			throw new ParseException("Phrase nicht komplett: " + currentPhrase);
		}
	}

	private void processTerms() throws ParseException {
		for (int i = 0; i < qTerms.size(); i++) {
			String term = qTerms.get(i);
			String escapedTerm = escapeSpecialChars(term);
			if (escapedTerm.contains(":")) {
				int colonCount = escapedTerm.length() - escapedTerm.replaceAll(":", "").length();
				if (colonCount > 1) {
					throw new ParseException("Doppelpunkt nur einmal erlaubt: " + term);
				}
				String[] prePost = escapedTerm.split(":");
				if (prePost.length == 1) {
					throw new ParseException("Unvollständige Suchanfrage: " + term);
				}
				String prefix = prePost[0];
				String postfix = prePost[1];
				boolean isFirst = i == 0;
				boolean isLast = i == qTerms.size() - 1;
				if (!isLast && qTerms.get(i + 1).equals("OR") || !isFirst && qTerms.get(i - 1).equals("OR")) {
					expandedQuery += String.format("%s:(%s %s* *%s*) ", prefix, postfix, postfix, postfix);
				} else {
					expandedQuery += String.format("+%s:(%s %s* *%s*) ", prefix, postfix, postfix, postfix);
				}
			} else if (escapedTerm.equals("AND")) {
				// ignore
			} else if (escapedTerm.equals("OR")) {
				expandedQuery += "OR ";
			} else {
				expandedQuery += String.format("%s %s* *%s* +(artikel:*%s* zitat:*%s*) ", escapedTerm, escapedTerm,
						escapedTerm, escapedTerm, escapedTerm);
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

	private void processPhrases() throws ParseException {
		for (String phrase : qPhrases) {
			checkForLeadingWildcards(phrase);
			if (isComplex(phrase) && hasPrefix(phrase)) {
				checkIfOneWord(phrase);
				String escapedPhrase = phrase.replaceAll("\"", "\\\\\"");
				expandedQuery += String.format("+_query_:\"{!complexphrase}%s\" ", escapedPhrase);
			} else if (isComplex(phrase) && !hasPrefix(phrase)) {
				checkIfOneWord(phrase);
				String escapedPhrase = phrase.replaceAll("\"", "\\\\\"");
				expandedQuery += String.format(
						"_query_:\"{!complexphrase}%s\" +(_query_:\"{!complexphrase}artikel:%s\" _query_:\"{!complexphrase}zitat:%s\") ",
						escapedPhrase, escapedPhrase, escapedPhrase);
			} else if (!isComplex(phrase) && hasPrefix(phrase)) {
				expandedQuery += String.format("+%s ", phrase);
			} else {
				expandedQuery += String.format("%s +(artikel:%s zitat:%s) ", phrase, phrase, phrase);
			}
		}
	}

	private void processRegexes() throws ParseException {
		for (String phrase : qRegexes) {
			if (hasPrefix(phrase)) {
				expandedQuery += String.format("+%s ", phrase);
			} else {
				expandedQuery += String.format("+(artikel:%s zitat:%s) ", phrase, phrase);
			}
		}
	}

	private void checkIfOneWord(String phrase) throws ParseException {
		if (phrase.split("[\" ]+").length == 2) {
			throw new ParseException("Phrasen mit * oder ? müssen mehrere Wörter enthalten: " + phrase);
		}

	}

	private boolean hasPrefix(String phrase) {
		return phrase.matches("[a-z]+:.*");
	}

	private boolean isComplex(String phrase) {
		return phrase.contains("*") || phrase.contains("?");
	}

	private void checkForLeadingWildcards(String phrase) throws ParseException {
		String[] parts = phrase.split("[\" ]");
		for (String part : parts) {
			if (part.startsWith("*") || part.startsWith("?")) {
				throw new ParseException("Bei Phrasen sind * und ? am Wortanfang nicht erlaubt: " + part);
			}
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

	private boolean isARegex(String q) {
		return (q.indexOf("/") < q.length() - 1) && q.endsWith("/");
	}

	private boolean isAPhrase(String q) {
		return (q.indexOf("\"") < q.length() - 1) && q.endsWith("\"");
	}

}
