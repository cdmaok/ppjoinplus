package jp.ndca.toolkit.nlp.distance;

/**
 * Edit Distance(レーベンシュタイン距離）の算出を行うクラスです。
 * 
 * @author just_do_neet
 */
public class EditDistance {

	/**
	 * Edit Distanceを計算します。引数にnullが含まれている場合は負数（-1）が返却されます。
	 * @param a
	 * @param b
	 * @return
	 */
	public int calc(String a, String b){
		//引数のうちどちらかがnullの場合は計算無効。-1を返す。
		if(a == null || b == null)
			return -1;
		//引数のうちどちらかが空文字の場合は計算無効。-1を返す。
		if(a.length() <= 0 || b.length() <= 0)
			return -1;
		//引数のどちらも一文字の場合は単純比較で終了する
		if(a.length() == 1 && b.length() == 1)
			return a.equals(b) ? 0 : 1;
		
        int[] cost = new int[a.length()+1];
        int[] back = new int[a.length()+1];

        for( int i=0; i<=a.length(); i++ ){
            cost[i] = i;
        }
        for( int j=0; j<b.length(); j++ ) {
        	int[] t = cost;
            cost = back;
            back = t;
            
            cost[0] = j+1;
            for( int i=0; i<a.length(); i++ ) {
                int match = (a.charAt(i)==b.charAt(j))?0:1;
                cost[i+1] = min( back[i]+match, cost[i]+1, back[i+1]+1 );
            }
        }
        return cost[a.length()];
	}

	/**
	 * 文字の挿入,削除,置換のうち、一番コストが低いものを返却します。
	 * @param insert
	 * @param delete
	 * @param replace
	 * @return
	 */
	private int min (int insert, int delete, int replace){
		if(insert > delete){
			if(delete > replace){
				return replace;
			}else{
				return delete;
			}
		}else{
			if(insert > replace){
				return replace;
			}else{
				return insert;
			}
		}
	}
	
	/**
	 * 同じ単語が複数回続いていたら一語にまとめます
	 * @param str
	 * @return
	 */
	private String compress(String str){
		if(str == null)
			return null;
		StringBuilder result = new StringBuilder();
		char b = (char)Integer.MAX_VALUE;
		for(char c : str.toCharArray()){
			if(b == c)
				continue;
			result.append(c);
			b = c;
		}
		return result.toString();
	}
	
	/**
	 * 渡された二つの文字列が類似しているかどうかを判定します。類似度が高いと判定された場合はtrueが返ります。
	 * @param a
	 * @param b
	 * @param ratio	類似度のパーセンテージ。
	 * @param minLength 判定処理を行う文字数の最小値。これより短い文字については類似度の判定を行わない。0以下の値が指定された場合は文字列長を問わずに判定処理を行う
	 * @return
	 */
	public boolean isSimilar(String a, String b, double ratio, int minLength){
		a = compress(a);
		b = compress(b);
		
		int editDistanceCost = this.calc(a, b);
		
		if(editDistanceCost < 0)
			return false;
		if(editDistanceCost == 0)
			return true;
		
		int lengthDistance = Math.abs(a.length() - b.length());
		int baseStringLength =
			a.length() > b.length() ? 
					b.length() : a.length();
		
		double lengthRadio = ((baseStringLength + lengthDistance) / baseStringLength)  * ratio;
		
		if(lengthRadio > 1)
			return false;
		if(minLength > 0 && baseStringLength <= minLength)
			return false;

		double editDistanceBaseCost = (baseStringLength + lengthDistance) * (1 - ratio);
		return editDistanceCost <= editDistanceBaseCost;
//		double similarRatio = editDistanceCost * lengthRadio * ratio;
//		
//		return similarRatio <= 1.0;

	}
	
	public static void main(String[] args){
		String a = args[0];
		String b = args[1];
		EditDistance distance = new EditDistance();
		double ratio = 0.80;
		int minLength = 5;
		
		System.out.println("string a: " + a + "(" + a.length() + ")");
		System.out.println("string b: " + b + "(" + b.length() + ")");
		System.out.println("edit distance: " + String.valueOf(distance.calc(a, b)));
		System.out.println("is similar: " + String.valueOf(distance.isSimilar(a, b, ratio, minLength)));
	}
	
}
