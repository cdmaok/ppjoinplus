package ppjoinplus.similarity.verify;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.item.NgramItem;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * 渡されたデータを文字列と判断してverify処理を実施します。
 */
public class NgramVerifier implements Verifier {
	/**
	 * しきい値。1.0 >= threshold >= 0.0
	 */
	private double threshold = 0.9;
	
	/**
	 * 類似度算出アルゴリズム。simmetricsを使用 <a href="http://sourceforge.net/projects/simmetrics/" >http://sourceforge.net/projects/simmetrics/</a>
	 */
	private AbstractStringMetric metrics;

	public NgramVerifier(){
		super();
	}
	
	public NgramVerifier(double threshold){
		this();
		this.threshold = threshold;
	}
	
	public NgramVerifier(double threshold , AbstractStringMetric metrics){
		this(threshold);
		this.metrics = metrics;
	}
	
	@Override
	public boolean verify(Item itemx, Item itemy, int i, int j, double a) {
		NgramItem ix = (NgramItem)itemx;
		NgramItem iy = (NgramItem)itemy;
		double score = 0.0;
		if(i < j){
			int ubound = ix.length() - i;
			if(ubound >= a){
				score = metrics.getSimilarity(ix.getOrigin(), iy.getOrigin());
			}
		}else{
			int ubound = iy.length() - j;
			if(ubound >= a){
				score = metrics.getSimilarity(ix.getOrigin(), iy.getOrigin());
			}
		}
		
		return score >= threshold;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public AbstractStringMetric getMetrics() {
		return metrics;
	}

	public void setMetrics(AbstractStringMetric metrics) {
		this.metrics = metrics;
	}
	
	
	
}
