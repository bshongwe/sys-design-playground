package main

// Node represents an element in doubly linked list
type Node struct {
	key, value int
	prev, next *Node
}

type LRUCache struct {
	capacity int
	cache    map[int]*Node
	head     *Node // head (MRU)
	tail     *Node // tail (LRU)
}

func Constructor(capacity int) LRUCache {
	// Initialize nodes
	head := &Node{}
	tail := &Node{}
	head.next = tail
	tail.prev = head

	return LRUCache{
		capacity: capacity,
		cache:    make(map[int]*Node),
		head:     head,
		tail:     tail,
	}
}

func (this *LRUCache) Get(key int) int {
	if node, exists := this.cache[key]; exists {
		// If it exists, move to head
		this.moveToHead(node)
		return node.value
	}
	return -1
}

func (this *LRUCache) Put(key int, value int) {
	if node, exists := this.cache[key]; exists {
		// Update existing node
		node.value = value
		this.moveToHead(node)
	} else {
		// Create new node
		newNode := &Node{key: key, value: value}
		this.cache[key] = newNode
		this.addNode(newNode)

		// Check capacity
		if len(this.cache) > this.capacity {
			// Evict least recently used (node b4 tail)
			lru := this.popTail()
			delete(this.cache, lru.key)
		}
	}
}

// --- Doubly Linked List Helper Methods ---

// addNode inserts node right after head
func (this *LRUCache) addNode(node *Node) {
	node.prev = this.head
	node.next = this.head.next

	this.head.next.prev = node
	this.head.next = node
}

// removeNode removes existing node list
func (this *LRUCache) removeNode(node *Node) {
	prev := node.prev
	next := node.next
	prev.next = next
	next.prev = prev
}

// moveToHead moves node to head
func (this *LRUCache) moveToHead(node *Node) {
	this.removeNode(node)
	this.addNode(node)
}

// popTail removes node right before tail
func (this *LRUCache) popTail() *Node {
	res := this.tail.prev
	this.removeNode(res)
	return res
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * obj := Constructor(capacity);
 * param_1 := obj.Get(key);
 * obj.Put(key,value);
 */
