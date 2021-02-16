package bgu.spl.mics.application.services;

//import java.util.ArrayList;

import java.util.List;

//import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private Future<Boolean>[] futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.futures = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
        int i = 0;
        for (Attack toAttack : attacks) {
            AttackEvent attackEventToSend = new AttackEvent(toAttack);
            Future<Boolean> returnFuture = sendEvent(attackEventToSend);
            futures[i] = returnFuture;
            i++;
        }
        for (Future<Boolean> isFInished : futures) {
            isFInished.get();
        }
        Future f = sendEvent(new DeactivationEvent());
    }
}















