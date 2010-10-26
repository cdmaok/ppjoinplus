package uk.ac.shef.wit.simmetrics.tokenisers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ppjoinplus.similarity.parser.CharacterNgramParser;

import uk.ac.shef.wit.simmetrics.wordhandlers.InterfaceTermHandler;

public class CharacterNgramTokenizer implements InterfaceTokeniser {
	private InterfaceTermHandler stopWordHandler;
	private CharacterNgramParser parser ;
	private int ngram = 2;
	
	public CharacterNgramTokenizer(){
		super();
		this.parser = new CharacterNgramParser();
	}
	
	public CharacterNgramTokenizer(int ngram){
		this();
		this.ngram = ngram;
	}
	
	public CharacterNgramTokenizer(int ngram, CharacterNgramParser parser){
		this(ngram);
		this.parser = parser;
	}

	@Override
	public String getDelimiters() {
		return "";
	}

	@Override
	public String getShortDescriptionString() {
		return "CharacterNgramTokenizer";
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
