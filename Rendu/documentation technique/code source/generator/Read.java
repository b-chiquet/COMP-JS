package org.xtext.example.generator;

public class Read extends Instruction{
	public Read(String left){
		this.left = left;
	}
	
	public String toString(){
		return "<READ, _, "+this.left+", _>";
	}
}
