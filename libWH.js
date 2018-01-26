// libWH.js

function Tree (l,r){
	this.left = l;
	this.right = r;
	
	Tree.prototype.setLeft = function(t){
		this.left=t;
	}
	
	Tree.prototype.setRight = function(t){
		this.right=t;
	}
	
	Tree.prototype.getLeft = function(){
		return this.left;
	}
	
	Tree.prototype.getRight = function(){
		return this.right;
	}
}

const nil = new Tree(null,null);

function and (t1, t2){
	return ((!(eq(t1.left,nil)==nil) && !(eq(t1.right,nil) == nil)) && (!(eq(t2.left,nil)==nil) && !(eq(t2.right,nil)==nil)));
}

function or (t1, t2){
	return ((!(eq(t1.left,nil)==nil) && !(eq(t1.right,nil) == nil)) || (!(eq(t2.left,nil)==nil) && !(eq(t2.right,nil)==nil)));
}

function eq (t1, t2){
	return nil;
}

function cons (t1, t2){
	return new Tree(t1,t2);
}

function list (t1, t2){
	return new Tree(t1,new Tree(t2,nil));
}

function hd (t){
	if(t == nil){
		return nil;
	}
	return t.left;
}

function tl (t){
	if(t == nil){
		return nil;
	}
	return t.right;
}

function countIt (t, cpt){
	if((t)==nil){
		return cpt;
	}
	return countIt(tl(t), cpt+1);
}