package org.xtext.example.generator;

public class Eq extends Instruction{
	String res;
	String left,right;
	
	public Eq(String res, String left, String right){
		this.res = res;
		this.left = left;
		this.right = right;
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public void setRight(String right){
		this.right = right;
	}

	public String toString(){
		return "<=?, "+res+", "+left+", "+right+">";
	}
}
