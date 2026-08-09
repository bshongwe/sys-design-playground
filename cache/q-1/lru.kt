class LRUCache(private val capacity: Int) {

    // Doubly Linked List Node
    private class Node(val key: Int, var value: Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    // "Search Engine"
    private val cache = mutableMapOf<Int, Node>()
    
    // "Timeline" (head & tail)
    private val head = Node(0, 0)
    private val tail = Node(0, 0)

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {
        val node = cache[key] ?: return -1
        // If found, move to head
        moveToHead(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        val existingNode = cache[key]
        if (existingNode != null) {
            // Update existing node
            existingNode.value = value
            moveToHead(existingNode)
        } else {
            // Add new node
            val newNode = Node(key, value)
            cache[key] = newNode
            addNode(newNode)

            // Evict if capacity exceeded
            if (cache.size > capacity) {
                val lru = popTail()
                cache.remove(lru.key)
            }
        }
    }

    // --- Doubly Linked List Helper Methods ---

    private fun addNode(node: Node) {
        // Always adds right after head
        node.prev = head
        node.next = head.next
        
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        val prevNode = node.prev
        val nextNode = node.next
        
        prevNode?.next = nextNode
        nextNode?.prev = prevNode
    }

    private fun moveToHead(node: Node) {
        removeNode(node)
        addNode(node)
    }

    private fun popTail(): Node {
        // Real LRU node, one just before tail
        val res = tail.prev!!
        removeNode(res)
        return res
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * var obj = LRUCache(capacity)
 * var param_1 = obj.get(key)
 * obj.put(key,value)
 */