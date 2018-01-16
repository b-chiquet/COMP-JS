package org.xtext.example.generator;

public class Cons extends Instruction{
	public Cons(String res, String left){
		this.left = left;
		this.res = res;
	}
	
	public String toString(){
		return "<CONS, "+this.res+", "+this.left+", _>";
	}
}
