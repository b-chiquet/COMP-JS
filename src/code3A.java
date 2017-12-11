package org.xtext.example.generator;

/**
 * Classe de représentation d'instructions en code 3 adresses
 * @author groupe JS
 *
 */
public class code3A {
	String op;
	String res;
	String left,right;
	
	code3A(String op, String res, String left, String right){
		this.op = op;
		this.res = res;
		this.left = left;
		this.right = right;
	}

	public String toString(){
		return "<"+op+", "+res+", "+left+", "+right+">";
	}
}
