package jp.ndca.toolkit.similarity.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteParser implements Parser<ByteArray> {

	@Override
	public List<ByteArray> convertToNgram(ByteArray object, int n) {
		return convertToNgram(object, n, object.length());
	}
	
	@Override
	public List<ByteArray> convertToNgram(ByteArray object, int n, int length) {
		List<ByteArray> result = new ArrayList<ByteArray>();
		for(int i = 0 ; (i + (n - 1)) < object.length() && i < length ; i++){
			result.add(new ByteArray(Arrays.copyOfRange(object.getBytes(), i, i + n)));
		}
		return result;
	}

}
