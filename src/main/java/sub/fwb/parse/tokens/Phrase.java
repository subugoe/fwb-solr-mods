package sub.fwb.parse.tokens;

public class Phrase extends QueryToken {

	public Phrase(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
	}
	
	@Override
	public String getModifiedQuery() {
		return String.format("%s +(artikel:%s zitat:%s) ", escapedString, escapedString, escapedString);
	}

}
