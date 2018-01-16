package org.xtext.example.generator;

public class Tl extends Instruction{
	public Tl(String res, String left){
		this.left = left;
		this.res = res;
	}
	
	public String toString(){
		return "<TL, "+this.res+", "+this.left+", _>";
	}
}
