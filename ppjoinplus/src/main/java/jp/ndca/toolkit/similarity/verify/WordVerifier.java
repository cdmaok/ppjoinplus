package jp.ndca.toolkit.similarity.verify;

import jp.ndca.toolkit.nlp.distance.EditDistance;
import jp.ndca.toolkit.similarity.item.Item;

/**
 * 渡されたデータを文字列と判断してverify処理を実施します。
 * @author just_do_neet
 *
 * @param <T>
 */
public class WordVerifier<T> implements Verifier<T> {
	/**
	 * しきい値。1.0 >= threshold >= 0.0
	 */
	private double threshold = 0.9;
	
	/**
	 * Edit Distanceの評価を行う下限文字数。この値より短い文字列は評価しない
	 */
	private int baselen = 10;

	public WordVerifier(){
		super();
	}
	
	public WordVerifier(double threshold){
		this();
		this.threshold = threshold;
	}
	
	public WordVerifier(int baselen){
		this();
		this.baselen = baselen;
	}
	
	public WordVerifier(double threshold, int baselen){
		this();
		this.threshold = threshold;
		this.baselen = baselen;
	}	
	
	
	@Override
	public boolean verify(Item<T> ix, Item<T> iy, int i, int j, double a) {
		boolean isSimilar = false;
		EditDistance distance = new EditDistance();
		if(i < j){
			int ubound = ix.getNgrams().size() - i;
			if(ubound >= a){
				isSimilar = distance.isSimilar(ix.getItem().toString(), iy.getItem().toString(), threshold, baselen);
			}
		}else{
			int ubound = iy.getNgrams().size() - j;
			if(ubound >= a){
				isSimilar = distance.isSimilar(ix.getItem().toString(), iy.getItem().toString(), threshold, baselen);
			}
		}
		return isSimilar;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getBaselen() {
		return baselen;
	}

	public void setBaselen(int baselen) {
		this.baselen = baselen;
	}
	
}
