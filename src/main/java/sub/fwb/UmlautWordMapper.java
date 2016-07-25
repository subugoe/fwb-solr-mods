package sub.fwb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UmlautWordMapper {

	private Map<String, String[]> charMappings = new HashMap<>();
	private List<String> mappedWords = new ArrayList<String>();
	private String term;

	public UmlautWordMapper(Set<String> mappingsSet) {
		for (String mapping : mappingsSet) {
			String[] umlautAndReplacements = mapping.split(":");
			String umlaut = umlautAndReplacements[0];
			if (umlaut.startsWith("U+")) {
				String hex = "0x" + umlaut.substring(2);
				umlaut = Character.toString((char)(int)Integer.decode(hex));
			}
			if (umlautAndReplacements.length >= 2) {
				String replacements = umlautAndReplacements[1];
				charMappings.put(umlaut, replacements.split(","));
			} else {
				charMappings.put(umlaut, new String[]{""});
			}
		}
	}

	public List<String> createMappings(String currentTerm) {
		term = currentTerm;
		mappedWords.add(term);

		int termLength = term.length();
		for (int i = termLength - 1; i >= 0; i--) {
			if (i >= 1) {
				String twoCh = term.substring(i - 1, i + 1);
				if (isUmlaut(twoCh)) {
					replaceUmlautAndAddToList(i - 1, i + 1);
					i--;
					continue;
				}
			}
			String ch = term.substring(i, i + 1);
			if (isUmlaut(ch)) {
				replaceUmlautAndAddToList(i, i + 1);
			}
		}

		return mappedWords;
	}

	private boolean isUmlaut(String ch) {
		if (charMappings.containsKey(ch)) {
			return true;
		}
		return false;
	}

	private void replaceUmlautAndAddToList(int from, int to) {
		List<String> wordsToAdd = new ArrayList<>();

		String umlaut = term.substring(from, to);
		String[] replacements = charMappings.get(umlaut);

		for (String wordFromList : mappedWords) {
			String prefix = wordFromList.substring(0, from);
			String postfix = wordFromList.substring(to);
			for (String replacement : replacements) {
				wordsToAdd.add(prefix + replacement + postfix);
			}
		}
		mappedWords.addAll(wordsToAdd);
	}

}
