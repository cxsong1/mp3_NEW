package cpen221.mp3.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a cache which stores recently accessed objects so they can be accessed faster in the future.
 *
 * Abstraction Function:
 *      Cache is a HashMap mapping a generic object that extends the interface Cacheable
 *      to an array that takes the time that it was stored/refreshed and last accessed. It has a capacity and
 *      timeout value which determine, respectively, the maximum number of items that can be stored in the cache at
 *      once and after how long an item must be removed.
 *
 * Representation Invariant:
 *      capacity and timeout values are greater than 0
 *      cache is not null and does not contain any null key-value sets
 *      for each o in cache.keySet(), cache.get(o) contains values that are greater than 0
 */
public class Cache<T extends Cacheable> {

    /* the default cache size is 32 objects */
    public static final int DSIZE = 32;
    public int capacity;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;
    public int timeout;

    public Map<T, Long[]> cache;

    /**
     * Create a cache with a user-specified capacity and a timeout value.
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
     *    make room for the new object.
     * @param t object to be put in the cache
     * @return true if object is successfully added to the cache and false if the value is already in the cache
     */
    public boolean put(T t) {
        removeExpired();
        Long time = System.currentTimeMillis();
        Long[] times = {time, time};
        if (cache.size() >= capacity){
            // remove least accessed
            removeLeastRecent();
        }
        for (Map.Entry e: cache.entrySet()){
            if (e.getKey() == t)
                return false;
        }
	    cache.put(t, times);
	    return true;
    }

    /**
     * Retrieves and returns the object associated with the id provided
     *
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the cache
     * @throws NoSuchObjectException if there is no object associated with "id" inside the cache
     */
    public T get(String id) throws NoSuchObjectException {
        removeExpired();
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
     * @return true if successful and false if the object to touch
     *          is not contained in the cache
     */
    public boolean touch(String id) {
        removeExpired();
        Long refresh = System.currentTimeMillis();

        for (T t: cache.keySet()){
            if (t.id().equals(id)){
                cache.remove(t);
                Long[] times = {refresh, refresh};
                cache.put(t, times);
                return true;
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
     * @return true if successful and false if item to be updated does not exist in cache
     */
    public boolean update(T t) {
        removeExpired();
        Long[] time = {System.currentTimeMillis(), System.currentTimeMillis()};

        if(this.cache.containsKey(t)) {
            cache.put(t, time);
            return true;
        }

        return false;
    }

    /**
     * When number of items in the cache exceeds capacity, removes least recently accessed object
     */
    private void removeLeastRecent(){
        T leastRecent = null;
        double compare = 0.0;
        Long now = System.currentTimeMillis();
        for (Map.Entry e: this.cache.entrySet()){
            Long[] times = (Long[]) e.getValue();
            Long accessedTime = times[1];
            if ( now - accessedTime > compare ){
                compare = accessedTime;
                leastRecent = (T) e.getKey();
            }
        }
        cache.remove(leastRecent);
    }

    /**
     * Removes the items stored at time with more than a "timeout" value difference from current time
     *
     */
    private void removeExpired(){
        for (Map.Entry e: this.cache.entrySet()){
            Long[] times = (Long[]) e.getValue();
            Long storedTime = times[0];
            if ( System.currentTimeMillis() - storedTime > timeout ){
                cache.remove(e);
            }
        }
    }

}
