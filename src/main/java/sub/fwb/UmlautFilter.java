package sub.fwb;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class UmlautFilter extends TokenFilter {
	private CharTermAttribute charTermAttr;
	private PositionIncrementAttribute posIncAttr;
	private Queue<char[]> terms;

	protected UmlautFilter(TokenStream ts) {
		super(ts);
		this.charTermAttr = addAttribute(CharTermAttribute.class);
		this.posIncAttr = addAttribute(PositionIncrementAttribute.class);
		this.terms = new LinkedList<char[]>();
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!terms.isEmpty()) {
			char[] buffer = terms.poll();
			charTermAttr.setEmpty();
			charTermAttr.copyBuffer(buffer, 0, buffer.length);
			posIncAttr.setPositionIncrement(0);
			return true;
		}

		if (!input.incrementToken()) {
			return false;
		} else {
			String currentTerm = String.valueOf(charTermAttr.buffer());
			
			UmlautWordMapper mapper = new UmlautWordMapper();
			List<String> mappedWords = mapper.createMappings(currentTerm);
			
			for (String mappedWord : mappedWords) {
				terms.add(mappedWord.toCharArray());
			}
			// we return true and leave the original token unchanged
			return true;
		}
	}
}
