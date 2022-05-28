/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

// omeritzhak , ID : 205656986
// saargerassi , ID : 313221210

public class AVLTree {
	public final IAVLNode external = new AVLNode();
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;

	public AVLTree(IAVLNode root) {
		this.root = root;
		this.min = this.get_min_key();
		this.max = this.get_max_key();

	}

	public AVLTree() {
		this.root = null;
		this.min = null;
		this.max = null;
	}

	public IAVLNode get_min_key (){
		if (this.empty()) {
			return null;
		}
		IAVLNode curr = this.root;
		while (curr.getLeft().getKey() != -1) {
			curr = curr.getLeft();
		}
		return curr;
	}
	public IAVLNode get_max_key (){
		if (this.empty()) {
			return null;
		}
		IAVLNode curr = this.root;
		while (curr.getRight().getKey() != -1) {
			curr = curr.getRight();
		}
		return curr;
	}


	/**
	 * public boolean empty()
	 * <p>
	 * Returns true if and only if the tree is empty.
	 */
	public boolean empty() {
		return (this.root == null);
	}  //O(1)

	/**
	 * public String search(int k)
	 * <p>
	 * Returns the info of an item with key k if it exists in the tree.
	 * otherwise, returns null.
	 */
	public String search(int k) { // O(logn)
		IAVLNode curr = this.root;
		int temp_key = this.root.getKey();
		while (curr != external) {
			if (temp_key == k) {
				return curr.getValue();
			} else if (temp_key > k) {
				curr = curr.getLeft();
				temp_key = curr.getKey();
			} else {
				curr = curr.getRight();
				temp_key = curr.getKey();
			}
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 * <p>
	 * Inserts an item with key k and info i to the AVL tree.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) { //O(logn)
		if (this.empty()){
			this.root = new AVLNode(k,i);
			this.max = this.root;
			this.min = this.root;
			return 0;
		}
		int res =0;
		if (search(k) != null) {
			return -1;
		}
		IAVLNode z = new AVLNode(k, i);
		IAVLNode y = search_node_insert(k);
		if (y.getKey() < k) {
			y.setRight(z);
		} else {
			y.setLeft(z);
		}
		z.setParent(y);
		this.min = get_min_key();
		this.max = get_max_key();
		while (y != null){
			int hight_temp = y.getHeight();
			y.setHeight(1+Math.max(y.getLeft().getHeight(),y.getRight().getHeight()));
			y.setSize(1+y.getLeft().getSize() + y.getRight().getSize());
			int BF = compute_BF(y);
			if (Math.abs(BF) < 2 && hight_temp == y.getHeight()){
				fixSizeHight(y);
				return 0;
			}
			else if (Math.abs(BF) < 2 && hight_temp != y.getHeight()) {
				y = y.getParent();
				res +=1;
			}
			else { //BF == 2
				String type = rebalnce(y);
				res += rotation(y,type);
				if (y.getParent().getParent() == null){
					this.root = y.getParent();
				}
				fixSizeHight(y);
				return res;
			}
		}
		fixSizeHight(y);
		return res;
	}
	private int compute_BF (IAVLNode y){
		return y.getLeft().getHeight()-y.getRight().getHeight();
	}
	private String rebalnce(IAVLNode y) {
		if (compute_BF(y) == 2) {
			if (compute_BF(y.getLeft()) == 1 || compute_BF(y.getLeft()) ==0 ) {
				return "R";
			} else { // left son is -1
				return "LR";
			}
		} else { // BF == -2
			if (compute_BF(y.getRight()) == -1 || compute_BF(y.getRight()) ==0) {
				return "L";
			} else { // right son is 1
				return "RL";
			}
		}
	}

	private void right_rotation(IAVLNode y){
		IAVLNode temp_parent = y.getParent();
		IAVLNode temp_left_son = y.getLeft();
		IAVLNode temp_left_son_right = temp_left_son.getRight();
		temp_left_son.setRight(y);
		temp_left_son.setParent(temp_parent);
		y.setLeft(temp_left_son_right);
		if (temp_left_son_right != external){
			temp_left_son_right.setParent(y);
		}
		y.setParent(temp_left_son);
		if (temp_parent != null && temp_parent.getLeft().getKey() == y.getKey()){
			temp_parent.setLeft(temp_left_son);
		}
		else if (temp_parent != null && temp_parent.getRight().getKey() == y.getKey()){
			temp_parent.setRight(temp_left_son);
		}
		y.setHeight(Math.max(y.getLeft().getHeight(),y.getRight().getHeight()) + 1);
		y.setSize(y.getLeft().getSize() + y.getRight().getSize() +1);
		temp_left_son.setHeight(Math.max(temp_left_son.getLeft().getHeight(),temp_left_son.getRight().getHeight()) + 1 );
		temp_left_son.setSize(temp_left_son.getLeft().getSize() + temp_left_son.getRight().getSize() +1);
	}

	private void left_rotation(IAVLNode y){
		IAVLNode temp_parent = y.getParent();
		IAVLNode temp_right_son = y.getRight();
		IAVLNode temp_right_son_left = temp_right_son.getLeft();
		temp_right_son.setLeft(y);
		temp_right_son.setParent(temp_parent);
		y.setParent(temp_right_son);
		y.setRight(temp_right_son_left);
		if (temp_right_son_left !=external){
			temp_right_son_left.setParent(y);
		}
		if ( temp_parent != null && temp_parent.getLeft().getKey() == y.getKey()){
			temp_parent.setLeft(temp_right_son);
		}
		else if (temp_parent != null && temp_parent.getRight().getKey() == y.getKey()){
			temp_parent.setRight(temp_right_son);
		}
		y.setHeight(Math.max(y.getLeft().getHeight(),y.getRight().getHeight()) + 1 );
		y.setSize(y.getLeft().getSize() + y.getRight().getSize() +1);
		temp_right_son.setHeight(Math.max(temp_right_son.getLeft().getHeight(),temp_right_son.getRight().getHeight()) + 1 );
		temp_right_son.setSize(temp_right_son.getLeft().getSize() + temp_right_son.getRight().getSize() +1);
	}

	private int rotation (IAVLNode y, String type){
		if (type.equals("R")){
			right_rotation (y);
			return 3;
		}
		else if (type.equals("RL")){
			right_rotation (y.getRight());
			left_rotation (y);
			return 6;
		}
		else if (type.equals("L")){
			left_rotation (y);
			return 3;
		}
		else {
			left_rotation (y.getLeft());
			right_rotation (y);
			return 6;
		}
	}

	private IAVLNode search_node_insert (int key){
		IAVLNode curr = this.root;
		IAVLNode curr_temp = new AVLNode();
		int temp_key = this.root.getKey();
		while (curr != external) {
			 if (temp_key > key) {
				 curr_temp = curr;
				 curr = curr.getLeft();
				 temp_key = curr.getKey();
			} else {
				 curr_temp = curr;
				 curr = curr.getRight();
				 temp_key = curr.getKey();
			}
		}
		return curr_temp;
	}

	private void fixSizeHight (IAVLNode y){
		while (y != null){
			y.setSize(y.getLeft().getSize() + y.getRight().getSize() +1);
			y.setHeight(Math.max(y.getLeft().getHeight() , y.getRight().getHeight()) +1);
			y =y.getParent();
		}
	}

	/**
	 * public int delete(int k)
	 * <p>
	 * Deletes an item with key k from the binary tree, if it is there.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) { // O(logn)
		if (search(k) == null || this.empty()) {
			return -1;
		}
		int how_many_actions = 0;
		IAVLNode z = search_node_delete(k);
		if (this.getRoot().getKey() == k) {
			if (this.getRoot().getRight() != external) {
				if (this.getRoot().getLeft() == external) {
					this.root = this.getRoot().getRight();
					delete_root(z);
					return 0;
				} else { // has two sons
					this.root = successor(z);
				}
			}
			else if (this.getRoot().getLeft() != external){
				this.root = this.getRoot().getLeft();
				delete_root(z);
				return 0;
			}
			else{
				this.root = null;
				return 0;
			}
		}
		IAVLNode y = delete_node(z);
		while (y != null){
			int hight_temp = y.getHeight();
			y.setHeight(1+Math.max(y.getLeft().getHeight(),y.getRight().getHeight()));
			y.setSize(1+ y.getLeft().getSize() + y.getRight().getSize());
			int BF = compute_BF(y);
			if (Math.abs(BF) < 2 && hight_temp==y.getHeight()){
				break;
			}
			else if (Math.abs(BF) < 2 && hight_temp != y.getHeight() ){
				y = y.getParent();
				how_many_actions +=1;
			}
			else { // BF ==+-2
				String type = rebalnce(y);
				IAVLNode prevParent = y.getParent();
				how_many_actions += rotation(y,type); // rotation returns int
				if (y.getParent().getParent() == null){
					this.root = y.getParent();
				}
				y = prevParent;
			}
		}

		this.min = get_min_key();
		this.max = get_max_key();
		return how_many_actions;
	}

	private IAVLNode search_node_delete (int key){
		IAVLNode curr = this.root;
		IAVLNode curr_temp = new AVLNode();
		int temp_key = this.root.getKey();
		while (curr != external) {
			if (temp_key == key){
				return curr;
			}
			else if (temp_key > key) {
				curr_temp = curr;
				curr = curr.getLeft();
				temp_key = curr.getKey();
			} else {
				curr_temp = curr;
				curr = curr.getRight();
				temp_key = curr.getKey();
			}
		}
		return curr_temp;
	}
	private IAVLNode delete_root (IAVLNode z) {
		IAVLNode y = external;
		if (z.getRight() != external) {
			y = z.getRight();
			z.setLeft(null);
			z.setRight(null);
			y.setParent(null);
		} else if (z.getLeft() != external) {
			y = z.getLeft();
			z.setLeft(null);
			z.setRight(null);
			y.setParent(null);
		}
	return y;
	}

	private IAVLNode delete_node (IAVLNode z){ 
		boolean z_left_son = false;
		IAVLNode y = z.getParent();
		if (y != null) {
			if (y.getKey() > z.getKey()) {
				z_left_son = true;
			} else {
				z_left_son = false;
			}
		}
		if (z.getRight() == external && z.getLeft() == external){
			if (y.getLeft().getKey() == z.getKey()){
				y.setLeft(external);
				z.setRight(null);
				z.setLeft(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			}
			else{
				y.setRight(external);
				z.setRight(null);
				z.setLeft(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			}
		}
		else if ((z.getRight() == external && z.getLeft() !=external) || (z.getLeft() ==external && z.getRight() != external)) {
			if (z.getRight() != external && !z_left_son) {
				IAVLNode son = z.getRight();
				y.setRight(son);
				son.setParent(y);
				z.setRight(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			} else if (z.getRight() != external && z_left_son) {
				IAVLNode son = z.getRight();
				y.setLeft(son);
				son.setParent(y);
				z.setRight(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			} else if (z.getLeft() != external && !z_left_son) {
				IAVLNode son = z.getLeft();
				y.setRight(son);
				son.setParent(y);
				z.setLeft(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			} else if (z.getLeft() != external && z_left_son) {
				IAVLNode son = z.getLeft();
				y.setLeft(son);
				son.setParent(y);
				z.setLeft(null);
				IAVLNode per = z.getParent();
				z.setParent(null);
				return per;
			}
		}
		else {//z has two sons
				IAVLNode sec = successor(z);
				sec.setHeight(z.getHeight());
				sec.setSize(z.getSize());
				IAVLNode per_sec = sec.getParent();
				delete_node(sec);
				sec.setParent(z.getParent());
				sec.setLeft(z.getLeft());
				sec.setRight(z.getRight());
				z.setRight(null);
				z.setLeft(null);
				z.setParent(null);
				IAVLNode parent = sec.getParent();
				if (parent != null) {
					if (parent.getKey() > sec.getKey()) {
						parent.setLeft(sec);
					} else {
						parent.setRight(sec);
					}
				}
				sec.getRight().setParent(sec);
				sec.getLeft().setParent(sec);
				if (per_sec.getKey() == z.getKey()){
					return sec;
				}
				return per_sec;
			}
		return z;
		}

	private IAVLNode successor (IAVLNode z){
		IAVLNode temp = new AVLNode();
		if (z.getRight() != external){
			temp = z.getRight();
			while (temp.getLeft() != external){
				temp = temp.getLeft();
			}
			return temp;
		}
		else{ // has no right son
			temp = z.getParent();
			while (temp.getRight().getKey() == z.getKey()){
				temp = temp.getParent();
			}
			return temp;
		}
	}
	/**
	 * public String min()
	 * <p>
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty.
	 */
	public String min() {
		return this.min.getValue();
	} //O(1)

	/**
	 * public String max()
	 * <p>
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty.
	 * @param height
	 * @param i
	 */
	public String max(int height, int i) {
		return this.max.getValue();
	} // O(1)

	/**
	 * public int[] keysToArray()
	 * <p>
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray() //O(n)
	{
		int i = 0;
		int[] in_order = new int[this.size()];
		return keysToArray_rec(this.root, in_order, i);
	}

	private int[] keysToArray_rec(IAVLNode node, int[] in_order, int i) {
		if (node == null) {
			return null;
		}
		keysToArray_rec(node.getLeft(), in_order, i);
		in_order[i] = node.getKey();
		i += 1;
		keysToArray_rec(node.getRight(), in_order, i);
		return in_order;
	}

	/**
	 * public String[] infoToArray()
	 * <p>
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray() { //O(n)
		int i = 0;
		String[] in_order = new String[this.size()];
		return infoToArray_rec(this.root, in_order, i);
	}

	private String[] infoToArray_rec(IAVLNode node, String[] in_order, int i) {
		if (node == null) {
			return null;
		}
		infoToArray_rec(node.getLeft(), in_order, i);
		in_order[i] = node.getValue();
		i += 1;
		infoToArray_rec(node.getRight(), in_order, i);
		return in_order;
	}

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    */
   public int size()
   { return (this.root.getSize());
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    */
   public IAVLNode getRoot()
   {
	   return this.root;
   }   // O(1)
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    *
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x) //O(logn)
   {
	   AVLNode min_node = new AVLNode(0,"sdf"); // to avoid Nullexeption this.min
	   AVLNode max_node = new AVLNode(Integer.MAX_VALUE, "sdf"); // to avoid Nullexeption this.max
	   AVLTree t1 = new AVLTree();
	   t1.min = min_node;
	   t1.max = max_node;
	   AVLTree t2 = new AVLTree();
	   t2.min = min_node;
	   t2.max = max_node;
	   IAVLNode y = whereToJoin(this.root, x);
	   if (y.getLeft() != external) {
		   t1.root = y.getLeft();
	   } else {
		   t1.root = external;
	   }
	   t1.getRoot().setParent(null);
	   if (t1.getRoot() != external) {
		   t1.min = getTempMin(t1.getRoot());
		   t1.max = getTempMax(t1.getRoot());
	   }
	   if (y.getRight() != external) {
		   t2.root = y.getRight();
	   }
	   else {
		   t2.root = external;
	   }
	   t2.getRoot().setParent(null);
	   if (t2.getRoot() != external){
		   t2.min = getTempMin(t2.getRoot());
		   t2.max = getTempMax(t2.getRoot());
	   }
	   IAVLNode c = y.getParent();
	   while (c != null && c != external){
		   IAVLNode parent = c.getParent();
		   if (c.getRight() == y){
			   AVLTree temp = new AVLTree();     
			   temp.root = c.getLeft();
			   temp.root.setParent(null);
			   temp.min = getTempMin(temp.root); // to avoid Nullexseption
			   temp.max = getTempMax(temp.root); // to avoid Nullexseption
			   fix_pointer_c(c);
			   temp.join(c,t1);
			   t1 = temp;
			   t1.root.setParent(null);
		   }
		   else {
			   AVLTree temp = new AVLTree();
			   temp.root = c.getRight();
			   temp.root.setParent(null);
			   temp.min = getTempMin(temp.root); // to avoid Nullexseption
			   temp.max = getTempMax(temp.root); // to avoid Nullexseption
			   temp.join(c,t2);
			   t2 = temp;
			   t2.root.setParent(null);
		   }
		   y = c ;
		   c = parent;
	   }
	   AVLTree[] arr_res = new AVLTree[] {t1,t2};
	   return arr_res;
   }

   private IAVLNode getTempMin (IAVLNode x){
	   while (x.getLeft().isRealNode()){
		   x= x.getLeft();
	   }
	   return x;
   }

	private IAVLNode getTempMax (IAVLNode x){
		while (x.getRight().isRealNode()){
			x= x.getRight();
		}
		return x;
	}




   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t) //O(logn)
   {
	 if (this.getRoot() == external) {
		 if (t.getRoot().getKey() != -1) {
			 x.setLeft(external);
			 x.setRight(external);
			 x.setSize(1);
			 this.root = this.min = this.max = x;
			 return 1;
		 } else { // this is empty but t is not.
			 this.root = t.root;
			 this.min = t.min;
			 this.max = t.max;
			 IAVLNode y = t.whereToJoin(this.root, x.getKey());
			 if (y.getKey() < x.getKey()) {
				 y.setRight(x);
				 if (y.getKey() == this.max.getKey()) {
					 this.max = x;
				 }
			 } else {
				 y.setLeft(x);
				 if (y.getKey() == this.min.getKey()) {
					 this.min = x;
				 }
			 }
			 t.SizeAfterJoin(y,1);
			 t.FixJoinRotation(y);
			 return t.getRoot().getHeight() +1;
		 }
	 }
	 else if (t.getRoot().getKey() == -1){
		 IAVLNode y = whereToJoin(this.root , x.getKey() );
		 if (x.getKey() > y.getKey()) {
			 y.setRight(x);
			 if (this.root != external && y !=external && y.getKey() == this.max.getKey()) {
				 this.max = x;
			 }
		 }
		 else {
			 y.setLeft(x);
			 if (this.root != external && y !=external && y.getKey() == this.min.getKey()) {
				 this.min = x;
			 }
		 }
		 SizeAfterJoin (y,1);
		 FixJoinRotation(y);
		 return this.getRoot().getHeight() +1;
		 }

	 int differenceHeight = this.getRoot().getHeight() - t.getRoot().getHeight();
	 String curr_case = whichCase(t, differenceHeight);
	 switch (curr_case){
	   case "smallEqual" :
		   equalJoin(x,t,true);
		   break;
	   case "bigEqual":
		   equalJoin(x,t,false);
		   break;
	   case "smallLeft":
		   leftJoin(x,t,true);
		   break;
	   case "bigerHight":
		   leftJoin(x,t,false);
		   break;
	   case "smallerHight":
		   hightJoin(x,t,false);
		   break;
	   case "biggerLeft":
		   hightJoin(x,t,true);
   }
   int complexity = Math.abs(differenceHeight) +1;
	 return complexity;
	 }


	private String whichCase (AVLTree t, int differenceHeight) {
		boolean smallthent;
		if (this.getRoot().getKey() < t.getRoot().getKey()){
			smallthent = true;
		}
		else {
			smallthent = false;
		}
	   if (smallthent){
		   if (differenceHeight ==0){ return "smallEqual"; }
		   if (differenceHeight > 0) {return "smallerHight"; }
		   if (differenceHeight <0) {return "smallLeft" ; }
		   }
	   if (differenceHeight == 0) {return "bigEqual";}
	   if (differenceHeight >0 ) {return "bigerHight";}
	   return "biggerLeft";
	}

	private void equalJoin (IAVLNode x, AVLTree t , boolean smallerthant){
	   IAVLNode right;
	   IAVLNode left;
	   if (smallerthant){
		   right = t.getRoot();
		   left = this.root;
		   this.max = t.max;
	   }
	   else {
		   right = this.root;
		   left = t.getRoot();
		   this.min = t.min;
	   }
	   this.root = x;
	   this.root.setLeft(left);
	   this.root.setRight(right);
	   left.setParent(x);
	   right.setParent(x);
	   this.root.setSize(this.root.getLeft().getSize() + this.root.getRight().getSize() +1);
	   this.root.setHeight(left.getHeight() +1);
	}

	private void leftJoin (IAVLNode x, AVLTree t , boolean smallerthant){
	   IAVLNode temp_left;
	   IAVLNode temp_right;
	   IAVLNode right;
	   IAVLNode temp_root;
	   if (smallerthant) {
		   temp_left = this.getRoot();
		   right = t.getRoot();
		   temp_root = t.getRoot();
		   this.max = t.max;
	   }
	   else {
		   right = this.getRoot();
		   temp_left = t.getRoot();
		   temp_root = this.getRoot();
		   this.min = t.min;
	   }
	   int currSize = temp_left.getSize() +1;
	   int rank = temp_left.getHeight();
	   IAVLNode c = right;
		temp_right = getRankl(right,rank,c);
	   c.setLeft(x);
	   changePointers(x,c,temp_left,temp_right);
	   x.setHeight(rank+1);
	   x.setSize(temp_right.getSize() + currSize);
	   SizeAfterJoin(c,currSize);
	   this.root = temp_root;
	   FixJoinRotation(c);
	}

	private void hightJoin (IAVLNode x, AVLTree t , boolean smallerthant){
		IAVLNode temp_left;
		IAVLNode temp_right;
		IAVLNode left;
		IAVLNode temp_root;
		if (smallerthant) {
			temp_right = this.getRoot();
			left = t.getRoot();
			temp_root = t.getRoot();
			this.min = t.min;
		}
		else {
			left = this.getRoot();
			temp_right = t.getRoot();
			temp_root = this.getRoot();
			this.max = t.max;
		}
		int tempSize = temp_right.getSize() +1;
		int rank = temp_right.getHeight();
		IAVLNode c = left;
		temp_left = getRankr(left,rank,c);
		c.setRight(x);
		changePointers(x,c,temp_left,temp_right);
		x.setHeight(rank+1);
		x.setSize(temp_left.getSize() + tempSize);
		SizeAfterJoin(c,tempSize);
		this.root = temp_root;
		FixJoinRotation(c);
	}

	private void changePointers(IAVLNode x, IAVLNode c, IAVLNode rank_left, IAVLNode rank_right){
	   x.setParent(c);
	   x.setLeft(rank_left);
	   rank_left.setParent(x);
	   x.setRight(rank_right);
	   rank_right.setParent(x);
	}

	private IAVLNode getRankl (IAVLNode right, int rank, IAVLNode c){
	   while (right.getHeight() > rank){
		   c = right;
		   right = right.getLeft();
	   }
	   return right;
	}

	private IAVLNode getRankr (IAVLNode left, int rank, IAVLNode c){
		while (left.getHeight() > rank){
			c = left;
			left = left.getRight();
		}
		return left;
	}

	private void SizeAfterJoin (IAVLNode c , int size){
	   while (c != null && c.isRealNode()){
		   c.setSize(c.getSize() +size);
		   c = c.getParent();
	   }
	}

	private void FixJoinRotation (IAVLNode y){
		while (y != null){
			int hight_temp = y.getHeight();
			y.setHeight(1+Math.max(y.getLeft().getHeight(),y.getRight().getHeight()));
			y.setSize(1+ y.getLeft().getSize() + y.getRight().getSize());
			int BF = compute_BF(y);
			if (Math.abs(BF) < 2 && hight_temp==y.getHeight()){
				break;
			}
			else if (Math.abs(BF) < 2 && hight_temp != y.getHeight() ){
				y = y.getParent();
			}
			else { // BF ==+-2
				String type = rebalnce(y);
				IAVLNode prevParent = y.getParent();
				rotation(y,type);
				if (y.getParent().getParent() == null){
					this.root = y.getParent();
				}
				y = prevParent;
			}
		}
	}

	private static IAVLNode whereToJoin (IAVLNode x , int key){
	   IAVLNode y = x;
	   while (x.isRealNode()) {
		   y = x;
		   if (key == x.getKey()) {
			   return x;
		   }
		   else {
			   if (key < x.getKey()){
				   x = x.getLeft();
			   }
			   else {
				   x= x.getRight();
			   }
		   }
	   }
	   return y;
	}

	private void fix_pointer_c (IAVLNode c){
	   c.setHeight(0);
	   c.setSize(1);
	   c.setRight(external);
	   c.setLeft(external);
	   c.setParent(null);
	}

	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public int getSize(); //returns the size of the node.
		public void setSize(int size);
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public class AVLNode implements IAVLNode{

	  private int key;
	  private String info;
	  private IAVLNode left;
	  private IAVLNode right;
	  private IAVLNode parent;
	  private int height;
	  private int size;

	  public AVLNode (int key,String info){
		  this.key = key;
		  this.info = info;
		  this.left = external;
		  this.right = external;
		  this.parent = null;
		  this.height = 0;
		  this.size = 1;
	  }

	  public AVLNode (){
		  this.height = -1;
		  this.key = -1;
		  this.size = 0;
	  }
	  public int getKey()
		{
			return this.key;
		}

		public String getValue()
		{
			return this.info;
		}

		public void setLeft(IAVLNode node)
		{
				this.left = node;
		}

		public IAVLNode getLeft()
		{
			return this.left;
		}
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}
		public IAVLNode getRight()
		{
			return this.right;
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node;
		}
		public IAVLNode getParent()
		{
			return this.parent;
		}
		public boolean isRealNode()
		{
			if (this.getKey() != -1){
				return true;
			}
			return false;
		}

	    public void setHeight(int height)
	    {
	      this.height = height;
	    }
	    public int getHeight()
	    {
	      return this.height;
	    }
		public int getSize() {return this.size;}
	  	public void setSize(int size) {this.size = size;}
  }
}


  
