package core.file;

import java.io.Serializable;

public class Block implements Serializable {
    public transient int occupied = 0;

    public synchronized void request() {
        occupied++;
    }

    public synchronized void release() {
        occupied--;
    }

    public synchronized boolean free() {
        return occupied == 0;
    }
}
