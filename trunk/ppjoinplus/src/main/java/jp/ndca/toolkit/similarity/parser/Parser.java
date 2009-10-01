package jp.ndca.toolkit.similarity.parser;

import java.util.List;

/**
 * データをn-gram単位に分割するparserです。
 * @author moaikids
 *
 * @param <T>
 */
public interface Parser<T> {
	public List<T> convertToNgram(T object, int n);
	public List<T> convertToNgram(T object, int n, int length);
}
