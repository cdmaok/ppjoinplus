package ppjoinplus.similarity.item;

/**
 * 比較対象のitemを表現
 * @author just_do_neet
 *
 */
//public abstract class Item implements Comparable<Item>{
public abstract class Item {
	protected Object id;

	public Object getId() {
		return id;
	}
	public boolean equals(Item item){
		return item.id == id;
	}

	public abstract int length();
	public abstract boolean compare(Item item , int xi, int yi);

}
