package org.xtext.example.generator;

public class Or extends Instruction{
	String left,right;
	
	public Or(String res, String left, String right){
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
		return "<OR, "+res+", "+left+", "+right+">";
	}
}
