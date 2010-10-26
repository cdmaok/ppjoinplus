package uk.ac.shef.wit.simmetrics.tokenisers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import ppjoinplus.similarity.parser.SentenseNgramParser;

import uk.ac.shef.wit.simmetrics.wordhandlers.InterfaceTermHandler;

public class SentenseNgramTokenizer implements InterfaceTokeniser {
	private InterfaceTermHandler stopWordHandler;
	private SentenseNgramParser parser ;
	private int ngram = 2;
	
	public SentenseNgramTokenizer(){
		super();
		this.parser = new SentenseNgramParser();
	}
	
	public SentenseNgramTokenizer(int ngram){
		this();
		this.ngram = ngram;
	}
	
	public SentenseNgramTokenizer(int ngram, SentenseNgramParser parser){
		this(ngram);
		this.parser = parser;
	}

	@Override
	public String getDelimiters() {
		return "";
	}

	@Override
	public String getShortDescriptionString() {
		return "SentenseNgramTokenizer";
	}

	@Override
	public InterfaceTermHandler getStopWordHandler() {
		return stopWordHandler;
	}

	@Override
	public void setStopWordHandler(
			InterfaceTermHandler paramInterfaceTermHandler) {
		this.stopWordHandler = paramInterfaceTermHandler;

	}

	@Override
	public ArrayList<String> tokenizeToArrayList(String paramString) {
		return (ArrayList<String>) parser.convertToNgram(paramString, ngram);
	}

	@Override
	public Set<String> tokenizeToSet(String paramString) {
		return new HashSet<String>(tokenizeToArrayList(paramString));
	}

}
