package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long durationShieldGenerator;

    public R2D2Microservice(long duration) {
        super("R2D2");
        durationShieldGenerator = duration;
    }

    @Override
    protected void initialize() { //===============Not done ================
        subscribeEvent(DeactivationEvent.class, DeactivationEvent -> {
            try {
                Thread.currentThread().sleep(durationShieldGenerator);
            } catch (InterruptedException ex) {
            }
            Diary.getInstance().setFinishTime(this);
            Future f = sendEvent(new BombDestroyerEvent());
        });
    }
}
