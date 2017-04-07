import java.io.Console;

public class LinearProbingHashMap<Key, Value> {

	// Implements a map (associates a Value with each Key) using a hash table.
	// Collisions are resolved using linear probing ... look for the next empty
	// slot in the table.

	// The hash table is implemented using two parallel arrays: one for the keys
	// and one for the associate values.  The value associated with the key found
	// in keys[i] will be found in values[i].

	// Capacity is the current length of the hash table arrays.  It will be doubled
	// when the load factor exceeds 50%.  The size of the map is the number of keys
	// currently containined in the map.

	private Key[]   keys;		// The keys for the items in the hash map.
	private Value[] values;		// values[i] is the Value associated with Key[i].
	private int     capacity;	// Current size of the arrays (Keys & Values).
	private int     size;		// Number of keys in the hash table


	public LinearProbingHashMap(int capacity) {
		this.keys     = (Key[]) new Object[capacity];
		this.values   = (Value[]) new Object[capacity];
		this.capacity = capacity;
		this.size     = 0;
	}

	public LinearProbingHashMap() {
		this(16);
	}


	public int capacity() {
		return this.capacity;
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}


	public int hash(Key key) {
		// Returns the hash of this key wrt the table size.
		return (key.hashCode() & 0x7FFFFFFF) % this.capacity;
	}

	private int increment(int index) {
		// Increments the index mod the capacity of the map.
		return (++index < this.capacity) ? index : 0;
	}

	private int locate(Key key) {
		// Returns the slot in which this key is to be found.
		int index = hash(key);
		while(this.keys[index] != null && !this.keys[index].equals(key)) {
			index = increment(index);
		}
		return index;
	}


	public boolean contains(Key key) {
		int index = locate(key);
		return this.keys[index] != null;
	}


	public Value find(Key key) {
		int index = locate(key);
		if (this.keys[index] != null) {
			return this.values[index];
		} else {
			return null;
		}
	}


	public void add(Key key, Value value) {

		// Resize the hash table if the load factor exceeds 50%.

		if (2 * this.size > this.capacity) {
			resize(2 * this.capacity);
		}

		// Add this item to the hash table in the expected location.

		int index = locate(key);
		this.keys[index] = key;
		this.values[index] = value;
		this.size++;
	}


	public void remove(Key key) {

		// Locate the item and remove it.

		if (!contains(key)) return;
		int index = locate(key);
		keys[index] = null;
		values[index] = null;
		this.size--;

		// Check to see if any of the items in the table that follow
		// this one had collided with the key that was just removed.
		// If so, we need to rehash/reinsert them.

		int i = increment(index);
		while (keys[i] != null) {

			// Save the key/value pair at this location.

			Key savedKey = keys[i];
			Value savedValue = values[i];

			// And temporarily remove it from the hash map.

			keys[i] = null;
			values[i] = null;

			// Figure out where it should go now and reinsert it.

			index = locate(savedKey);
			keys[index] = savedKey;
			values[index] = savedValue;

			// Advance to the next item.
			i = increment(i);
		}
	}


	private void resize(int capacity) {

		// To resize the hash table, we simply allocate a new table of
		// the appropriate size and rehash/reinsert all of the items.

		// Keep track of the old contents of the hash table.

		Key[] oldKeys = keys;
		Value[] oldValues = values;
		int oldCapacity = this.capacity;

		// Allocate the new hash table.

		this.keys = (Key[]) new Object[capacity];
		this.values = (Value[]) new Object[capacity];
		this.capacity = capacity;

		// For each item in the old map, rehash and then 
		// insert it into the newly allocated tables.

		for (int i = 0;  i < oldCapacity; i++) {
			Key key = oldKeys[i];
			Value value = oldValues[i];
			if (key != null) {
				int index = locate(key);
				this.keys[index] = key;
				this.values[index] = value;
			}
		}
	}


	public void print() {
		for (int i = 0; i < this.capacity; i++) {
			System.out.print(i + ": ");
			if (keys[i] != null) {
				System.out.print(keys[i]);
				if (values[i] != null) {
					System.out.print(" = " + values[i]);
				}
			}
			System.out.println();
		}
	}


    private static String getArgument(String line, int index) {
        String[] words = line.split("\\s");
        return words.length > index ? words[index] : "";
    }

	private static String getCommand(String line) {
		return getArgument(line, 0);
	}


    public static void main(String[] args) {
		LinearProbingHashMap<String, String> map = new LinearProbingHashMap<>();
		Console console = System.console();

		if (console == null) {
			System.err.println("No console");
			return;
		}

        // Allow the user to enter commands on standard input:
        //
        //   contains <key>    prints true if a key is in the map; false if not
		//   find <key>        prints the value associated with the key
        //   add <key> <value> adds an item to the tree
        //   remove <key>      removes an item from the tree (if present)
        //   clear             removes all items from the tree
        //   print             prints the contents of the hash table
        //   exit              quit the program


        String line = console.readLine("Command: ").trim();
        while (line != null) {
            String command = getCommand(line);
            String arg = getArgument(line, 1);

            switch(command) {
				case "hash":
					System.out.println(arg.hashCode());
					break;

				case "index":
					System.out.println(map.hash(arg));
					break;

				case "size":
					System.out.println(map.size());
					break;

				case "capacity":
					System.out.println(map.capacity());
					break;

				case "contains":
                    System.out.println(map.contains(arg));
                    break;

				case "find":
					System.out.println(map.find(arg));
					break;

                case "add":
                case "insert":
                    map.add(arg, getArgument(line, 2));
                    break;

				case "delete":
				case "remove":
					map.remove(arg);
					break;

                case "print":
                    map.print();
                    break;

				case "clear":
					map = new LinearProbingHashMap<>();
					break;

                case "end":
				case "exit":
                case "quit":
                    return;

                default:
                    System.out.println("Invalid command: " + command);
                    break;
            }

            line = console.readLine("Command: ").trim();
        }
    }
}


