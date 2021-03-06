package ppjoinplus.similarity;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.item.NgramItem;
import ppjoinplus.similarity.parser.CharacterNgramParser;
import ppjoinplus.similarity.parser.NgramParser;
import ppjoinplus.similarity.verify.NgramVerifier;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.OverlapCoefficient;
import uk.ac.shef.wit.simmetrics.tokenisers.CharacterNgramTokenizer;

public class NgramSimpleJoinTest {
	private static final Log log = LogFactory.getLog(NgramSimpleJoinTest.class);
	private String[] testFiles = {
			"src/test/java/ascii_test.txt", 
			"src/test/java/string_test.txt", 
//			"src/test/java/sada.txt", 
	};
	private double[] thresholds = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
	private int[] ngrams = {1,2,3};

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCharacterNgramJoinItem() throws IOException{
		for(String file : testFiles){
			List<String> lines = readFile(file);
			
			for(int ngram : ngrams){
				for(AbstractStringMetric metrics :
					new AbstractStringMetric[]{
						new Levenshtein(), 
						new JaroWinkler(), 
						new JaccardSimilarity(new CharacterNgramTokenizer(ngram, new CharacterNgramParser())),
						new CosineSimilarity(new CharacterNgramTokenizer(ngram, new CharacterNgramParser())),
//						new OverlapCoefficient(new CharacterNgramTokenizer(ngram, new CharacterNgramParser())),
						}){
					NgramParser parser = new CharacterNgramParser();
					List<Item> items = new ArrayList<Item>();
					int count = 1;
					for(String s : lines){
						items.add(new NgramItem(count++, s.trim(), ngram, parser));
					}
					
					for(int i = 0 ; i < thresholds.length ; i++){
						double threshold = thresholds[i];
						
						long start = System.currentTimeMillis();
						SimpleJoin ppjoin = new SimpleJoin();
						ppjoin.setThreshold(threshold);
						ppjoin.setVerifier(new NgramVerifier(threshold, metrics));

						log.info("do start.");
						log.info("----- \tmetrics : " + metrics.getClass().getName());
						log.info("----- \tdata file : " + file);
						log.info("----- \tdataset size : " + String.valueOf(items.size()));
						log.info("----- \tngram : " + String.valueOf(ngram));
						log.info("----- \tthreshold : " + String.valueOf(threshold));
					
						List<Item[]> r = ppjoin.joinItem(items);
						log.info("----- \tresult : " + String.valueOf(r.size()));
						if(log.isDebugEnabled()){
							for(Item[] results : r){
								log.debug(results[0].toString());
								log.debug(results[1].toString());
								log.debug("");
							}
						}
							
						log.info("process time :" + (System.currentTimeMillis() - start) + "ms");
						log.info("end.");
					}
					
				}
			}
		}
		
	}
	
	private List<String> readFile(String file) throws IOException{
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while(reader.ready()){
				String line = reader.readLine();
				if(line == null)
					continue;
				result.add(line.trim());
			}
		}finally{
			if(reader != null)
				reader.close();
		}
		return result;
	}
}
