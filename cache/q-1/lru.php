<?php

class Node {
    public $key;
    public $value;
    public $prev;
    public $next;

    function __construct($key, $value) {
        $this->key = $key;
        $this->value = $value;
        $this->prev = null;
        $this->next = null;
    }
}

class LRUCache {

    private $capacity;
    private $cache = [];   // key => Node reference -> O(1) lookup
    private $head;         // head (MRU side)
    private $tail;         // tail (LRU side)

    /**
     * @param Integer $capacity
     */
    function __construct($capacity) {
        $this->capacity = $capacity;
        $this->cache = [];

        // Dummy nodes remove the need for null checks at the boundaries
        $this->head = new Node(0, 0);
        $this->tail = new Node(0, 0);
        $this->head->next = $this->tail;
        $this->tail->prev = $this->head;
    }
  
    /**
     * @param Integer $key
     * @return Integer
     */
    function get($key) {
        if (!isset($this->cache[$key])) {
            return -1;
        }

        $node = $this->cache[$key];
        $this->moveToHead($node);
        return $node->value;
    }
  
    /**
     * @param Integer $key
     * @param Integer $value
     * @return NULL
     */
    function put($key, $value) {
        if (isset($this->cache[$key])) {
            $node = $this->cache[$key];
            $node->value = $value;
            $this->moveToHead($node);
            return;
        }

        $newNode = new Node($key, $value);
        $this->cache[$key] = $newNode;
        $this->addNode($newNode);

        if (count($this->cache) > $this->capacity) {
            $lru = $this->popTail();
            unset($this->cache[$lru->key]);
        }
    }

    // --- Doubly Linked List Helper Methods ---

    private function addNode($node) {
        $node->prev = $this->head;
        $node->next = $this->head->next;

        $this->head->next->prev = $node;
        $this->head->next = $node;
    }

    private function removeNode($node) {
        $prevNode = $node->prev;
        $nextNode = $node->next;

        $prevNode->next = $nextNode;
        $nextNode->prev = $prevNode;
    }

    private function moveToHead($node) {
        $this->removeNode($node);
        $this->addNode($node);
    }

    private function popTail() {
        $res = $this->tail->prev;
        $this->removeNode($res);
        return $res;
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * $obj = LRUCache($capacity);
 * $ret_1 = $obj->get($key);
 * $obj->put($key, $value);
 */
