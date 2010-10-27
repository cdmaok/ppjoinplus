package ppjoinplus.similarity.suffixfilter;

import java.util.Arrays;

import ppjoinplus.similarity.item.Item;
import ppjoinplus.similarity.item.NgramItem;

public class NgramSuffixFilter extends SuffixFilter {
	private static final int MINIMUM_MAXDEPTH = 3;
	private int maxdepth = MINIMUM_MAXDEPTH;
	
	public NgramSuffixFilter(){
		super();
	}
	
	public NgramSuffixFilter(int maxdepth){
		this();
		if(maxdepth < MINIMUM_MAXDEPTH)
			throw new IllegalArgumentException("maxdepth >= " + MINIMUM_MAXDEPTH + " : " + maxdepth);
		this.maxdepth = maxdepth;
	}

	@Override
	public double filter(Item x, Item y, int xstart, int ystart, int xend, int yend, double hmax, int depth) {
		NgramItem itemx = (NgramItem)x;
		NgramItem itemy = (NgramItem)y;
		
		return suffixFilter(
				Arrays.copyOfRange(itemx.getNgram(), xstart, xend), 
				Arrays.copyOfRange(itemy.getNgram(), ystart, yend), 
				hmax, 
				depth);
	}

	/**
	 * Suffix Filtering処理を行います。
	 * @param wx
	 * @param wy
	 * @param hmax
	 * @param depth
	 * @return
	 */
	private double suffixFilter(String[] wx, String[] wy, double hmax, int depth) {
		if(depth > maxdepth)
			return Math.abs(wx.length - wy.length);
		int xlen = wx.length;
		int ylen = wy.length;
		if(xlen <= 0 || ylen <= 0)
			return hmax + 1;
		
		int mid = (int)Math.ceil(ylen / 2);
		String w = wy[mid];
		double o = (hmax - Math.abs(xlen - ylen)) / 2;
		int ol = xlen < ylen ? 1 : 0;
		int or = xlen < ylen ? 0 : 1;
		
		//partitioning(y)
		Partition yp = partition(wy, w, mid, mid);
		//partitioning(x)
		int xpl = (int)(mid - o - Math.abs(xlen - ylen) * ol);
		int xpr = (int)(mid + o + Math.abs(xlen - ylen) * or);
		Partition xp = 
			this.partition(
					wx, 
					w, 
					xpl < 0 ? 0 : xpl, 
					xpr >= wx.length ? wx.length - 1 : xpr);
		int diff = xp.getDiff() > 0 ||  yp.getDiff() > 0 ? 1 : 0 ;
		
		if(yp.getF() == 0  || xp.getF() == 0)
			return hmax + 1;
		
		//examination
		double h = 
			Math.abs(xp.getSl().length - yp.getSl().length) + 
			Math.abs(xp.getSr().length - yp.getSr().length) + 
			diff;
		if(h > hmax){
			return h;
		}
		
		double hlmax = hmax - Math.abs(xp.getSr().length - yp.getSr().length) - diff;
		double hl = 
			this.suffixFilter(
					xp.getSl(), 
					yp.getSl(), 
					hlmax < 0.0 ? 0.0 : hlmax, 
					depth + 1);
		h = hl + Math.abs(xp.getSr().length - yp.getSr().length) + diff;
		
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
	
	private Partition partition(String[] s, String w, int l, int r) {
		String[] sl = new String[]{};
		String[] sr = new String[]{};
		
		if(l > r || r < l)
			return new Partition(sl, sr, 0, 1);
		
		int p = -1;
		if(l == r){
			p = l;
		}else{
			for(int i = l ; i < r ; i++){
				String item = s[i];
				if(item.equals(w)){
					p = i;
					break;
				}
			}
		}
		if(p <= 0)
			return new Partition(sl, sr, 0, 1);
		
		int diff = 0;
		sl = Arrays.copyOfRange(s, 0, p -1);
		if(s[p].equals(w)){
			sr = Arrays.copyOfRange(s, p + 1, s.length);
			diff = 0;
		}else{
			sr = Arrays.copyOfRange(s, p , s.length);
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
		private String[] sl;
		private String[] sr;
		private int f;
		private int diff;
		
		public Partition(){
			super();
		}
		
		public Partition(String[] sl, String[] sr, int f, int diff){
			this.sl = sl;
			this.sr = sr;
			this.f = f;
			this.diff = diff;
		}

		public String[] getSl() {
			return sl;
		}

		public void setSl(String[] sl) {
			this.sl = sl;
		}

		public String[] getSr() {
			return sr;
		}

		public void setSr(String[] sr) {
			this.sr = sr;
		}

		public int getF() {
			return f;
		}
		public void setF(int f) {
			this.f = f;
		}
		public int getDiff() {
			return diff;
		}
		public void setDiff(int diff) {
			this.diff = diff;
		}
		
		@Override
		public String toString(){
			return this.sl.length + "\n" + sr.length + "\n" + f + "\n" + diff;
		}
		
	}
}
