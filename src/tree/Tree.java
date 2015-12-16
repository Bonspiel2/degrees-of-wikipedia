package tree;

import java.util.ArrayList;
import java.util.List;


public class Tree<T> {

	private Node<T> root;
	
	public Tree(T rootData) {
	
		root = new Node<T>(rootData, null);
		
	}
	
	public Node<T> getRoot(){
		return root;
	}
	
	
	public static class Node<T> {
		private T data;
		private Node<T> parent;
		private List<Node<T>> children;
		
		public Node(T data, Node<T> parent){
			this.data = data;
			this.parent = parent;
			children = new ArrayList<Node<T>>();
		}
		
		public void addChild(T child){
			children.add(new Node<T>(child, this));
		}
		
		public Node<T> findChild(T childToFind){
			
			T data = childToFind;
			
			for(Node<T> child : children){
				if (child.getData().equals(data)){
					return child;
				}
			}
			
			return null;
			
		}
		
		public boolean hasChild(T data){
			for (Node<T> child : children){
				if (child.getData().equals(data)){
					return true;
				}
			}
			return false;
		}
		
		public T getData(){
			return data;
		}
		
		public Node<T> getParent(){
			return parent;
		}
		
		public void setParent(Node<T> p){
			parent = p;
		}
		
		public List<Node<T>> getChildren(){
			return children;
		}
	}

}
