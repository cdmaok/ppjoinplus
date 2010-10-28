-このプロジェクトについて
PPJoin(PPJoin+)の論文に基づいて実装した、文字列群から類似する文字列の組み合わせを高速で抽出するアルゴリズムです。
ただし論文に記されたアルゴリズム完全準拠という訳ではありません。実装上一部独自の解釈を加えています。
http://www.cse.unsw.edu.au/~lxue/WWW08.pdf

-必要な動作環境
--Java
Generics等を使用しているためJava 1.5以上
--simmetrics
類似度算出の際に使用
http://sourceforge.net/projects/simmetrics/
--sen
日本語の形態素解析の際に使用
https://sen.dev.java.net/servlets/ProjectDocumentList?folderID=755&expandFolder=755&folderID=0
--JUnit4.x(test時）

-動作方法
--テストクラスの動作
　src/test/java
配下に、テスト用のクラス
　ppjoinplus.similarity.NgramPPJoinTest
を用意しています。こちらを実行すると複数のテスト用文字列群に対してPPJoin/PPJoin+を用いたペア抽出を実施します。

実装の仕方はテストクラスの中身や、以下のサンプルソースを参考にしてください。
--------------------------------------------------
List<Item> items = new ArrayList<Item>();//ここに類似ペア抽出対象の文字列を詰める

PPJoin ppjoin = new PPJoin();
ppjoin.setPpjoinplus(ppjoinplus);
ppjoin.setSuffixFilter(new NgramSuffixFilter());
ppjoin.setThreshold(threshold); //類似度のしきい値
ppjoin.setVerifier(
	new NgramVerifier(
		threshold, 
		new OverlapCoefficient(new CharacterNgramTokenizer(ngram, new CharacterNgramParser()))); //類似度算出アルゴリズム。ここではOverlap
	
List<Item[]> r = ppjoin.joinItem(items);//抽出開始
--------------------------------------------------
