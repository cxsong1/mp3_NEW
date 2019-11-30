package cpen221.mp3;

import cpen221.mp3.cache.Cacheable;

public class Francis implements Cacheable {

    String id;

    // this constructor creates a default Francis object
    public Francis(){
        this.id = "default";
    }

    // this constructor takes a String id and
    public Francis(String id){
        this.id = id;
    }

    public String id(){
        return new String(id);
    }
}
