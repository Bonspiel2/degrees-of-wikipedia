package tree;

import java.util.ArrayList;
import java.util.List;


public class Tree<T> implements java.io.Serializable{

	private Node<T> root;
	
	public Tree(T rootData) {
	
		root = new Node<T>(rootData, null);
		
	}
	
	public void addNode(T data, T parent){
		Node<T> parentNode = findNode(parent, root);
		
		if (parentNode != null){
			parentNode.addChild(data);
		}
	}
	
	public boolean contains(T data){
		return findNode(data, root) != null;
	}
	
	public ArrayList<T> findPath(T data, int branches){
		ArrayList<T> path = new ArrayList<T>();
		
		Node<T> foundNode = findNode(data, root, branches);
		
		if (foundNode != null){
			
			Node<T> parentNode = foundNode;
			
			for (int i = 0; i < branches && parentNode!=null; i++){
				path.add(parentNode.getData());
				parentNode = parentNode.getParent();
			}
		}
		
		
		return path;
	}
	
	private Node<T> findNode(T data, Node<T> root){
		if(data.equals(root.getData())){
			return root;
		} else {
			
			List<Node<T>> rootChildren = root.getChildren();
			Node<T> returnNode = null;
			
			for (int i = 0; i < rootChildren.size(); i++){
				returnNode = findNode(data, rootChildren.get(i));
				
				if (returnNode != null){
					return returnNode;
				}
			}
			
			return returnNode;
		}
	}
	
	private Node<T> findNode(T data, Node<T> root, int branches){
		
		if (branches <= 0){
			return null;
		}
		
		if(data.equals(root.getData())){
			return root;
		} else {
			
			List<Node<T>> rootChildren = root.getChildren();
			Node<T> returnNode = null;
			
			for (int i = 0; i < rootChildren.size(); i++){
				returnNode = findNode(data, rootChildren.get(i), branches - 1);
				
				if (returnNode != null){
					return returnNode;
				}
			}
			
			return returnNode;
		}
	}
	
	
	public Node<T> getRoot(){
		return root;
	}
	
	
	public static class Node<T> implements java.io.Serializable{
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
