package sub.fwb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LemmaNormalizer {

	private List<String> mappedWords = new ArrayList<String>();

	public List<String> createMappings(String currentTerm) {
		
		if (currentTerm.contains("|")) {
			mappedWords.add(currentTerm.replaceAll("\\|", ""));
		}
		
		if (currentTerm.contains("(") && currentTerm.contains(")")) {
			extendByParentheses(currentTerm);
		}
		
		return mappedWords;
	}

	private void extendByParentheses(String currentTerm) {
		String parensRegex = "\\((.*?)\\)";
		
		mappedWords.add(currentTerm.replaceAll(parensRegex, ""));
		
		String[] termParts = currentTerm.split(parensRegex);
		
		List<String> parensParts = extractUsingRegex(parensRegex, currentTerm);
		
		for (Set<Integer> combination : generateAllCombinations(parensParts.size())) {
			addToMappedWords(combination, parensParts, termParts);
		}
		
	}

	private void addToMappedWords(Set<Integer> combination, List<String> parensParts, String[] termParts) {
		String toBeAdded = "";
		for (int i = 0; i < termParts.length; i++) {
			toBeAdded += termParts[i];
			if (combination.contains(i)) {
				toBeAdded += parensParts.get(i);
			}
		}
		mappedWords.add(toBeAdded);
	}

	private List<String> extractUsingRegex(String regex, String s) {
		List<String> results = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			results.add(matcher.group(1));
		}

		if (results.isEmpty()) {
			results.add("");
		}
		return results;
	}

	private List<Set<Integer>> generateAllCombinations(int numberOfParentheses) {
		List<Set<Integer>> results = new ArrayList<>();
		for (int i = 0; i < numberOfParentheses; i++) {
			List<Set<Integer>> resultsForI = new ArrayList<>();
			Set<Integer> currentCombs = new HashSet<>();
			currentCombs.add(i);
			resultsForI.add(currentCombs);
			for (Set<Integer> doneSet : results) {
				Set<Integer> temp = new HashSet<>(doneSet);
				temp.add(i);
				resultsForI.add(temp);
			}
			results.addAll(resultsForI);
		}
		return results;
	}
}
