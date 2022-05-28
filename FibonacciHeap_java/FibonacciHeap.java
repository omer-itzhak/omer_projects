/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    public static int numberOfLINKS;
    public static int numberOfCUTS;

    private int size;
    private HeapNode head;
    private HeapNode min;
    private int numberOfMarked;
    private int numberOfTrees;

    public FibonacciHeap() { //O(1)
        head = null;
        min = null;

    }

    /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() // O(1)
    {
        return (this.head ==null);
    }

    public HeapNode getHead(){
        return this.head;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key) //O(1)
    {

        HeapNode node = new HeapNode(key);
        this.size +=1;
        this.numberOfTrees +=1;
        if (this.isEmpty()) {
            this.head = node;
            this.min = node;
            return node;
        }

        if (key < this.min.key){
            this.min =node;
        }

        HeapNode temp = this.head;
        this.head = node;
        node.prev = temp.prev;
        node.next = temp;
        temp.prev = node;
        node.prev.next = node;
        return node;

    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
   public void deleteMin() //O(logn)
   {
       if (this.isEmpty() || this.size == 1) {
           this.min = null;
           this.head = null;
           this.size = 0;
           this.numberOfTrees = 0;
           return;
       }
       int sizeBuckets = log2(this.size) + 1;
       HeapNode[] bucketsArr = new HeapNode[sizeBuckets];
       this.size -=1;

       HeapNode node = this.head;
       int oldNumberOfTrees = numberOfTrees;
       int treesToAdd = min.rank;
       int i = 0;
       int j = 0;

       while (i < oldNumberOfTrees) {
           if (node == this.min) {
               HeapNode child = this.min.child;
               while (j < treesToAdd) {
                   if (child.marked == 1) {
                       child.marked = 0;
                       this.numberOfMarked -=1;
                   }
                   HeapNode next = child.next;
                   child.parent = null;
                   child.prev = child;
                   child.next = child;
                   //create the links to min's chlidren
                   while (bucketsArr[child.rank] != null) {
                       child = linkHeaps(bucketsArr[child.rank], child);
                       bucketsArr[child.rank-1] = null;
                   }
                   bucketsArr[child.rank] = child;
                   child = next;
                   j += 1;
               }
               node = node.next;
               i +=1;
           }
           else {
               HeapNode next = node.next;
               node.parent = null;
               node.prev = node;
               node.next = node;
               while (bucketsArr[node.rank] != null) {
                   node = linkHeaps(bucketsArr[node.rank], node);
                   bucketsArr[node.rank-1] = null;
               }
               bucketsArr[node.rank] = node;
               node = next;
               i += 1;
           }
       }
       this.head = null;
       this.numberOfTrees = 0;
       creatLegalHeap(bucketsArr);
   }

    private void creatLegalHeap(HeapNode[] bucketsArr) {
        HeapNode node = this.head;
        // iterate over buckets
        for (HeapNode bucket : bucketsArr) {
            if (bucket != null) {
                this.numberOfTrees +=1;
                if (this.head == null) {
                    this.head = bucket;
                    node = this.head;
                    this.min = this.head;
                    continue;
                }
                node.next = bucket;
                bucket.prev = node;
                node = node.next;
                if (node.key < min.key){
                    this.min = node;
                }
            }
        }
        node.next = this.head;
        this.head.prev = node;
    }

    private HeapNode linkHeaps(HeapNode oldRoot, HeapNode newRoot) {
        HeapNode smallKey;
        HeapNode bigKey;
        if (oldRoot.getKey() > newRoot.getKey()){
            bigKey =oldRoot;
            smallKey = newRoot;
        }
        else{
            bigKey = newRoot;
            smallKey = oldRoot;
        }
        if (smallKey.child == null) {
            smallKey.child = bigKey;
        }
        else {
            HeapNode highChild = smallKey.child;
            smallKey.child = bigKey;
            bigKey.next = highChild;
            highChild.prev.next = bigKey;
            bigKey.prev = highChild.prev;
            highChild.prev = bigKey;
        }
        bigKey.parent = smallKey;
        smallKey.rank += 1;
        numberOfLINKS += 1;
        numberOfTrees -= 1;
        return smallKey;
    }

    public static int log2 (int x){
        return (int) (Math.log(x) / Math.log(2));
    }




    /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() //O(1)
    {
        if (this.isEmpty()){
            return null;
        }
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  if (this.isEmpty()){
              if (!heap2.isEmpty()){
                  this.head = heap2.head;
                  this.min = heap2.findMin();
                  this.size = heap2.size();
                  this.numberOfTrees = heap2.numberOfTrees;
                  this.numberOfMarked = heap2.numberOfMarked;
              }
          }
          else {
              if (!heap2.isEmpty()) {
                  this.numberOfTrees += heap2.numberOfTrees;
                  this.size += heap2.size();
                  this.numberOfMarked += heap2.numberOfMarked;
                  if (this.findMin().getKey() > heap2.findMin().getKey()) {
                      this.min = heap2.findMin();
                  }
                  HeapNode last = heap2.head.prev;
                  HeapNode first = this.head;

                  last.next = first;
                  first.prev.next = heap2.head;
                  heap2.head.prev = first.prev;
                  first.prev =last;
              }
          }

    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() {return this.size;} //O(1)

    public int getNumberOfTrees() { return numberOfTrees ;}

    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep()
    {
    	if (this.isEmpty()){return new int[0];}

        int[] arr = new int[log2(this.size) +1];
        HeapNode curr = this.head;
        arr[curr.rank] +=1;
        curr =curr.next;
        while (curr != this.head){
            arr[curr.rank] +=1;
            curr = curr.next;
        }
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {
        this.decreaseKey(x, Integer.MAX_VALUE);
        this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key -= delta;
        if (x.key < min.key){
            this.min = x;
        }
        if ((x.parent == null) || (x.parent.key < x.key)){
            return;
        }
        cascadingCuts(x);
    }

    private void cascadingCuts(HeapNode x){
        HeapNode parentOfX = x.parent;
        cutNode(x);
        if (parentOfX.parent == null) {
            return;
        }
        else if (parentOfX.marked == 0){
            numberOfMarked +=1;
            parentOfX.marked = 1;
            return;
        }
        cascadingCuts(parentOfX);
    }

    private void cutNode(HeapNode x){
        HeapNode parentOfX = x.parent;
        parentOfX.rank -= 1;
        if (x.marked == 1){
            x.marked = 0 ;
            numberOfMarked -= 1;
        }
        if (x.next == x) { //x has no brothers
            parentOfX.child = null;
        }
        else { //x has brothers
            if (parentOfX.child == x){
                parentOfX.child = x.next;
            }
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }
        x.parent =null;
        x.next =this.head;
        this.head.prev.next =x;
        x.prev = this.head.prev;
        this.head.prev =x;
        this.head = x;
        numberOfTrees +=1;
        numberOfCUTS +=1;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() { return numberOfTrees + 2*numberOfMarked; } //O(1)

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks() { //O(1)
        int res = numberOfLINKS;
        return res ;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() //O(1)
    {    
    	int res = numberOfCUTS;
        return res;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        if (k<=0){
            return new int[0];
        }
        int[] resArr = new int[k];
        if (k ==1){
            resArr[0] = H.min.getKey();
            return resArr;
        }
        FibonacciHeap tempHeap = new FibonacciHeap();
        tempHeap.insert(H.min.key);
        tempHeap.min.currParent = H.min;
        for (int i =0 ; i<k ; i++){
            resArr[i] = tempHeap.min.key;
            HeapNode currChild = tempHeap.min.currParent.child;
            tempHeap.deleteMin();
            if (currChild != null && currChild.key != resArr[i]) {
                HeapNode curr = currChild;
                while (currChild.next != curr){
                    HeapNode tempInsert = tempHeap.insert(currChild.key);
                    tempInsert.currParent = currChild;
                    currChild = currChild.next;
                }
                HeapNode tempInsert = tempHeap.insert(currChild.key);
                tempInsert.currParent = currChild;
            }
        }
        return resArr;
    }

   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        private int rank;
        private int marked;
        private HeapNode parent;
        private HeapNode next;
        private HeapNode child;
        private HeapNode prev;
        private HeapNode currParent;

    	public HeapNode(int key) {
            this.key = key;
            this.child = null;
            this.parent = null;
            this.next = this;
            this.prev = this;
            this.marked = 0;
            this.rank = 0;
            this.currParent = null;
    	}

    	public int getKey() {
    		return this.key;
    	}
    }
}
