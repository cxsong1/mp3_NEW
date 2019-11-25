package cpen221.mp3.cache;

import javax.lang.model.element.Element;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cache<T extends Cacheable> {

    /* the default cache size is 32 objects */
    public static final int DSIZE = 32;
    public int capacity;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;
    public int timeout;

    /* TODO: Implement this datatype */
    Map<T, Date> cache;

    /**
     * Create a cache with a fixed capacity and a timeout value.
     * Objects in the cache that have not been refreshed within the timeout period
     * are removed from the cache.
     *
     * @param capacity the number of objects the cache can hold
     * @param timeout  the duration an object should be in the cache before it times out
     */
    public Cache(int capacity, int timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
        this.cache= new HashMap<>(capacity);
    }

    /**
     * Create a cache with default capacity and timeout values.
     */
    public Cache() {
        this(DSIZE, DTIMEOUT);
        this.cache = new HashMap<>(DSIZE);
    }

    /**
     * Add a value to the cache.
     * If the cache is full then remove the least recently accessed object to
     * make room for the new object.
     */
    public boolean put(T t) {
        // TODO: implement this method
        Date time = new Date(System.currentTimeMillis());
        cache.put(t, time);
        return false;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the cache
     */
    public T get(String id) throws NoSuchObjectException {
        /* TODO: change this */
        /* Do not return null. Throw a suitable checked exception when an object
            is not in the cache. */
        for (T t: cache.keySet()){
            if (t.id().equals(id)){
               return t;
            }
        }
        throw new NoSuchObjectException();
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its timeout
     * is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public boolean touch(String id) {
        /* TODO: Implement this method */
        Date refresh = new Date(System.currentTimeMillis());

        for (T t: cache.keySet()){
            if (t.id().equals(id)){
                cache.remove(t);
                cache.put(t, refresh);
                // check if successful
                if (cache.containsKey(refresh)){
                    if (cache.get(refresh).equals(t))
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Update an object in the cache.
     * This method updates an object and acts like a "touch" to renew the
     * object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public boolean update(T t) {
        /* TODO: implement this method */
        for (T find: cache.keySet()){
            if (t.id().equals(find.id())){
                // do something to update t, put new t
                // mutable, do not need to put again maybe?
                find = t;
                // check if it's updated, return true if so
                return true;
            }
        }
        return false;
    }

}
