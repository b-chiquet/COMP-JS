package org.xtext.example.generator;

import java.util.ArrayList;

public class Call extends Instruction{
	private ArrayList<String> right;
	
	public Call(String res, String left){
		this.res=res;
		this.left=left;
		this.right= new ArrayList<String>();
	}
	
	public void setRight(ArrayList<String> list){
		this.right = list;
	}
	
	public ArrayList<String> getRight() {
		return right;
	}

	public String toString(){
		return "<CALL, "+res+", "+left+", "+right.toString()+">";
	}
}
