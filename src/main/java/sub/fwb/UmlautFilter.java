package sub.fwb;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class UmlautFilter extends TokenFilter {
	private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
	private final PositionIncrementAttribute posIncrAttr = addAttribute(PositionIncrementAttribute.class);
	private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
	private boolean finished = false;
	private int startOffset = 0;
	private int endOffset = 0;
	private int posIncr = 0;
	private Queue<String> terms;

	public UmlautFilter(TokenStream input) {
		super(input);
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
		this.terms = new LinkedList<String>();

	}

	@Override
	public boolean incrementToken() throws IOException {
		while (!finished) {
			while (terms.size() > 0) {
				String buffer = terms.poll();

				termAttr.copyBuffer(buffer.toCharArray(), 0, buffer.length());
				offsetAttr.setOffset(startOffset, endOffset);
				posIncrAttr.setPositionIncrement(posIncr);

				posIncr = 0;
				return true;
			}

			if (input.incrementToken()) {
				String currentTerm = termAttr.toString();
				startOffset = offsetAttr.startOffset();
				endOffset = offsetAttr.endOffset();
				posIncr = 1;

				UmlautWordMapper mapper = new UmlautWordMapper();
				List<String> mappedWords = mapper.createMappings(currentTerm);

				for (String mappedWord : mappedWords) {
					terms.add(mappedWord);
				}
			} else {
				finished = true;
			}
		}
		return false;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		finished = false;
		terms.clear();
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
	}

}
