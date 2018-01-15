package org.xtext.example.generator;

public class And extends Instruction{
	String left,right;
	
	public And(String res, String left, String right){
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
		return "<AND, "+res+", "+left+", "+right+">";
	}
}
