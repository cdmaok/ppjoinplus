package ppjoinplus.similarity.parser;


import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CharacterNgramParserTest {
	private String[] testCases = {
			"abcdefghijklmno",
			"あいうえおかきくけこさしすせそ",
			"今夜も生でさだまさし"
	};
	private String[] shortestTestCases = {
			"a",
			"あ"
	};
	
	private String[][][] ngramTestCases = {
			//0-gram(empty)
			{},
			//1-gram
			{
				{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o"},
				{"あ","い","う","え","お","か","き","く","け","こ","さ","し","す","せ","そ"},
				{"今","夜","も","生","で","さ","だ","ま","さ","し"}
			},
			//2-gram
			{
				{"ab","bc","cd","de","ef","fg","gh","hi","ij","jk","kl","lm","mn","no"},
				{"あい","いう","うえ","えお","おか","かき","きく","くけ","けこ","こさ","さし","しす","すせ","せそ"},
				{"今夜","夜も","も生","生で","でさ","さだ","だま","まさ","さし"}
			},
			//3-gram
			{
				{"abc","bcd","cde","def","efg","fgh","ghi","hij","ijk","jkl","klm","lmn","mno"},
				{"あいう","いうえ","うえお","えおか","おかき","かきく","きくけ","くけこ","けこさ","こさし","さしす","しすせ","すせそ"},
				{"今夜も","夜も生","も生で","生でさ","でさだ","さだま","だまさ","まさし"}
			}
	};
	
	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void testShortestStringConvertToNgram() throws Exception{
		CharacterNgramParser parser = new CharacterNgramParser();

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
		CharacterNgramParser parser = new CharacterNgramParser();

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
		CharacterNgramParser parser = new CharacterNgramParser();

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
		CharacterNgramParser parser = new CharacterNgramParser();

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
}
