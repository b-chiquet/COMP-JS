package org.xtext.example.generator;

public class Affect extends Instruction{
	
	public Affect(String res, String left){
		this.res = res;
		this.left = left;
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public String toString(){
		return "<AFFECT, "+res+", "+left+", _>";
	}
}
