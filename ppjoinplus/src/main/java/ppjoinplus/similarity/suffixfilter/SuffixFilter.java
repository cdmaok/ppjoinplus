package ppjoinplus.similarity.suffixfilter;

import ppjoinplus.similarity.item.Item;

/**
 * PPJoin+のSuffixFiltering処理を実施します。
 * @author moaikids
 *
 */
public abstract class SuffixFilter {
	protected int maxdepth;
	
	public int getMaxdepth() {
		return maxdepth;
	}

	public void setMaxdepth(int maxdepth) {
		this.maxdepth = maxdepth;
	}


	public abstract double filter(Item x, Item y, int xstart, int ystart, int xend, int yend, double hmax, int depth);
}
