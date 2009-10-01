package jp.ndca.toolkit.similarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ndca.toolkit.similarity.item.Item;
import jp.ndca.toolkit.similarity.parser.ByteArray;
import jp.ndca.toolkit.similarity.parser.ByteParser;
import jp.ndca.toolkit.similarity.parser.WordParser;
import jp.ndca.toolkit.similarity.verify.ByteVerifier;
import jp.ndca.toolkit.similarity.verify.WordVerifier;

import org.junit.Test;


public class PPJoinTest {
//	private String testFile = "src/test/java/jp/ndca/toolkit/similarity/data_test.txt";
	private String testFile = "src/test/java/jp/ndca/toolkit/similarity/data500.txt";
	private int repeat = 1;
	private int maxdepth = 3;
	
	@Test
	public void testJoinForText() throws IOException{
		System.out.println("----- testJoinForText -----");
		for(double threshold : new double[]{0.9,0.8,0.7}){
			System.out.println("----- threshold : " + String.valueOf(threshold));
			for(int i = 0 ; i < repeat ; i++){
				List<String> items = new ArrayList<String>();
				
				//initialize
				BufferedReader reader = 
					new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
				while(reader.ready()){
					String word = reader.readLine();
					if(word.startsWith("{"))
						word = parseLine(word);
					if(word == null || word.trim().isEmpty() || "null".equalsIgnoreCase(word))
						continue;
					items.add(word.trim());
				}
				reader.close();
				
				System.out.println("----- \tdataset size : " + String.valueOf(items.size()));
				
				//PPJoin
				PPJoin<String> joiner = new PPJoin<String>(2, threshold);
				joiner.setPpjoinplus(true);
				joiner.setMaxdepth(maxdepth);
				
				joiner.setParser(new WordParser());
				joiner.setVerifier(new WordVerifier<String>(threshold, 0));
				
				List<Item<String>[]> r = joiner.join(items);
				System.out.println("----- \tresult : " + String.valueOf(r.size()));
				for(Item<String>[] results : r){
					System.out.println(results[0].getId() + "\t" + results[0].getItem());
					System.out.println("\t" + results[1].getId() + "\t" + results[1].getItem());
					System.out.println();
				}
			}
		}
	}
	
	@Test
	public void testJoinForByte() throws IOException{
		System.out.println("----- testJoinForByte -----");
		for(double threshold : new double[]{0.9,0.8,0.7}){
			System.out.println("----- threshold : " + String.valueOf(threshold));
			for(int i = 0 ; i < repeat ; i++){
				List<ByteArray> items = new ArrayList<ByteArray>();
				
				//initialize
				BufferedReader reader = 
					new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
				while(reader.ready()){
					String word = reader.readLine();
					if(word.startsWith("{"))
						word = parseLine(word);
					if(word == null || word.trim().isEmpty() || "null".equalsIgnoreCase(word))
						continue;
					items.add(new ByteArray(word.trim().getBytes("UTF-8")));
				}
				reader.close();

				System.out.println("----- \tdataset size : " + String.valueOf(items.size()));
				
				//PPJoin
				PPJoin<ByteArray> joiner = new PPJoin<ByteArray>(2, threshold);
				joiner.setPpjoinplus(true);
				joiner.setMaxdepth(maxdepth);
				
				joiner.setParser(new ByteParser());
				joiner.setVerifier(new ByteVerifier(threshold));
				
				List<Item<ByteArray>[]> r = joiner.join(items);
				System.out.println("----- \tresult : " + String.valueOf(r.size()));
//				for(Item<ByteArray>[] results : r){
//					System.out.println(results[0].getId() + "\t" + results[0].getItem());
//					System.out.println("\t" + results[1].getId() + "\t" + results[1].getItem());
//					System.out.println();
//				}
			}
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
