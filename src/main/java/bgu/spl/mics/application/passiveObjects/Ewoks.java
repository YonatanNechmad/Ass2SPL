package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    // singleton
    private static class singletonEwokHolder {
        private static Ewoks instance = new Ewoks();
    }

    private Ewoks() {
        ewoksArr = new Ewok[1];
        numOfEwoks = 0;
    }

    public synchronized static Ewoks getInstance() {
        return singletonEwokHolder.instance;
    }

    // data members
    private Ewok[] ewoksArr;
    private int numOfEwoks;

    //methods
    public void init(int num) {
        Ewok[] ewoksArr1 = new Ewok[num + 1];
        this.numOfEwoks = num;
        for (int i = 1; i < ewoksArr1.length; i++) {
            ewoksArr1[i] = new Ewok(i);
        }
        ewoksArr = ewoksArr1;
    }


    public boolean acquireEwoks(List<Integer> serials) {
        serials.sort(Integer::compareTo);  //avoid from dead-lock
        for (Integer numberOfTheEwok : serials) {
            if (numberOfTheEwok > ewoksArr.length) {
                return false;
            }
            Ewok e = ewoksArr[numberOfTheEwok];
            synchronized (e) {
                while (!e.getAvailable()) {
                    try {
                        e.wait();
                    } catch (InterruptedException ex) {
                    }
                }
                e.acquire();
            }
        }

        return true;
    }


    public boolean toReleaseEwoks(List<Integer> serials) {
        serials.sort(Integer::compareTo);  //avoid from dead-lock
        for (Integer numberOfTheEwok : serials) {
            if (numberOfTheEwok > ewoksArr.length) {
                return false;
            }
            Ewok e = ewoksArr[numberOfTheEwok];
            synchronized (e) {
                e.release();
                e.notify();
            }
        }

        return true;
    }
}