package jp.ndca.toolkit.similarity.item;

import java.util.List;

/**
 * 比較対象のitemを表現
 * @author just_do_neet
 *
 */
public class Item<T> implements Comparable<Item<T>>{
	private Object id;
	private T item;
	private List<T> ngrams;
	private int length;

	public Item(Object id, T item, List<T> ngrams){
		super();
		this.id = id;
		this.item = item;
		this.ngrams = ngrams;
	}
	
	public Item(Object id, T item, List<T> ngrams, int length){
		this(id, item, ngrams);
		this.length = length;
	}
	
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public boolean equals(Item<T> item){
		return item.id == id;
	}

	public T getItem() {
		return item;
	}
	public void setItem(T item) {
		this.item = item;
	}
	public List<T> getNgrams() {
		return ngrams;
	}
	public void setNgrams(List<T> ngrams) {
		this.ngrams = ngrams;
	}
	@Override
	public int hashCode(){
		return (id + "_" + item.toString()).hashCode();
	}

	@Override
	public String toString() {
		return item.toString();
	}

	@Override
	public int compareTo(Item<T> i) {
		if(i.length == length){
			return item.toString().compareTo(i.getItem().toString());
		}
		return length < i.length ? -1 : 1;
	}
}
