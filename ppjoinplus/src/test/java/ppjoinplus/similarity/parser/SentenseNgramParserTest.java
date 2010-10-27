package ppjoinplus.similarity.parser;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class SentenseNgramParserTest {
	
	private String[] testCases = {
			"abcdefghijklmno",
			"あいうえおかきくけこさしすせそ",
			"今夜も生でさだまさし"
	};
	private String[] shortestTestCases = {
			"a",
			"あ"
	};
	
	private String[] stopwords = {
			"。",
			" ",
			"　",
	};
	private String[] stopwordTestCases = {
		"こんにちは。私は変人です。",
		" と　の間における天 変 地 異。"
	};
	private String[][] ngramStopwordTestCases = {
			{"こんにちは","私","は","変人","です"},
			{"と","の","間","における","天", "変", "地", "異",}
		};
	
//	private String[] illegalSentenses = {
//			"。",
//			" ",
//			"　",
//	};
	
	private String[][][] ngramTestCases = {
			//0-gram(empty)
			{},
			//1-gram
			{
				{"abcdefghijklmno"},
				{"あい", "うえ", "おかき", "くけ", "こ", "さ", "し", "す", "せ", "そ"},
				{"今夜", "も", "生", "で", "さだ", "まさし"}
			},
			//2-gram
			{
				{},
				{"あいうえ", "うえおかき", "おかきくけ", "くけこ", "こさ", "さし", "しす", "すせ", "せそ"},
				{"今夜も", "も生", "生で", "でさだ", "さだまさし"}
			},
			//3-gram
			{
				{},
				{"あいうえおかき", "うえおかきくけ", "おかきくけこ", "くけこさ", "こさし", "さしす", "しすせ", "すせそ"},
				{"今夜も生", "も生で", "生でさだ", "でさだまさし"}
			}
	};
	
	
	@Test
	public void testShortestStringConvertToNgram() throws Exception{
		SentenseNgramParser parser = new SentenseNgramParser();

		for(int ngram = 1 ; ngram < 4 ; ngram++){
			for(int i = 0 ; i < shortestTestCases.length ; i++){
				String origin = shortestTestCases[i];
				String[] after = new String[]{origin};
				{
					List<String> proceed = parser.convertToNgram(origin, ngram);
					if(ngram == 1){
						String[] buf = new String[proceed.size()];
						proceed.toArray(buf);
						
						Assert.assertTrue(Arrays.equals(after, buf));
					}else{
						Assert.assertEquals(proceed.size(), 0);
					}
				}
			}
		}
		
	}
	
	@Test
	public void testConvertToNgram() throws Exception{
		SentenseNgramParser parser = new SentenseNgramParser();

		for(int ngram = 1 ; ngram < 4 ; ngram++){
			for(int i = 0 ; i < testCases.length ; i++){
				String origin = testCases[i];
				String[] after = ngramTestCases[ngram][i];
				
				{
					List<String> proceed = parser.convertToNgram(origin, ngram);
					String[] buf = new String[proceed.size()];
					proceed.toArray(buf);
					
					Assert.assertTrue(Arrays.equals(after, buf));
				}
			}
		}
		
	}
	
	@Test
	public void testNullOrEmptyConvertToNgram() throws Exception{
		SentenseNgramParser parser = new SentenseNgramParser();

		for(int ngram = 1 ; ngram < 4 ; ngram++){
			{
				List<String> proceed = parser.convertToNgram(null, ngram);
				Assert.assertNull(proceed);
			}
			{
				List<String> proceed = parser.convertToNgram("", ngram);
				Assert.assertEquals(proceed.size() , 0);
			}
		}
	}
	
	@Test
	public void testIllegalParameterConvertToNgram() throws Exception{
		SentenseNgramParser parser = new SentenseNgramParser();

		for(int ngram = 1 ; ngram < 4 ; ngram++){
			for(int i = 0 ; i < testCases.length ; i++){
				String origin = testCases[i];
				String[] after = ngramTestCases[ngram][i];
				
				//minus-gram
				{
					try{
						List<String> proceed = parser.convertToNgram(origin, ngram * -1);
					}catch(Exception e){
						Assert.assertEquals(e.getClass(), IllegalArgumentException.class);
					}
				}
				//0-gram
				{
					try{
						List<String> proceed = parser.convertToNgram(origin, 0);
					}catch(Exception e){
						Assert.assertEquals(e.getClass(), IllegalArgumentException.class);
					}
				}
				
			}
		}
	}
	
	@Test
	public void testStopWordConvertToNgram() throws Exception{
		SentenseNgramParser parser = new SentenseNgramParser();
		Set<String> stopwords = new HashSet<String>();
		for(String s : this.stopwords){
			stopwords.add(s);
		}
		parser.setStopwords(stopwords);

		for(int i = 0 ; i < stopwordTestCases.length ; i++){
			String origin = stopwordTestCases[i];
			String[] after = ngramStopwordTestCases[i];
			
			{
				List<String> proceed = parser.convertToNgram(origin, 1);
				String[] buf = new String[proceed.size()];
				proceed.toArray(buf);
				
				Assert.assertTrue(Arrays.equals(after, buf));
			}
		}
	}
}
