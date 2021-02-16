package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {


    // data members
    private long C3POFinish;
    private long C3POTerminate;
    private long HanSoloFinish;
    private long HanSoloTerminate;
    private long R2D2Deactivate;
    private long R2D2Terminate;
    private long LandoTerminate;
    private long LeiaTerminate;
    private AtomicInteger totalAttacks;


    //singleton impl
    private Diary(){
        C3POFinish=0;
        C3POTerminate=0;
        HanSoloFinish=0;
        HanSoloTerminate=0;
        R2D2Deactivate=0;
        R2D2Terminate=0;
        LandoTerminate=0;
        LeiaTerminate=0;
        totalAttacks =new AtomicInteger(0);
    }



    private static class DiaryHolder {
        private static Diary instance = new Diary();
    }
    public static Diary getInstance(){
        return DiaryHolder.instance;
    }

    //methods
    public void addAttack(){
        int currNum;
        do{
            currNum= totalAttacks.get();
        }
        while (!totalAttacks.compareAndSet(currNum,currNum+1));
    }

    public void setFinishTime(MicroService microService) {
        if (microService instanceof C3POMicroservice) {
            C3POFinish = System.currentTimeMillis();
        } else if (microService instanceof HanSoloMicroservice) {
            HanSoloFinish = System.currentTimeMillis();
        } else if (microService instanceof R2D2Microservice) {
            R2D2Deactivate = System.currentTimeMillis();
        }
    }

        public void setTerminationTime(MicroService microService){
            if (microService instanceof C3POMicroservice){
                C3POTerminate=System.currentTimeMillis();
            }
            else if (microService instanceof HanSoloMicroservice){
                HanSoloTerminate=System.currentTimeMillis();
            }
            else if (microService instanceof R2D2Microservice){
                R2D2Terminate=System.currentTimeMillis();
            }
            else if (microService instanceof LeiaMicroservice){
                LeiaTerminate =System.currentTimeMillis();
            }
            else if (microService instanceof LandoMicroservice){
                LandoTerminate=System.currentTimeMillis();
            }


    }
    // getter just fot tests
    public void resetNumberAttacks() {
        totalAttacks =new AtomicInteger(0);
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public AtomicInteger getNumberOfAttacks() {
        return totalAttacks;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }
    public long getC3POFinish(){
        return C3POFinish;
    }


}
