package tcp;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author paso
 * @since 2020/11/14
 */
public class MessagePool {
    String name;
    CopyOnWriteArrayList<Object> list = new CopyOnWriteArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized boolean hasContent() {
        return list.size() > 0;
    } // hasContent

    public synchronized void putMessage(Object msg) {
        list.add(msg);
    } // putMessage

    public synchronized Object getMessage() {
        return list.remove(0);
    } // getMessage
} // MessagePool
