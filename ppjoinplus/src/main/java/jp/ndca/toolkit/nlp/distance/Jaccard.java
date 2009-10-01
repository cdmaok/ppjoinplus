package jp.ndca.toolkit.nlp.distance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ジャッカード係数の算出を行うクラスです
 * @author just_do_neet
 *
 */
public class Jaccard {
	public  Jaccard(){
		super();
	}

	/**
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double calc(Object[] a, Object[] b){
		int alen = a.length;
		int blen = b.length;
		Set<Object> set = new HashSet<Object>(alen + blen);
		set.addAll(Arrays.asList(a));
		set.addAll(Arrays.asList(b));

		return innerCalc(alen, blen, set.size());
	}

	/**
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double calc(List<? extends Object> a, List<? extends Object> b){
		int alen = a.size();
		int blen = b.size();
		Set<Object> set = new HashSet<Object>(alen + blen);
		set.addAll(a);
		set.addAll(b);

		return innerCalc(alen, blen, set.size());
	}


	private double innerCalc(int alen, int blen, int union){
		double intersection = Math.min(alen, blen) - (union - Math.max(alen, blen));
		if(intersection <= 0)
			return 0.0;
		return intersection / union;
	}
}
