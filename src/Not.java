package org.xtext.example.generator;

public class Not extends Instruction{
	public Not(String res, String left){
		this.res = res;
		this.left = left;
	}
	
	public String toString(){
		return "<NOT, "+res+", "+left+", _>";
	}
}
