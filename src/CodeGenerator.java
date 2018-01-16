package org.xtext.example.generator;

import java.util.ArrayList;

import org.xtext.example.projet.*;

public class CodeGenerator {
	
	//table des fonctions
	private funcTab table;
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
		this.compile(d.getCode(), nouvelleFunc.getCode());
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
	private void compile(COMMANDS c, ArrayList<Instruction> listDest) {
		this.compile(c.getCommand(), listDest);
		for(COMMAND command : c.getCommands()){
			this.compile(command, listDest);
		}
	}

	// Pour le type "COMMAND", càd chaque commande 
	private void compile(COMMAND c,ArrayList<Instruction> listDest) {
		if(c.eClass().getName() == "AFFECT"){
			this.compile((AFFECT)c,listDest);
		}
		if(c.eClass().getName() == "IF_THEN"){
			this.compile((IF_THEN)c,listDest);
		}
		if(c.eClass().getName() == "NOP"){
			this.compile((NOP)c,listDest);
		}
		if(c.eClass().getName() == "FOR_LOOP"){
			this.compile((FOR_LOOP)c,listDest);
		}
		if(c.eClass().getName() == "WHILE"){
			this.compile((WHILE)c,listDest);
		}
		if(c.eClass().getName() == "FOREACH"){
			this.compile((FOREACH)c,listDest);
		}
	}

	// Pour le type "AFFECT"
	private void compile(AFFECT a, ArrayList<Instruction> listDest) {
		nouvelleFunc.addVar(a.getVariable());
		//si expression est un and
		if(!a.getValeur().getExpand().getExpors().isEmpty()){
			And instr = new And("","","");
			listDest.add(instr);
			instr.setRes(nouvelleFunc.getVar(a.getVariable()));
			instr.setLeft(this.compile(a.getValeur(),instr, listDest));
		}
		//si expression est un or
		else if(!a.getValeur().getExpand().getExpor().getExpnots().isEmpty()){
			Or instr = new Or("","","");
			listDest.add(instr);
			instr.setRes(nouvelleFunc.getVar(a.getVariable()));
			instr.setLeft(this.compile(a.getValeur(),instr,listDest));
		}
		//si expression est un =?
		else if(!a.getValeur().getExpand().getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			Eq instr = new Eq("","","");
			listDest.add(instr);
			instr.setRes(nouvelleFunc.getVar(a.getVariable()));
			instr.setLeft(this.compile(a.getValeur(),instr,listDest));
		}
		//si expression simple
		else{
			//récupération de l'expression simple
			EXPRSIMPLE e = a.getValeur().getExpand().getExpor().getExpnot().getExpeq().getExp1();
			//nil ou variable ou symbole
			if(e.getValeur() != null){
				if(e.getValeur().equals("nil")){
					Nil instr = new Nil(nouvelleFunc.getVar(a.getVariable()));
					listDest.add(instr);
				}else{
					Affect instr = new Affect(nouvelleFunc.getVar(a.getVariable()),nouvelleFunc.getVar(e.getValeur()));
					listDest.add(instr);
				}
			//cons
			}else if(e.getCons() != null){
				Cons instr = new Cons("","");
				listDest.add(instr);
				instr.setRes(nouvelleFunc.getVar(a.getVariable()));
				instr.setLeft(nouvelleFunc.getVar(this.compile(e.getLexpr(),instr,listDest)));
			//list
			}else if(e.getList() != null){
				Liste instr = new Liste("","");
				listDest.add(instr);
				instr.setRes(nouvelleFunc.getVar(a.getVariable()));
				instr.setLeft(nouvelleFunc.getVar(this.compile(e.getLexpr(),instr,listDest)));
			//hd
			}else if(e.getHd() != null){
				Hd instr = new Hd("","");
				listDest.add(instr);
				instr.setRes(nouvelleFunc.getVar(a.getVariable()));
				instr.setLeft(nouvelleFunc.getVar(this.compile(e.getExpr(),instr,listDest)));
			//tl
			}else if(e.getTl() != null){
				Tl instr = new Tl("","");
				listDest.add(instr);
				instr.setRes(nouvelleFunc.getVar(a.getVariable()));
				instr.setLeft(nouvelleFunc.getVar(this.compile(e.getExpr(),instr,listDest)));
			//sym
			}else if(e.getSym() != null){
				//to do
				//res = this.compile(e.getExpr(), instr);
				if(e.getLexpr() != null){
					
				}
			}
		}
	}

	// Pour le type "IF_THEN"
	private void compile(IF_THEN if_then, ArrayList<Instruction> listDest) {
		If instr = new If("");
		listDest.add(instr);
		EXPRAND and = if_then.getCond().getExpand();
		//si la condition est une expression composée (AND , OR , =?)
		if(!and.getExpors().isEmpty()){
			String reg = nouvelleFunc.addReg();
			And tmp = new And("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(if_then.getCond(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnots().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Or tmp = new Or("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(if_then.getCond(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Eq tmp = new Eq("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(if_then.getCond(), tmp, listDest));
			instr.setRight(reg);
		//
		}else{
			instr.setLeft(this.compile(if_then.getCond(),instr, listDest));
		}
		for(COMMANDS comm : if_then.getCommands1()){
			this.compile(comm, instr.getCode());
		}
		for(COMMANDS comm : if_then.getCommands2()){
			this.compile(comm, instr.getCodeBis());
		}
	}

	// Pour le type "NOP"
	private void compile(NOP n, ArrayList<Instruction> listDest) {
		Nop instr = new Nop();
		listDest.add(instr);
	}
	
	//Pour le type "FOR_LOOP"
	private void compile(FOR_LOOP fl, ArrayList<Instruction> listDest){
		For instr = new For("");
		listDest.add(instr);
		EXPRAND and = fl.getExp().getExpand();
		//si la condition est une expression composée (AND , OR , =?)
		if(!and.getExpors().isEmpty()){
			String reg = nouvelleFunc.addReg();
			And tmp = new And("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fl.getExp(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnots().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Or tmp = new Or("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fl.getExp(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Eq tmp = new Eq("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fl.getExp(), tmp, listDest));
			instr.setRight(reg);
		//
		}else{
			instr.setLeft(this.compile(fl.getExp(),instr, listDest));
		}
		for(COMMANDS comm : fl.getCommands()){
			this.compile(comm, instr.getCode());
		}
	}
	
	//Pour le type "WHILE"
	private void compile(WHILE w, ArrayList<Instruction> listDest){
		While instr = new While("");
		listDest.add(instr);
		EXPRAND and = w.getCond().getExpand();
		//si la condition est une expression composée (AND , OR , =?)
		if(!and.getExpors().isEmpty()){
			String reg = nouvelleFunc.addReg();
			And tmp = new And("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(w.getCond(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnots().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Or tmp = new Or("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(w.getCond(), tmp, listDest));
			instr.setRight(reg);
		}else if(!and.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Eq tmp = new Eq("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(w.getCond(), tmp, listDest));
			instr.setRight(reg);
		//
		}else{
			instr.setLeft(this.compile(w.getCond(),instr, listDest));
		}
		for(COMMANDS comm : w.getCommands()){
			this.compile(comm, instr.getCode());
		}
	}
	
	//Pour le type "FOREACH"
	private void compile(FOREACH fe, ArrayList<Instruction> listDest){
		Foreach instr = new Foreach("");
		listDest.add(instr);
		EXPRAND exp1 = fe.getExp1().getExpand();
		//si la condition est une expression composée (AND , OR , =?)
		if(!exp1.getExpors().isEmpty()){
			String reg = nouvelleFunc.addReg();
			And tmp = new And("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp1(), tmp, listDest));
			instr.setRight(reg);
		}else if(!exp1.getExpor().getExpnots().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Or tmp = new Or("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp1(), tmp, listDest));
			instr.setRight(reg);
		}else if(!exp1.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Eq tmp = new Eq("","","");
			listDest.add(tmp);
			instr.setLeft(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp1(), tmp, listDest));
			instr.setRight(reg);
		//
		}else{
			instr.setLeft(this.compile(fe.getExp1(),instr, listDest));
		}
		//instr.setLeft(this.compile(fe.getExp1(), instr, listDest));
		EXPRAND exp2 = fe.getExp2().getExpand();
		//si la condition est une expression composée (AND , OR , =?)
		if(!exp2.getExpors().isEmpty()){
			String reg = nouvelleFunc.addReg();
			And tmp = new And("","","");
			listDest.add(tmp);
			instr.setLeftBis(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp2(), tmp, listDest));
			instr.setRight(reg);
		}else if(!exp2.getExpor().getExpnots().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Or tmp = new Or("","","");
			listDest.add(tmp);
			instr.setLeftBis(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp2(), tmp, listDest));
			instr.setRight(reg);
		}else if(!exp2.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			String reg = nouvelleFunc.addReg();
			Eq tmp = new Eq("","","");
			listDest.add(tmp);
			instr.setLeftBis(reg);
			tmp.setRes(reg);
			tmp.setLeft(this.compile(fe.getExp2(), tmp, listDest));
			instr.setRight(reg);
		//
		}else{
			instr.setLeftBis(this.compile(fe.getExp2(),instr, listDest));
		}
		//instr.setLeftBis(this.compile(fe.getExp2(), instr, listDest));
		for(COMMANDS comm : fe.getCommands()){
			this.compile(comm, instr.getCode());
		}
  	}


	//Pour le type "EXPRESSION"
	private String compile(EXPRESSION e,Instruction instr, ArrayList<Instruction> listDest){
		return this.compile(e.getExpand(),instr,listDest);
	}
	
	private String compile(EXPRAND e,Instruction instr, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une expression AND
		if (!e.getExpors().isEmpty()){
			//compilation terme de gauche
			res = this.compile(e.getExpor(),instr, listDest);
			//compilation terme de droite
			for(EXPRAND exp : e.getExpors()){
				//si l'expression de droite est une expression composee (and, or, eq)
				if(!exp.getExpors().isEmpty()){
					String reg = nouvelleFunc.addReg();
					And tmp = new And("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				}else if(!exp.getExpor().getExpnots().isEmpty()){
					String reg = nouvelleFunc.addReg();
					Or tmp = new Or("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				}else if(!exp.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
					String reg = nouvelleFunc.addReg();
					Eq tmp = new Eq("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				//
				}else{
					instr.setRight(this.compile(exp, instr, listDest));
				}
			}
		//si c'est une expression OR	
		}else{
			res = this.compile(e.getExpor(),instr, listDest);
		}
		return res;
	}
	
	private String compile(EXPROR e,Instruction instr, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une expression OR
		if (!e.getExpnots().isEmpty()){
			//compilation terme de gauche
			res = this.compile(e.getExpnot(),instr, listDest);
			//compilation terme de droite
			for(EXPRAND exp :e.getExpnots()){
				//si l'expression de droite est une expression composee (or, eq, and)
				if(!exp.getExpors().isEmpty()){
					String reg = nouvelleFunc.addReg();
					And tmp = new And("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				}else if(!exp.getExpor().getExpnots().isEmpty()){
					String reg = nouvelleFunc.addReg();
					Or tmp = new Or("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				}else if(!exp.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
					String reg = nouvelleFunc.addReg();
					Eq tmp = new Eq("","","");
					listDest.add(tmp);
					tmp.setRes(reg);
					tmp.setLeft(this.compile(exp, tmp, listDest));
					instr.setRight(reg);
				//
				}else{
					instr.setRight(this.compile(exp, instr, listDest));
				}
			}
		}else{
			res = this.compile(e.getExpnot(),instr, listDest);
		}
		return res;
	}
	
	private String compile(EXPRNOT e,Instruction instr, ArrayList<Instruction> listDest){
		String res="";
		//si on a une instruction not
		if(e.getN() != null){
			res = this.compile(e.getExpeq(),instr, listDest);
			nouvelleFunc.addCode(new Not("",""));
		}else{
			res = this.compile(e.getExpeq(),instr, listDest);
		}
		return res;
	}
	
	private String compile(EXPREQ e,Instruction instr, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une exression simple ou eq
		if(e.getExp1() != null){
			//si c'est une expression eq
			if(!e.getExp2().isEmpty()){
				//instr.setOp(Op.EQ);
				res = this.compile(e.getExp1(),instr, listDest);
				for(EXPRAND exp : e.getExp2()){
					//si l'expression de droite est une expression composee (or, eq, and)
					if(!exp.getExpors().isEmpty()){
						String reg = nouvelleFunc.addReg();
						And tmp = new And("","","");
						listDest.add(tmp);
						tmp.setRes(reg);
						tmp.setLeft(this.compile(exp, tmp, listDest));
						instr.setRight(reg);
					}else if(!exp.getExpor().getExpnots().isEmpty()){
						String reg = nouvelleFunc.addReg();
						Or tmp = new Or("","","");
						listDest.add(tmp);
						tmp.setRes(reg);
						tmp.setLeft(this.compile(exp, tmp, listDest));
						instr.setRight(reg);
					}else if(!exp.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
						String reg = nouvelleFunc.addReg();
						Eq tmp = new Eq("","","");
						listDest.add(tmp);
						tmp.setRes(reg);
						tmp.setLeft(this.compile(exp, tmp, listDest));
						instr.setRight(reg);
					//
					}else{
						instr.setRight(this.compile(exp,instr, listDest));
					}
				}
			//si c'est une expression simple
			}else{
				res = this.compile(e.getExp1(),instr, listDest);
			}
		//si c'est une expression entre parenthèses
		}else{
			EXPRAND exp = e.getExp();
			/*//si l'expression est une expression composee (or, eq, and)
			 * ...
			*/
			res = this.compile(exp,instr, listDest);
		}
		return res;
	}
	
	private String compile(EXPRSIMPLE e,Instruction instr, ArrayList<Instruction> listDest){
		String res="";
		if(e.getValeur() != null){
			if(e.getValeur().equals("nil")){
				res = "_";
			}else{
				res = e.getValeur();
			}
		}else if (e.getLexpr() != null){
			res = this.compile(e.getLexpr(), instr, listDest);
		}else if (e.getExpr() != null){
			res = this.compile(e.getExpr(), instr, listDest);
		}
		return res;
	}
	
	private String compile(LEXPR e,Instruction instr, ArrayList<Instruction> listDest){
		String res = this.compile(e.getExpr(),instr, listDest);
		if(e.getLexpr() != null){
			this.compile(e.getLexpr(), instr, listDest);
		}
		return res;
	}
}
