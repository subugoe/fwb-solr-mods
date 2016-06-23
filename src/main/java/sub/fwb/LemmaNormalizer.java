package sub.fwb;

import java.util.ArrayList;
import java.util.List;
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
		//System.out.println(parensParts);
		
		for (int i = 0; i < termParts.length; i++) {
			System.out.println(termParts[i]);
		}
		
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

}
