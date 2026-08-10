
// ------- Problem -----------
// An LFU Cache evicts based on frequency of use, not recency. But there's a crucial
// twist: ties are broken by recency (LRU-style). So this problem is really
// "LRU Cache" + "an extra dimension: frequency count."

// Every key tracks:
//    1. Its value
//    2. Its use counter (cnt)

// Eviction Rule:
//    * Find the key(s) with the smallest cnt.
//    * If there's a tie, evict the least recently used among those tied keys.

// So we need to simultaneously answer three questions in O(1):
//    1. "What's the value for this key?" (like LRU)
// 	  2. "What's the minimum frequency currently in the cache?"
//    3. "Among keys with that minimum frequency, which one was used longest ago?"

// Possible Solution Approaches
//  1. Brute Force: HashMap + sort/scan on eviction

// 	  * Store key -> (value, count, timestamp) in a hash map.
// 	  * On eviction, scan all entries to find min count (breaking ties by oldest timestamp).
// 	  * Problem: Eviction is O(n). Fails the O(1) requirement.

//  2. HashMap + Min-Heap (Priority Queue)

// 	  * Store (count, timestamp, key, value) in a min-heap ordered by (count, timestamp).
// 	  * Problem: Heap insert/update is O(log n), not O(1). Close, but doesn't satisfy the
//               strict O(1) requirement (though it's a very common "good enough"
//                solution in interview answer).

//  3. HashMap + HashMap of Doubly Linked Lists (The Gold Standard)
// This is the classic O(1) solution. It generalizes our LRU trick by using one doubly
// linked list per frequency count, instead of just one global list.

// 	* keyMap: key -> Node (value, count) — for O(1) value/count lookup, just like LRU.
// 	* freqMap: frequency -> DoublyLinkedList of Nodes — each frequency bucket is its own
//             mini "LRU list"! The head of each list = most recently used at that frequency,
//             tail = least recently used at that frequency.
// 	* minFreq: an integer tracking the current minimum frequency in the whole cache.

// How operations work:
// 	* get(key): Look up node in keyMap (O(1)). Remove it from its current frequency's list,
//             then increment its count, and add it to the front of the new frequency's list
//             (O(1)). Update minFreq if the old frequency's list is now empty and it was the
//             minFreq.
// 	* put(key, value): Same idea — if key exists, update value + bump frequency like get. If
// new and at capacity, evict the tail node from the minFreq bucket's list (O(1)), then
// insert the new key at frequency 1, and reset minFreq = 1.

// ------------------------------------------------------------------
// ------------------------------------------------------------------

// The Best Approach: HashMap + HashMap of Doubly Linked Lists
// Why?: This achieves true O(1) for both get and put, satisfying the problem's explicit
// requirement.

// Think of it as "buckets of LRU caches":

// 	* Each frequency count (1, 2, 3, ...) has its own Doubly Linked List.
// 	* Moving a key from frequency f to f+1 is just: unlink from list f, link to front of list
//    f+1 — both O(1) pointer operations.
// 	* Tracking minFreq as a simple integer lets us instantly jump to the correct bucket for
//    eviction, avoiding any search.

// The Min-Heap approach (#2) is a very respectable "Medium-Hard" interview answer and much
// simpler to code, but it's technically O(log n), so it doesn't meet this problem's strict
// constraint the way the double-hashmap approach does.

import java.util.HashMap;
import java.util.Map;

class LFUCache {

    private class Node {
        int key, value, freq;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    // Doubly linked list -> all nodes with same freq
    private class DLList {
        Node head, tail;
        int size;

        DLList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
            size = 0;
        }

        void addFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        Node removeTail() {
            if (size == 0) return null;
            Node res = tail.prev;
            remove(res);
            return res;
        }
    }

    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyMap;
    private final Map<Integer, DLList> freqMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    public int get(int key) {
        Node node = keyMap.get(key);
        if (node == null) return -1;

        touch(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        Node node = keyMap.get(key);
        if (node != null) {
            node.value = value;
            touch(node);
            return;
        }

        // Insert new key
        if (keyMap.size() >= capacity) {
            // Evict from minFreq bucket's tail (true LFU + LRU tiebreak)
            DLList minList = freqMap.get(minFreq);
            Node evict = minList.removeTail();
            keyMap.remove(evict.key);
        }

        Node newNode = new Node(key, value);
        keyMap.put(key, newNode);

        freqMap.computeIfAbsent(1, f -> new DLList()).addFront(newNode);
        minFreq = 1;
    }

    // Moves node from current freq bucket to next one
    private void touch(Node node) {
        int oldFreq = node.freq;
        DLList oldList = freqMap.get(oldFreq);
        oldList.remove(node);

        // If minFreq bucket is empty, bump minFreq
        if (oldList.size == 0 && minFreq == oldFreq) {
            minFreq++;
        }

        node.freq++;
        freqMap.computeIfAbsent(node.freq, f -> new DLList()).addFront(node);
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */