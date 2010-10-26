package ppjoinplus.similarity.verify;

import ppjoinplus.similarity.item.Item;

/**
 * PPJoin+のアルゴリズムに基づき、vefiry処理を実施します。
 * @author just_do_neet
 *
 * @param <T>
 */
public interface Verifier {
	public boolean verify(Item ix, Item iy, int i, int j, double a);
}
