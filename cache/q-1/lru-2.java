import java.util.HashMap;
import java.util.Map;

class LRUCache {

    // 1. Doubly Linked List Node
    // Stores actual data + pointers for "Timeline"
    private class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    // 2. The System Components
    private final int capacity;
    private final Map<Integer, Node> map; // "Search Engine" (O(1) Access)
    private final Node head;             // head (MRU side)
    private final Node tail;             // tail (LRU side)

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Initialize Nodes
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    // 3. The Combined get() logic
    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        // Step A: Use Map to find node in O(1)
        Node node = map.get(key);
        // Step B: Use List to move it to
        // "Most Recently Used" position in O(1)
        moveToHead(node);
        return node.value;
    }

    // 4. Combined put() logic
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            // -- Update existing:
            //  * Find via Map
            //  * Update Value
            //  * Move to Head
            Node node = map.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            // -- Insert new:
            //  * Create Node
            //  * Add to Map
            //  * Add to Head
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addNode(newNode);

            // -- Evict if capacity exceeded
            if (map.size() > capacity) {
                // * Find LRU node via tail pointer in O(1)
                Node lru = popTail();
                // * Remove it from Map in O(1)
                map.remove(lru.key);
            }
        }
    }

    // --- Doubly Linked List Helper Methods ("Timeline" Logic) ---

    private void addNode(Node node) {
        // Always adds right after head
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        // Snips node out of timeline
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }

    private Node popTail() {
        // Node before tail, actual Least Recently Used
        Node res = tail.prev;
        removeNode(res);
        return res;
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */