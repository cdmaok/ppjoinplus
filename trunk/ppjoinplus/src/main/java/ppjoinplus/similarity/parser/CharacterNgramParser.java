package ppjoinplus.similarity.parser;

import java.util.ArrayList;
import java.util.List;

public class CharacterNgramParser implements NgramParser{

	@Override
	public List<String> convertToNgram(String object, int n) {
		if(object == null)
			return null;
		if(n <= 0)
			throw new IllegalArgumentException("n > 0 :" + n);
		
		List<String> result = new ArrayList<String>();
		String s = object.toString();
		for(int i = 0 ; (i + (n - 1)) < s.length() ; i++){
			String ngram = s.substring(i, i + n);
			result.add(ngram);
		}
		return result;
	}

}
