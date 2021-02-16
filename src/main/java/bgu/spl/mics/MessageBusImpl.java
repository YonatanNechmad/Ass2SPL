package bgu.spl.mics;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */


public class MessageBusImpl implements MessageBus {


    // singleton implement
    private static class singletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        Qmsg = new ConcurrentHashMap<>();
        Qmicro = new ConcurrentHashMap<>();
        EventToFuture = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return singletonHolder.instance;
    }

    // data members

    //hold the type of MSG --> Queue of all Microservice are relate to this type .
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> Qmsg;
    // hold the Microservice --> Queue of all his message .
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> Qmicro;
    // hold the Event --> his future .
    private ConcurrentHashMap<Event, Future> EventToFuture;

    @Override
    public void register(MicroService m) {
        LinkedBlockingQueue<Message> toInsert = new LinkedBlockingQueue<Message>();
        Qmicro.putIfAbsent(m, toInsert);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> currMQueue = Qmicro.get(m);
        if (currMQueue == null) {
            throw new NoSuchElementException();
        }
        return currMQueue.take();
    }

    @Override
    public void unregister(MicroService m) {
        if (Qmicro.containsKey(m)) {
            LinkedBlockingQueue<Message> Qeuemsgtodelet;
            // remove m Microservice from  "MSG -> Queue of all Microservice are relate to this type"
            Set<Class<? extends Message>> keys = Qmsg.keySet();
            for (Class<? extends Message> MsgType : keys) {
                ConcurrentLinkedQueue<MicroService> subsQ = Qmsg.get(MsgType);
                synchronized ((MsgType)) {
                    if (subsQ.contains(m))
                        subsQ.remove(m);
                }
            }
            Qeuemsgtodelet = Qmicro.remove(m);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = EventToFuture.get(e);
        if (future != null) {
            future.resolve(result);
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        MicroService m;
        ConcurrentLinkedQueue<MicroService> subsEvent;
        Future future = new Future();
        synchronized (e.getClass()) {
            subsEvent = Qmsg.get(e.getClass());
            if (subsEvent == null || subsEvent.isEmpty()) {
                return null;
            }
            m = subsEvent.poll();
            subsEvent.add(m);

            LinkedBlockingQueue<Message> microMSG = Qmicro.get(m);
            EventToFuture.put(e, future);
            try {
                microMSG.put(e);
            } catch (InterruptedException exception) {
            }
            return future;
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> goOverQueue = Qmsg.get(b.getClass());
        synchronized (b.getClass()){
            if (goOverQueue != null && !goOverQueue.isEmpty()) {
                for (MicroService x : goOverQueue) {
                    LinkedBlockingQueue cuurMsgOfM = Qmicro.get(x);
                    try {
                        if (cuurMsgOfM != null) {
                            cuurMsgOfM.put(b);
                        }
                    } catch (InterruptedException exception) {}
                }
            }
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (type.getClass()){
            Qmsg.putIfAbsent(type, new ConcurrentLinkedQueue<>());
            ConcurrentLinkedQueue<MicroService> toInsert = Qmsg.get(type);
            if (!toInsert.contains(m)) {
                toInsert.add(m);
            }
        }
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) { //Synchronized ? yonatan
        synchronized ((type.getClass())){
            Qmsg.putIfAbsent(type, new ConcurrentLinkedQueue<>());
            ConcurrentLinkedQueue<MicroService> toInsert = Qmsg.get(type);
            if (!toInsert.contains(m)) {
                toInsert.add(m);
            }
        }
    }

}