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
			String umlaut = mapping.split(":")[0];
			String replacements = mapping.split(":")[1];
			charMappings.put(umlaut, replacements.split(","));
		}
	}

	public List<String> createMappings(String currentTerm) {
		term = currentTerm;
		mappedWords.add(term);

		int termLength = term.length();
		for (int i = termLength - 1; i >= 0; i--) {
			char ch = term.charAt(i);
			if (isUmlaut(ch)) {
				replaceUmlautAndAddToList(i);
			}
		}

		return mappedWords;
	}

	private boolean isUmlaut(char ch) {
		if (charMappings.containsKey("" + ch)) {
			return true;
		}
		return false;
	}

	private void replaceUmlautAndAddToList(int umlautPosition) {
		List<String> wordsToAdd = new ArrayList<>();

		String umlaut = "" + term.charAt(umlautPosition);
		String[] replacements = charMappings.get(umlaut);

		for (String wordFromList : mappedWords) {
			String prefix = wordFromList.substring(0, umlautPosition);
			String postfix = wordFromList.substring(umlautPosition + 1);
			for (String replacement : replacements) {
				wordsToAdd.add(prefix + replacement + postfix);
			}
		}
		mappedWords.addAll(wordsToAdd);
	}

}
