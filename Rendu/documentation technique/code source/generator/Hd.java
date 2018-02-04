package org.xtext.example.generator;

public class Hd extends Instruction{
	public Hd(String res, String left){
		this.left = left;
		this.res = res;
	}
	
	public String toString(){
		return "<HD, "+this.res+", "+this.left+", _>";
	}
}
