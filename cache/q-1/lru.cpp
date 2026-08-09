#include <unordered_map>
using namespace std;

class LRUCache {
private:
    struct Node {
        int key;
        int value;
        Node* prev;
        Node* next;
        Node(int k, int v) : key(k), value(v), prev(nullptr), next(nullptr) {}
    };

    int capacity;
    std::unordered_map<int, Node*> cache; // Maps key -> Node pointer
    Node* head; // head
    Node* tail; // tail

    // ----------------------------------------------
    // HELPER FUNCTIONS
    // ----------------------------------------------

    // #1. Helper function to add node right after head
    void addNode(Node* node) {
        node->next = head->next;
        node->prev = head;
        head->next->prev = node;
        head->next = node;
    }

    // #2. Helper function to remove existing node from list
    void removeNode(Node* node) {
        Node* prevNode = node->prev;
        Node* nextNode = node->next;
        prevNode->next = nextNode;
        nextNode->prev = prevNode;
    }

    /** #3. Helper function to move node to head,
    *   marks it as most recently used
    */
    void moveToHead(Node* node) {
        removeNode(node);
        addNode(node);
    }

    /** #4. Helper function to pop least recently used node,
    *   one before tail
    */
    Node* popTail() {
        Node* res = tail->prev;
        removeNode(res);
        return res;
    }

//////////////////////////////////////////////
/////////////////////////////////////////////
public:
    LRUCache(int capacity) : capacity(capacity) {
        // #1. Initialize head & tail
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head->next = tail;
        tail->prev = head;
    }
    
    int get(int key) {
        if (cache.find(key) == cache.end()) {
            return -1;
        }
        // #2. If key exists, move it to head, return value
        Node* node = cache[key];
        moveToHead(node);
        return node->value;
    }
    
    void put(int key, int value) {
        if (cache.find(key) != cache.end()) {
            // #3. If key exists, update value, move to head
            Node* node = cache[key];
            node->value = value;
            moveToHead(node);
        } else {
            // #4. If key is new, create new node
            Node* newNode = new Node(key, value);
            cache[key] = newNode;
            addNode(newNode);

            // #5. If capacity exceeded, evict LRU item
            if (cache.size() > capacity) {
                Node* lru = popTail();
                cache.erase(lru->key);
                delete lru; // Free memory
            }
        }
    }
};

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache* obj = new LRUCache(capacity);
 * int param_1 = obj->get(key);
 * obj->put(key,value);
 */