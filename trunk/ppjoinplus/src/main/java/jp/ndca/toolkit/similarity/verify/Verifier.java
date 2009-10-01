package jp.ndca.toolkit.similarity.verify;

import jp.ndca.toolkit.similarity.item.Item;

/**
 * PPJoin+のアルゴリズムに基づき、vefiry処理を実施します。
 * @author just_do_neet
 *
 * @param <T>
 */
public interface Verifier<T> {
	public boolean verify(Item<T> ix, Item<T> iy, int i, int j, double a);
}
