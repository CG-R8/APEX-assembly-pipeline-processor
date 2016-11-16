import java.util.Map;
/**
 * 
 *This is hashmap which stores register details 
 * @param <K> like R0 R1 R2 ...
 * @param <V> like 0,1,2....
 */
final class KeyValue<K, V> implements Map.Entry<K, V> {
	
    private final K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}