package ppjoinplus.similarity.item;

import java.util.List;

import ppjoinplus.similarity.parser.NgramParser;

public class NgramItem extends Item {
	private String[] ngram;
	private String origin;

	public NgramItem(Object id, String origin, int ngram, NgramParser parser){
		super();
		this.id = id;
		this.origin = origin;
		
		List<String> ngrams = parser.convertToNgram(origin, ngram);
		this.ngram = new String[ngrams.size()];
		ngrams.toArray(this.ngram);
	}
	
	@Override
	public int length() {
		return ngram.length;
	}

	@Override
	public boolean compare(Item item , int xi, int yi){
		NgramItem yitem = (NgramItem)item;
		return yitem.ngram[yi].equals(this.ngram[xi]);
	}

	@Override
	public String toString(){
		StringBuilder buf = new StringBuilder();
		for(String s : ngram)
			buf.append(s).append("/");
		return id + " _ " + buf.toString();
	}
	
	public String[] getNgram(){
		return this.ngram;
	}
	
	public String getOrigin(){
		return this.origin;
	}
	
	public String get(int pos){
		return this.ngram[pos];
	}
}
