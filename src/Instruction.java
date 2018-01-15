package org.xtext.example.generator;

public class Instruction {
	String res,left,right;
	
	public Instruction(){
		this.res="_";
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
		return "<INSTR, "+res+", "+left+", "+right+">";
	}
}
