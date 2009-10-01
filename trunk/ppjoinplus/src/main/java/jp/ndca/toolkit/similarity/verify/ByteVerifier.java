package jp.ndca.toolkit.similarity.verify;

import jp.ndca.toolkit.nlp.distance.Jaccard;
import jp.ndca.toolkit.similarity.item.Item;
import jp.ndca.toolkit.similarity.parser.ByteArray;

/**
 * 渡されたデータをbyte配列と判断してverify処理を実施します。
 * @author just_do_neet
 *
 * @param <T>
 */
public class ByteVerifier implements Verifier<ByteArray> {
	/**
	 * しきい値。1.0 >= threshold >= 0.0
	 */
	private double threshold = 0.9;
	
	public ByteVerifier(){
		super();
	}
	
	public ByteVerifier(double threshold){
		this();
		this.threshold = threshold;
	}

	
	@Override
	public boolean verify(Item<ByteArray> ix, Item<ByteArray> iy, int i, int j, double a) {
		boolean isSimilar = false;
		Jaccard jaccard = new Jaccard();
		if(i < j){
			int ubound = ix.getNgrams().size() - i;
			if(ubound >= a){
				isSimilar = jaccard.calc(ix.getNgrams(), iy.getNgrams()) >= threshold;
			}
		}else{
			int ubound = iy.getNgrams().size() - j;
			if(ubound >= a){
				isSimilar = jaccard.calc(ix.getNgrams(), iy.getNgrams()) >= threshold;
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
	
}
