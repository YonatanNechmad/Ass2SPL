package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent) -> {
            Ewoks ewoks = Ewoks.getInstance();
            if (ewoks.acquireEwoks(attackEvent.getAttackEventSerial())) {
                try {
                    Thread.sleep(attackEvent.getAttackEventSerialDuration());
                } catch (InterruptedException e) {
                }

                complete(attackEvent, true);
                ewoks.toReleaseEwoks(attackEvent.getAttackEventSerial());
                Diary.getInstance().addAttack();
                Diary.getInstance().setFinishTime(this);
            } else {
                complete(attackEvent, false);
            }
        });


    }
}

