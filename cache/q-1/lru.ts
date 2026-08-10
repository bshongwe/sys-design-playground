class Node {
    key: number;
    value: number;
    prev: Node | null;
    next: Node | null;

    constructor(key: number, value: number) {
        this.key = key;
        this.value = value;
        this.prev = null;
        this.next = null;
    }
}

class LRUCache {
    private capacity: number;
    private cache: Map<number, Node>;
    private head: Node; // head (MRU side)
    private tail: Node; // tail (LRU side)

    constructor(capacity: number) {
        this.capacity = capacity;
        this.cache = new Map<number, Node>();

        // Initialize nodes
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    get(key: number): number {
        const node = this.cache.get(key);
        if (!node) {
            return -1;
        }
        // Mark as recently used
        this.moveToHead(node);
        return node.value;
    }

    put(key: number, value: number): void {
        const existingNode = this.cache.get(key);

        if (existingNode) {
            // Update existing node
            existingNode.value = value;
            this.moveToHead(existingNode);
        } else {
            // Insert new node
            const newNode = new Node(key, value);
            this.cache.set(key, newNode);
            this.addNode(newNode);

            // Pop if capacity exceeded
            if (this.cache.size > this.capacity) {
                const lru = this.popTail();
                this.cache.delete(lru.key);
            }
        }
    }

    // --- Doubly Linked List Helper Methods ---

    private addNode(node: Node): void {
        // Insert right after head
        node.prev = this.head;
        node.next = this.head.next;

        if (this.head.next) {
            this.head.next.prev = node;
        }
        this.head.next = node;
    }

    private removeNode(node: Node): void {
        const prevNode = node.prev;
        const nextNode = node.next;

        if (prevNode) prevNode.next = nextNode;
        if (nextNode) nextNode.prev = prevNode;
    }

    private moveToHead(node: Node): void {
        this.removeNode(node);
        this.addNode(node);
    }

    private popTail(): Node {
        // LRU node, just before tail
        const res = this.tail.prev as Node;
        this.removeNode(res);
        return res;
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * var obj = new LRUCache(capacity)
 * var param_1 = obj.get(key)
 * obj.put(key,value)
 */