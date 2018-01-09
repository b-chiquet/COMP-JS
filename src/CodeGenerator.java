package org.xtext.example.generator;

import org.xtext.example.projet.*;

public class CodeGenerator {
	
	//table des fonctions
	static private funcTab table;
	//numéro de fonction
	private int num;
	//funcEntry utilisé pour ajouter des fonctions à la funcTab
	private funcEntry nouvelleFunc;
	
	
	public CodeGenerator(){
		this.table = new funcTab();
		num=0;
	}
	
	public void generate(PROGRAM d){
		this.compile(d);
		System.out.println(this.table.toString());
	}

	
	// Pour le type "PROGRAM", qui représente tout le programme
	private void compile(PROGRAM d) {
		for (FUNCTION func : d.getFunctions()){
			this.compile(func);
		}
	}

	// Pour le type "FUNCTION"
	private void compile(FUNCTION f) {
		this.compile(f.getDef());
	}

	// Pour le type "DEFINTION"
	private void compile(DEFINITION d) {
		nouvelleFunc = new funcEntry();
		table.addFunc("f"+num,nouvelleFunc);
		num++;
		this.compile(d.getInputs());
		this.compile(d.getCode());
		this.compile(d.getOutputs());
	}

	// Pour le type INPUT
	private void compile(INPUTS i) {
		nouvelleFunc.addIn();
		nouvelleFunc.addVar(i.getInput());
		for(INPUTS in : i.getInputs()){
			this.compile(in);
		}
	}

	// Pour le type OUTPUT
	private void compile(OUTPUTS o) {
		nouvelleFunc.addOut();
		for(OUTPUTS out : o.getOutputs()){
			this.compile(out);
		}
	}

	// Pour le type "COMMANDS"
	private void compile(COMMANDS c) {
		this.compile(c.getCommand());
		for(COMMAND command : c.getCommands()){
			this.compile(command);
		}
	}

	// Pour le type "COMMAND", càd chaque commande 
	private void compile(COMMAND c) {
		if(c.eClass().getName() == "AFFECT"){
			this.compile((AFFECT)c);
		}
		if(c.eClass().getName() == "IF_THEN"){
			this.compile((IF_THEN)c);
		}
		if(c.eClass().getName() == "NOP"){
			this.compile((NOP)c);
		}
		if(c.eClass().getName() == "FOR_LOOP"){
			this.compile((FOR_LOOP)c);
		}
		if(c.eClass().getName() == "WHILE"){
			this.compile((WHILE)c);
		}
		if(c.eClass().getName() == "FOREACH"){
			this.compile((FOREACH)c);
		}
	}

	// Pour le type "AFFECT"
	private void compile(AFFECT a) {
		code3A instr = nouvelleFunc.addCode("_","_","_","_");
		nouvelleFunc.addVar(a.getVariable());
		instr.setRes(a.getVariable());
		this.compile(a.getValeur(),instr);
	}

	// Pour le type "IF_THEN"
	private void compile(IF_THEN if_then) {
		code3A instr = nouvelleFunc.addCode("if","_","_","_");
		this.compile(if_then.getCond(),instr);
		for(COMMANDS comm : if_then.getCommands1()){
			this.compile(comm);
		}
		for(COMMANDS comm : if_then.getCommands2()){
			this.compile(comm);
		}
	}

	// Pour le type "NOP"
	private void compile(NOP n) {
		nouvelleFunc.addCode("nop","_","_","_");
	}
	
	//Pour le type "FOR_LOOP"
	private void compile(FOR_LOOP fl){
		nouvelleFunc.addCode("for","_","_","_");
	}
	
	//Pour le type "WHILE"
	private void compile(WHILE w){
		nouvelleFunc.addCode("while","_","_","_");
	}
	
	//Pour le type "FOREACH"
	private void compile(FOREACH fe){
		nouvelleFunc.addCode("foreach","_","_","_");
  	}


	//Pour le type "EXPRESSION"
	private void compile(EXPRESSION e,code3A instr){
		this.compile(e.getExpand(),instr);
	}
	
	private void compile(EXPRAND e,code3A instr){
		if (!e.getExpors().isEmpty()){
			instr.setOp("AND");
			//instr.setLeft("...");
			this.compile(e.getExpor(),instr);
			for(EXPRAND exp : e.getExpors()){
				//instr.setRight("...");
				this.compile(exp, nouvelleFunc.addCode("_","_","_","_"));
			}
		}else{
			this.compile(e.getExpor(),instr);
		}
	}
	
	private void compile(EXPROR e,code3A instr){
		if (!e.getExpnots().isEmpty()){
			instr.setOp("OR");
			//nouvelleFunc.addVar("r")
			//instr.setLeft("...");
			this.compile(e.getExpnot(),instr);
			for(EXPROR exp :e.getExpnots()){
				//instr.setRight("...");
				this.compile(exp, nouvelleFunc.addCode("_","_","_","_"));
			}
		}else{
			this.compile(e.getExpnot(),instr);
		}
	}
	
	private void compile(EXPRNOT e,code3A instr){
		if(e.getN() != null){
			this.compile(e.getExpeq(),instr);
			nouvelleFunc.addCode("not","_","_","_");
		}else{
			this.compile(e.getExpeq(),instr);
		}
	}
	
	private void compile(EXPREQ e,code3A instr){
		if(e.getExp1() != null){
			if(!e.getExp2().isEmpty()){
				instr.setOp("=?");
				//instr.setLeft("...");
				this.compile(e.getExp1(),instr);
				for(EXPRSIMPLE exp : e.getExp2()){
					//instr.setRight("...");
					this.compile(exp,nouvelleFunc.addCode("_","_","_","_"));
				}
			}else{
				this.compile(e.getExp1(),instr);
			}
		}else{
			this.compile(e.getExp(),instr);
		}
	}
	
	private void compile(EXPRSIMPLE e,code3A instr){
		if(e.getValeur() != null){
			if(e.getValeur() == "nil"){
				instr.setOp("nil");
			}else{
				if (instr.getOp() == "_"){
					instr.setOp("affect");
					instr.setLeft(nouvelleFunc.getVar(e.getValeur()));
				}
			}
		}else if(e.getCons() != null){
			//to do'''
		}else if(e.getList() != null){
			//to do
		}else if(e.getHd() != null){
			//instr.setLeft("...");
			this.compile(e.getExpr(),instr);
			instr.setOp("hd");
		}else if(e.getTl() != null){
			//instr.setLeft("...");
			this.compile(e.getExpr(), instr);
			instr.setOp("tl");
		}else if(e.getSym() != null){
			//to do
		}
	}
	
	private void compile(LEXPR e,code3A instr){
		this.compile(e.getExpr(),instr);
		if(e.getLexpr() != null){
			this.compile(e.getLexpr(), instr);
		}
	}
}
