package ppjoinplus.similarity;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.item.NgramItem;
import ppjoinplus.similarity.parser.CharacterNgramParser;
import ppjoinplus.similarity.parser.NgramParser;
import ppjoinplus.similarity.parser.SentenseNgramParser;
import ppjoinplus.similarity.suffixfilter.NgramSuffixFilter;
import ppjoinplus.similarity.verify.NgramVerifier;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.tokenisers.CharacterNgramTokenizer;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2;

public class NgramPPJoinTest {
	private String testFile;
	private double threshold = 0.9;
	private boolean ppjoinplus = true;

	@Before
	public void setUp() throws Exception {
		this.testFile = System.getProperty("file");
		this.threshold = Double.parseDouble(System.getProperty("threshold"));
		this.ppjoinplus = Boolean.parseBoolean(System.getProperty("ppjoinplus"));
	}

	@Test
	public void testCharacterNgramJoinItem() throws IOException{
		int ngram = 2;
		PPJoin ppjoin = new PPJoin();
		ppjoin.setPpjoinplus(ppjoinplus);
		ppjoin.setMaxdepth(3);
		ppjoin.setThreshold(threshold);
//		ppjoin.setVerifier(new NgramVerifier(threshold, new JaccardSimilarity(new CharacterNgramTokenizer(ngram))));//TODO
//		ppjoin.setVerifier(new NgramVerifier(threshold, new JaccardSimilarity(new SentenseNgramTokenizer(ngram))));
		ppjoin.setVerifier(new NgramVerifier(threshold, new Levenshtein()));
//		ppjoin.setVerifier(new NgramVerifier(threshold, new JaroWinkler()));
		ppjoin.setSuffixFilter(new NgramSuffixFilter(3));
		
		//initialize
		List<Item> items = new ArrayList<Item>();
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
		int count = 0;
		NgramParser parser = new CharacterNgramParser();
//		NgramParser parser = new SentenseNgramParser("/usr/local/sen");
		while(reader.ready()){
			String word = reader.readLine();
			if(word == null || word.trim().isEmpty())
				continue;
			items.add(new NgramItem(count++, word.trim(), ngram, parser));
		}
		reader.close();
		
		System.out.println("----- \tdata file : " + testFile);
		System.out.println("----- \tdataset size : " + String.valueOf(items.size()));
		System.out.println("----- \tthreshold : " + String.valueOf(threshold));
		System.out.println("----- \tppjoinplus : " + String.valueOf(ppjoinplus));
		
		
		
		List<Item[]> r = ppjoin.joinItem(items);
		System.out.println("----- \tresult : " + String.valueOf(r.size()));
		for(Item[] results : r){
			System.out.println(results[0].toString());
			System.out.println(results[1].toString());
			System.out.println();
		}
	}
	
	private String parseLine(String line){
		if(line == null)
			return null;
		String[] splits = line.replaceAll("\"", "").replaceAll("\\\\r", "").replaceAll("\\\\n", "").replaceAll("\\\\", "").split(",");
		for(String s : splits){
			if(s.startsWith("description:")){
				return s.substring("description:".length());
			}
		}
		return null;
	}
}
