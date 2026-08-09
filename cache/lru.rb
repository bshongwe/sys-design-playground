class LRUCache

=begin
    :type capacity: Integer
=end
    def initialize(capacity)
        @capacity = capacity
        @cache = {}
        
        # Dummy nodes to make pointer management easier
        @head = Node.new(0, 0)
        @tail = Node.new(0, 0)
        @head.next = @tail
        @tail.prev = @head
    end


=begin
    :type key: Integer
    :rtype: Integer
=end
    def get(key)
        if @cache.key?(key)
            node = @cache[key]
            move_to_head(node)
            return node.val
        end
        return -1
    end


=begin
    :type key: Integer
    :type value: Integer
    :rtype: Void
=end
    def put(key, value)
        if @cache.key?(key)
            node = @cache[key]
            node.val = value
            move_to_head(node)
        else
            new_node = Node.new(key, value)
            @cache[key] = new_node
            add_node(new_node)
            
            if @cache.size > @capacity
                lru_node = pop_tail
                @cache.delete(lru_node.key)
            end
        end
    end

    private

    # Internal Node class for the Doubly Linked List
    class Node
        attr_accessor :key, :val, :prev, :next
        def initialize(key, val)
            @key = key
            @val = val
            @prev = nil
            @next = nil
        end
    end

    def add_node(node)
        node.prev = @head
        node.next = @head.next
        @head.next.prev = node
        @head.next = node
    end

    def remove_node(node)
        prev_node = node.prev
        next_node = node.next
        prev_node.next = next_node
        next_node.prev = prev_node
    end

    def move_to_head(node)
        remove_node(node)
        add_node(node)
    end

    def pop_tail
        res = @tail.prev
        remove_node(res)
        return res
    end

end

# Your LRUCache object will be instantiated and called as such:
# obj = LRUCache.new(capacity)
# param_1 = obj.get(key)
# obj.put(key, value)