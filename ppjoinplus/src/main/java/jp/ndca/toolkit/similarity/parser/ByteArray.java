package jp.ndca.toolkit.similarity.parser;

import java.util.Arrays;

public class ByteArray {
	private byte[] bytes;
	
	public ByteArray(){
		super();
	}

	public ByteArray(byte[] bytes){
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public int length(){
		return bytes.length;
	}
	
	public byte[] subList(int from, int to){
		return Arrays.copyOfRange(bytes, from, to);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ByteArray){
			return Arrays.equals(bytes, ((ByteArray)obj).getBytes());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bytes);
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	
}
