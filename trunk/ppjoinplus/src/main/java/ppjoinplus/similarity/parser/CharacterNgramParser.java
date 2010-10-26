package ppjoinplus.similarity.parser;

import java.util.ArrayList;
import java.util.List;

public class CharacterNgramParser implements NgramParser{

	@Override
	public List<String> convertToNgram(String object, int n) {
		return convertToNgram(object, n, object.length());
	}
	
	@Override
	public List<String> convertToNgram(String object, int n, int length) {
		List<String> result = new ArrayList<String>();
		String s = object.toString();
		for(int i = 0 ; (i + (n - 1)) < s.length() && i < length ; i++){
			String ngram = s.substring(i, i + n);
			result.add(ngram);
		}
		return result;
	}

}
