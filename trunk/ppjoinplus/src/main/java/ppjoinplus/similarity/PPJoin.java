package ppjoinplus.similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.suffixfilter.NgramSuffixFilter;
import ppjoinplus.similarity.suffixfilter.SuffixFilter;
import ppjoinplus.similarity.verify.NgramVerifier;
import ppjoinplus.similarity.verify.Verifier;

/**
 * PPJoin+アルゴリズムです。
 *
 */
public class PPJoin {
	private static Log log = LogFactory.getLog(PPJoin.class);
	
	/**
	 * しきい値。1.0 >= threshold > 0.0
	 */
	private double threshold = 0.9;
	
	/**
	 * PPJoin+アルゴリズムとして動作させるかどうかのフラグ。
	 * trueの場合はPPJoin+。falseの場合はPPJoinとして動作する
	 */
	private boolean ppjoinplus = true;
	
	/**
	 * SuffixFilteringの対象文字列を、既にseek済みの文字列の後ろから行うか、文字列の先頭から行うか。
	 * trueの場合seek済みの文字からSuffixFilteringを行う。falseの場合は先頭から。
	 * PPJoin+の論文ではtrueの方のロジックが書かれているが、その場合処理速度は向上するがrecallは下がる。
	 */
	private boolean useSeekPositionOnSuffixFiltering = true;

	/**
	 * vefify処理を行うクラス
	 */
	private Verifier verifier = new NgramVerifier();

	/**
	 * SuffixFilteringを行うクラス
	 */
	private SuffixFilter suffixFilter = new NgramSuffixFilter();

	public PPJoin(){
		super();
	}
	
	public PPJoin(double threshold){
		this();
		this.threshold = threshold;
	}
	
	/**
	 * 総当たりで類似データの抽出を行います。
	 * @param dataset
	 * @return 類似文章
	 */
	public List<Item[]> joinItem(List<Item> dataset){
		Set<Object> bufs = new HashSet<Object>();//処理済のItemID
		
		List<Item[]> result = new ArrayList<Item[]>();
		if(dataset == null || dataset.size() == 0)
			return result;
		
		for(int src = 0 ; src < dataset.size() ; src++){
			Item ix = dataset.get(src);
			if(bufs.contains(ix.getId()))
				continue;
			int xlen = ix.length();
			if(xlen == 0)
				continue;
			
			List<Item> r = joinItem(ix, dataset, bufs);
			for(Item item : r){
				if(ix.getId().equals(item.getId()))
					continue;
				result.add(new Item[]{ix, item});
			}
			bufs.add(ix.getId());
			
		}
		
		return result;
	}
	
	/**
	 * データセットの中から、引数で渡されたItemと類似するデータを抽出します
	 * @param item
	 * @param dataset
	 * @return
	 */
	public List<Item> joinItem(Item ix, List<Item> dataset){
		return joinItem(ix, dataset, new HashSet<Object>());
	}
	
	/**
	 * データセットの中から、引数で渡されたItemと類似するデータを抽出します
	 * @param ix
	 * @param dataset
	 * @param buf	処理済みのItemID。この中に含まれているIDと処理ItemのIDが一致している場合は、処理対象外となります。
	 * @return
	 */
	public List<Item> joinItem(Item ix, List<Item> dataset, Set<Object> buf){
		List<Item> result = new ArrayList<Item>();
		if(dataset == null || dataset.size() == 0)
			return result;
		
		int xlen = ix.length();
		if(xlen == 0)
			return result;
		//p ← |x| − (t · |x|) + 1;
		int p = (int)(xlen - Math.ceil(threshold * xlen) + 1);

		int debugThrowPrefixPositionCounter = 0;//for debug
		int debugThrowSuffixFilteringCounter = 0;//for debug
		for(int dist = 0 ; dist < dataset.size() ; dist++){
			Item iy = dataset.get(dist);
			if(ix.getId().equals(iy.getId()))
				continue;
			if(buf.contains(iy.getId()))
				continue;
			
			int ylen = iy.length();
			if(ylen < (threshold * xlen) || xlen < (threshold * ylen))
				continue;

			//a ← ┌(t / 1 + t) * (|x| + |y|)┐;
			double a = Math.ceil((threshold / (1 + threshold)) * (xlen + ylen));

			//seek prefix position
			int prefixI = -1, prefixJ = -1;
			for(int i = 0 ; i < p && i < xlen ; i++){
				for(int j = 0 ; j < p && j < ylen; j++){
					//ubound ← 1 + min(|x| − i, |y| − j);
					int ubound = 1 + Math.min(xlen - i, ylen - j);
					if(ubound < a){
						break;
					}

					if(ix.compare(iy,i,j)){
						prefixI = i;
						prefixJ = j;
						break;
					}
				}
				if((prefixI > -1 && prefixJ > -1))
					break;
			}
			if(log.isDebugEnabled()){
				log.debug("seek prefix position:" + 
						ix.toString() + "\t" + 
						iy.toString() + "\t" + 
						prefixI + "\t" + 
						prefixJ + "\t" + 
						(!(prefixI <= -1 || prefixJ <= -1)));
				if(!(prefixI <= -1 || prefixJ <= -1))
					debugThrowPrefixPositionCounter++;
			}
			if((prefixI <= -1 || prefixJ <= -1))
				continue;
			
			//suffix filtering
			if(ppjoinplus && suffixFilter != null){
				//Hmax = |x| + |y| - 2 * ( ( t / (1 + t) ) * (|x| + |y|) ) - (i + j - 2)
				double hmax = 
					xlen + ylen - 2
					* Math.ceil((threshold / (1 + threshold)) * (xlen + ylen))
					- (prefixI + prefixJ - 2); 
				
				double h = 0;
				if(useSeekPositionOnSuffixFiltering){
					h = suffixFilter.filter(ix, iy, prefixI + 1, prefixJ + 1, ix.length(), iy.length(), hmax, 1);
				}else{
					h = suffixFilter.filter(ix, iy, 0, 0, ix.length(), iy.length(), hmax, 1);
				}
				if(log.isDebugEnabled()){
					log.debug("suffix filtering:" + 
							ix.toString() + "\t" + 
							iy.toString() + "\t" + 
							hmax + "\t" + 
							h + "\t" + 
							(!(h < hmax)));
					if(!(h < hmax))
						debugThrowSuffixFilteringCounter++;
				}
				if(h < hmax)
					continue;
			}

			//verify
			if(verifier.verify(ix, iy, prefixI, prefixJ, a)){
				result.add(iy);
			}
		}
		if(log.isDebugEnabled()){
			log.debug("throw prefix position : " + debugThrowPrefixPositionCounter);
			log.debug("throw suffix filtering : " + debugThrowSuffixFilteringCounter);
		}
		
		return result;
	}


	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public boolean isPpjoinplus() {
		return ppjoinplus;
	}

	public void setPpjoinplus(boolean ppjoinplus) {
		this.ppjoinplus = ppjoinplus;
	}

	public Verifier getVerifier() {
		return verifier;
	}

	public void setVerifier(Verifier verifier) {
		this.verifier = verifier;
	}

	public SuffixFilter getSuffixFilter() {
		return suffixFilter;
	}

	public void setSuffixFilter(SuffixFilter suffixFilter) {
		this.suffixFilter = suffixFilter;
	}

	public boolean isUseSeekPositionOnSuffixFiltering() {
		return useSeekPositionOnSuffixFiltering;
	}

	public void setUseSeekPositionOnSuffixFiltering(
			boolean useSeekPositionOnSuffixFiltering) {
		this.useSeekPositionOnSuffixFiltering = useSeekPositionOnSuffixFiltering;
	}
	
	
}
