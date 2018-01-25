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

const nil = new Tree();

function and (t1, t2){
	return nil;
}

function or (t1, t2){
	return nil;
}

function eq (t1, t2){
	return false;
}