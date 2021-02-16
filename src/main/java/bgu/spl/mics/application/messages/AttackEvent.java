package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private Attack attackEvent1;

    public AttackEvent(Attack a){
        this.attackEvent1=a;
    }

    public List<Integer> getAttackEventSerial(){
        return attackEvent1.getSerials();
    }
    public int getAttackEventSerialDuration(){
        return attackEvent1.getDuration();
    }

}
