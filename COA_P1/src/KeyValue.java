import java.util.Map;
/**
 * 
 *This is hashmap class which stores every register's details 
 * @param <K> keys such as R0 R1 R2 ...
 * @param <V> values such as 0,1,2....
 */
final class KeyValue<K, V> implements Map.Entry<K, V> {
    private V regValue;
    private final K registerName;


    public KeyValue(K registerName, V regValue) {
        this.registerName = registerName;
        this.regValue = regValue;
    }

    @Override
    public K getKey() {
        return registerName;
    }

    @Override
    public V getValue() {
        return regValue;
    }

    @Override
    public V setValue(V value) {
        V lastValue = this.regValue;
        this.regValue = value;
        return lastValue;
    }
}