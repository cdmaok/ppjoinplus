package ppjoinplus.similarity.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.sen.StringTagger;
import net.java.sen.Token;

public class SentenseNgramParser implements NgramParser{
	private Set<String> stopwords;
	private Set<String> illegalSentenses;
	
	public SentenseNgramParser(){
		super();
		stopwords = new HashSet<String>();
		illegalSentenses = new HashSet<String>();
	}
	
	public SentenseNgramParser(String senHome){
		this();
		System.setProperty("sen.home", senHome);
	}

	@Override
	public List<String> convertToNgram(String object, int n) {
		return convertToNgram(object, n, object.length());
	}
	
	@Override
	public List<String> convertToNgram(String object, int n, int length) {
		List<String> result = new ArrayList<String>();
		String s = object.toString();
		try {
			StringTagger tagger = StringTagger.getInstance();
			Token[] token = tagger.analyze(s);
			for(int i = 0 ; (i + (n - 1)) < token.length ; i++){
				StringBuilder buf = new StringBuilder();
				boolean skip = false;
				for(int j = i ; (j - i) < n && j < token.length ; j++){
					if(stopwords.contains(token[j].getSurface())){
						skip = true;
						break;
					}
					if(illegalSentenses.contains(token[j].getPos())){
						skip = true;
						break;
					}
					buf.append(token[j].getSurface());
				}
				
				if(skip)
					continue;
				result.add(buf.toString());
			}

		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	public Set<String> getStopwords() {
		return stopwords;
	}

	public void setStopwords(Set<String> stopwords) {
		this.stopwords = stopwords;
	}

	public Set<String> getIllegalSentenses() {
		return illegalSentenses;
	}

	public void setIllegalSentenses(Set<String> illegalSentenses) {
		this.illegalSentenses = illegalSentenses;
	}
	
}
