package org.xtext.example.generator;

import java.util.ArrayList;

public class Liste extends Instruction{
	private ArrayList<String> left;
	
	public Liste(String res, String left){
		this.res = res;
		this.left = new ArrayList<String>();
		this.right = "_";
	}
	
	public void setLeft(ArrayList<String> list){
		this.left = list;
	}
	
	public Object[] getLeft(){
		return this.left.toArray();
	}
	
	public String toString(){
		return "<LIST, "+this.res+", "+this.left.toString()+", "+this.right+">";
	}
}
