package ppjoinplus.similarity.parser;

import java.util.List;

public interface NgramParser {
	public List<String> convertToNgram(String object, int n);
}
