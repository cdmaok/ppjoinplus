package ppjoinplus.similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.verify.NgramVerifier;
import ppjoinplus.similarity.verify.Verifier;

/**
 * 総当りで類似度検索をします。
 *
 */
public class SimpleJoin {
	private static Log log = LogFactory.getLog(SimpleJoin.class);
	
	/**
	 * しきい値。1.0 >= threshold > 0.0
	 */
	private double threshold = 0.9;
	
	/**
	 * vefify処理を行うクラス
	 */
	private Verifier verifier = new NgramVerifier();

	public SimpleJoin(){
		super();
	}
	
	public SimpleJoin(double threshold){
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
		
		for(int dist = 0 ; dist < dataset.size() ; dist++){
			Item iy = dataset.get(dist);
			if(ix.getId().equals(iy.getId()))
				continue;
			if(buf.contains(iy.getId()))
				continue;
			
			//verify
			if(verifier.verify(ix, iy, 0, 0, 0)){
				result.add(iy);
			}
		}
		
		return result;
	}


	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public Verifier getVerifier() {
		return verifier;
	}

	public void setVerifier(Verifier verifier) {
		this.verifier = verifier;
	}
	
}
