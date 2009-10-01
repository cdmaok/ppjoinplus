package jp.ndca.toolkit.similarity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.ndca.toolkit.similarity.item.Item;
import jp.ndca.toolkit.similarity.parser.Parser;
import jp.ndca.toolkit.similarity.parser.WordParser;
import jp.ndca.toolkit.similarity.verify.Verifier;
import jp.ndca.toolkit.similarity.verify.WordVerifier;

/**
 * PPJoin+アルゴリズムです。
 * @author just_do_neet
 *
 */
public class PPJoin<T> {
	private static Log log = LogFactory.getLog(PPJoin.class);
	
	/**
	 * しきい値。1.0 >= threshold >= 0.0
	 */
	private double threshold = 0.9;
	/**
	 * n-gramの単位
	 */
	private int ngram = 2;
	/**
	 * SuffixFilterの再帰回数上限
	 */
	private int maxdepth = 3;
	
	/**
	 * PPJoin+アルゴリズムとして動作させるかどうかのフラグ。
	 * trueの場合はPPJoin+。falseの場合はPPJoinとして動作する
	 */
	private boolean ppjoinplus = true;
	
	/**
	 * データを分割するparser。デフォルトでは対象データを文字列としてngramに分割する
	 */
	@SuppressWarnings("unchecked")
	private Parser parser = new WordParser();
	/**
	 * vefify処理を行うクラス
	 */
	private Verifier<T> verifier = new WordVerifier<T>();

	public PPJoin(){
		super();
	}
	
	public PPJoin(int ngram, double threshold){
		this();
		this.ngram = ngram;
		this.threshold = threshold;
	}
	/**
	 * 類似文章の抽出を行います。
	 * @param dataset
	 * @return	類似文書
	 */
	public List<Item<T>[]> join(List<T> dataset){
		return this.joinItem( this.preprocess(dataset) );
	}
	
	/**
	 * データセットをitemクラスに置き換える処理です。Similarity Joinの比較処理前に実施します。
	 * @param dataset
	 * @return
	 */
	public List<Item<T>> preprocess(List<T> dataset){
		List<Item<T>> wordset = new ArrayList<Item<T>>();
		int wc = 0;
		for(T o : dataset){
			wordset.add(this.convertItem(wc, o));
			wc++;
		}
		return wordset;
	}

	/**
	 * データをItemインスタンスに変換します。
	 * @param id
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Item<T> convertItem(Object id, T o){
		return new Item<T>(id, o, parser.convertToNgram(o, ngram));
	}	
	
	/**
	 * 総当たりで類似データの抽出を行います。
	 * @param dataset
	 * @return 類似文章
	 */
	@SuppressWarnings("unchecked")
	public List<Item<T>[]> joinItem(List<Item<T>> dataset){
		Set<Object> bufs = new HashSet<Object>();//処理済のItemID
		
		List<Item<T>[]> result = new ArrayList<Item<T>[]>();
		if(dataset == null || dataset.size() == 0)
			return result;
		
		for(int src = 0 ; src < dataset.size() ; src++){
			Item<T> ix = dataset.get(src);
			if(bufs.contains(ix.getId()))
				continue;
			int xlen = ix.getNgrams().size();
			if(xlen == 0)
				continue;
			
			List<Item<T>> r = joinItem(ix, dataset, bufs);
			for(Item<T> item : r){
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
	public List<Item<T>> joinItem(Item<T> ix, List<Item<T>> dataset){
		return joinItem(ix, dataset, new HashSet<Object>());
	}
	
	/**
	 * データセットの中から、引数で渡されたItemと類似するデータを抽出します
	 * @param ix
	 * @param dataset
	 * @param buf
	 * @return
	 */
	public List<Item<T>> joinItem(Item<T> ix, List<Item<T>> dataset, Set<Object> buf){
		List<Item<T>> result = new ArrayList<Item<T>>();
		if(dataset == null || dataset.size() == 0)
			return result;
		
		int xlen = ix.getNgrams().size();
		if(xlen == 0)
			return result;
		//p ← |x| − (t · |x|) + 1;
		int p = (int)(xlen - (threshold * xlen) + 1);

		for(int dist = 0 ; dist < dataset.size() ; dist++){
			Item<T> iy = dataset.get(dist);
			if(ix.getId().equals(iy.getId()))
				continue;
			if(buf.contains(iy.getId()))
				continue;
			
			int ylen = iy.getNgrams().size();
			if(ylen < (threshold * xlen) || xlen < (threshold * ylen))
				continue;

			//a ← (t / 1 + t) * (|x| + |y|);
			double a = (threshold / (1 + threshold)) * (xlen + ylen);

			//seek prefix position
			int prefixI = -1, prefixJ = -1;
			boolean finish = false;
			for(int i = 0 ; i < p ; i++){
				for(int j = 0 ; j < p ; j++){
					//ubound ← 1 + min(|x| − i, |y| − j);
					int ubound = 1 + Math.min(xlen - i, ylen - j);
					if(ubound < a){
						finish = true;
						break;
					}

					if(ix.getNgrams().get(i).equals(iy.getNgrams().get(j))){
						prefixI = i;
						prefixJ = j;
						break;
					}
				}
				if(finish || (prefixI > -1 && prefixJ > -1))
					break;
			}
			if(finish || (prefixI <= -1 || prefixJ <= -1))
				continue;
			
			//suffix filtering
			if(ppjoinplus){
				//Hmax = |x| + |y| - 2 * ( ( t / (1 + t) ) * (|x| + |y|) ) - (i + j - 2)
				double hmax = 
					xlen + ylen - 2
					* ((threshold / (1 + threshold)) * (xlen + ylen))
					- (prefixI + prefixJ - 2); 
				double h = 
					this.suffixFilter(
							ix.getNgrams().subList(prefixI + 1, ix.getNgrams().size()), 
							iy.getNgrams().subList(prefixJ + 1, iy.getNgrams().size()), 
							hmax, 
							1);
				if(h > hmax)
					continue;
			}

			//verify
			if(verifier.verify(ix, iy, prefixI, prefixJ, a)){
				result.add(iy);
			}
		}
		
		return result;
	}

	/**
	 * Suffix Filtering処理を行います。
	 * @param wx
	 * @param wy
	 * @param hmax
	 * @param depth
	 * @return
	 */
	private double suffixFilter(List<T> wx, List<T> wy, double hmax, int depth) {
		if(depth >= maxdepth)
			return Math.abs(wx.size() - wy.size());
		int xlen = wx.size();
		int ylen = wy.size();
		if(xlen <= 0 || ylen <= 0)
			return hmax + 1;
		
		int mid = ylen / 2;
		T w = wy.get(mid);
		double o = (hmax - Math.abs(xlen - ylen)) / 2;
		int ol = xlen < ylen ? 1 : 0;
		int or = xlen < ylen ? 0 : 1;
		
		//partitioning(y)
		Partition yp = 
			this.partition(wy, w, mid, mid);
		//partitioning(x)
		int xpl = (int)(mid - o - Math.abs(xlen - ylen) * ol);
		int xpr = (int)(mid + o + Math.abs(xlen - ylen) * or);
		Partition xp = 
			this.partition(
					wx, 
					w, 
					xpl < 0 ? 0 : xpl, 
					xpr >= wx.size() ? wx.size() - 1 : xpr);
		int diff = xp.getDiff() > 0 ||  yp.getDiff() > 0 ? 1 : 0 ;
		
		if(yp.getW() == 0  || xp.getW() == 0)
			return hmax + 1;
		
		//examination
		double h = 
			Math.abs(xp.getSl().size() - yp.getSl().size()) + 
			Math.abs(xp.getSr().size() - yp.getSr().size()) + 
			diff;
		if(h > hmax){
			return h;
		}
		
		double hlmax = hmax - Math.abs(xp.getSr().size() - yp.getSr().size());
		double hl = 
			this.suffixFilter(
					xp.getSl(), 
					yp.getSl(), 
					hlmax < 0.0 ? 0.0 : hlmax, 
					depth + 1);
		h = hl + Math.abs(xp.getSl().size() - yp.getSl().size()) + diff;
		
		if(h <= hmax){
			double hrmax = hmax - hl - diff;
			double hr = 
				this.suffixFilter(
						xp.getSr(), 
						yp.getSr(), 
						hrmax < 0.0 ? 0.0 : hrmax, 
						depth + 1);
			return hl + hr + diff;
		}else{
			return h;
		}
 	}
	
	private Partition partition(List<T> s, T w, int l, int r) {
		List<T> sl = new ArrayList<T>();
		List<T> sr = new ArrayList<T>();
		
		if(l > r || r < l)
			return new Partition(sl, sr, 0, 1);
		
		int p = -1;
		if(l == r){
			p = l;
		}else{
			for(int i = l ; i < r ; i++){
				T item = s.get(i);
				if(item.equals(w)){
					p = i;
					break;
				}
			}
		}
		if(p <= 0)
			return new Partition(sl, sr, 0, 1);
		
		int diff = 0;
		sl = s.subList(0, p -1);
		if(s.get(p).equals(w)){
			sr = s.subList(p + 1, s.size());
		}else{
			sr = s.subList(p , s.size());
			diff = 1;
		}

		return new Partition(sl, sr, 1, diff);
	}
	
	/**
	 * Partitionの値を表現
	 * @author moaikids
	 *
	 */
	public class Partition{
		private List<T> sl;
		private List<T> sr;
		private int w;
		private int diff;
		
		public Partition(){
			super();
		}
		
		public Partition(List<T> sl, List<T> sr, int w, int diff){
			this.sl = sl;
			this.sr = sr;
			this.w = w;
			this.diff = diff;
		}
		public List<T> getSl() {
			return sl;
		}
		public void setSl(List<T> sl) {
			this.sl = sl;
		}
		public List<T> getSr() {
			return sr;
		}
		public void setSr(List<T> sr) {
			this.sr = sr;
		}
		public int getW() {
			return w;
		}
		public void setW(int w) {
			this.w = w;
		}
		public int getDiff() {
			return diff;
		}
		public void setDiff(int diff) {
			this.diff = diff;
		}
		
		@Override
		public String toString(){
			return this.sl.toString() + "\n" + sr.toString() + "\n" + w + "\n" + diff;
		}
		
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getNgram() {
		return ngram;
	}

	public void setNgram(int ngram) {
		this.ngram = ngram;
	}
	
	public boolean isPpjoinplus() {
		return ppjoinplus;
	}

	public void setPpjoinplus(boolean ppjoinplus) {
		this.ppjoinplus = ppjoinplus;
	}

	public int getMaxdepth() {
		return maxdepth;
	}

	public void setMaxdepth(int maxdepth) {
		this.maxdepth = maxdepth;
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Verifier<T> getVerifier() {
		return verifier;
	}

	public void setVerifier(Verifier<T> verifier) {
		this.verifier = verifier;
	}
	
}
