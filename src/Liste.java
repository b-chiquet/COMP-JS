package org.xtext.example.generator;

public class Liste extends Instruction{
	public Liste(String res, String left){
		this.left = left;
		this.res = res;
	}
	
	public String toString(){
		return "<LIST, "+this.res+", "+this.left+", _>";
	}
}
