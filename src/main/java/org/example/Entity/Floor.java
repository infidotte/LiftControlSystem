package org.example.Entity;

import lombok.Data;
import org.example.Data.States;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class Floor {

    //region fields of TZ
    private int number;
    private int cabOneCurrentFloor;
    private States cabOneCurrentState;
    private int cabTwoCurrentFloor;
    private States cabTwoCurrentState;
    private AtomicBoolean isCalled;
    //endregion

    //region other fields
    private Queue<Integer> queue = new LinkedList<>();
    private Cabin subscriber;
    //endregion

    //region constructor
    public Floor(int number) {
        this.number = number;
        isCalled = new AtomicBoolean(false);
    }
    //endregion

    //region methods of TZ
    private void pressLiftButton(){
        isCalled.set(true);
    }
    //endregion

    //region subscribe methods
    public void subscribe(Cabin subscriber) {
        this.subscriber = subscriber;
    }

    public boolean haveSubscriber() {
        return subscriber != null;
    }

    public void unSubscribe() {
        subscriber = null;
    }

    public boolean isSubscriber(Cabin cabin) {
        return cabin.equals(subscriber);
    }
    //endregion

    //region work with order methods
    public int getIn() {

        synchronized (this) {
            if (queue.size() == 1) isCalled.set(false);
            return queue.poll();
        }
    }

    public void addToQueue(int toFloor) {
        synchronized (this) {
            queue.add(toFloor);
            pressLiftButton();
        }
    }
    //endregion
}
