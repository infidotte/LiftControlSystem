package org.example.Utility;

import org.example.Entity.Cabin;
import org.example.Entity.Floor;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager implements Runnable {
    List<Floor> floors = new ArrayList<>(20);
    Queue<Integer> mainOrder = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        Cabin firstLift = new Cabin(floors, "firstLift", 5, mainOrder);
        Cabin secondLift = new Cabin(floors, "secondLift", 10, mainOrder);
        for (int i = 1; i <= 20; i++) {
            floors.add(new Floor(i));
        }

        firstLift.start();
        secondLift.start();

        //hardCode(firstLift, secondLift);
        random(firstLift, secondLift);

    }

    //situation from TZ
    private void hardCode(Cabin firstLift, Cabin secondLift) {
        Floor floor = floors.get(0);
        floor.addToQueue(14);
        if (!floor.haveSubscriber()) floor.subscribe(chooseLift(firstLift, secondLift, 0));
        floor = floors.get(14);
        floor.addToQueue(1);
        if (!floor.haveSubscriber()) floor.subscribe(chooseLift(firstLift, secondLift, 14));
    }

    //random generator
    private void random(Cabin firstLift, Cabin secondLift) {
        Random random = new Random();
        while (true) {

            //add floor to queue of cabin again
            while (mainOrder.size() != 0) {
                Integer fromFloor = mainOrder.poll();
                Floor floor = floors.get(fromFloor - 1);
                if (!floor.haveSubscriber()) floor.subscribe(chooseLift(firstLift, secondLift, fromFloor));
            }

            //generate passenger activity

            int fromFloor = random.nextInt(1, 21);
            int toFloor = random.nextInt(1, 21);
            while (fromFloor == toFloor) {
                toFloor = random.nextInt(1, 21);
            }

            Floor floor = floors.get(fromFloor - 1);
            floor.addToQueue(toFloor);
            if (!floor.haveSubscriber()) floor.subscribe(chooseLift(firstLift, secondLift, fromFloor));

            System.out.println(String.format(" \t a man from %s floor want to %s floor", fromFloor, toFloor));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    //which lift to send a passenger
    private Cabin chooseLift(Cabin firstLift, Cabin secondLift, int fromFloor) {
        int difference = firstLift.getCurrFloor() - fromFloor;
        if ((difference > 0 && firstLift.isDown()) || (difference < 0 && firstLift.isUp()) || firstLift.isStay())
            return firstLift;
        else return secondLift;
    }

}
