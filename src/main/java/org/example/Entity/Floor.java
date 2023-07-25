package org.example.Entity;

import lombok.Data;
import org.example.Data.States;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class Floor {

    private int number;

    Queue<Integer> queue = new LinkedList<>();

    private int cabOneCurrentFloor;
    private States cabOneCurrentState;

    private int cabTwoCurrentFloor;
    private States cabTwoCurrentState;

    private AtomicBoolean isCalled;

    public int getIn() {
        synchronized (this){
            if (queue.size() == 1) isCalled.set(false);
            return queue.poll();
        }
    }

    public Floor(int number) {
        this.number = number;
        isCalled = new AtomicBoolean(false);
    }

    public void addToQueue(int tf) {
        synchronized (this){
            queue.add(tf);
            isCalled.set(true);
        }
    }
}
