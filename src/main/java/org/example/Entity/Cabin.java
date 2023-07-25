package org.example.Entity;

import lombok.Data;
import org.example.Data.States;

import java.util.List;
import java.util.*;

@Data
public class Cabin extends Thread {

    private final List<Floor> floors;

    private int currFloor = 1;

    private States states;

    boolean up = false;
    boolean down = false;

    private Map<Integer, Integer> order = new HashMap<>(20);

    private boolean[] pick = new boolean[20];

    private int pickCapacityUp = 0;
    private int pickCapacityDown = 0;
    private int dropCapacityUp = 0;
    private int dropCapacityDown = 0;
    private int pickCapacityStay = 0;


    private int maxCapacity;
    private int currCapacity = 0;

    public Cabin(List<Floor> floors, String name, int maxCapacity) {
        super(name);
        this.floors = floors;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (up || down) {
                    System.out.println(
                            String.format(
                                    "%s on %s : pcu-%s, pcd-%s, pcs-%s, dcu-%s, dcd-%s",
                                    currentThread().getName(),
                                    currFloor,
                                    pickCapacityUp,
                                    pickCapacityDown,
                                    pickCapacityStay,
                                    dropCapacityUp,
                                    dropCapacityDown));
                }
                if (pickCapacityDown + pickCapacityUp != 0 || dropCapacityDown + dropCapacityUp != 0 || pickCapacityStay != 0) {
                    if (order.containsKey(currFloor)) {
                        dropPassengers(currFloor);
                    }
                    /*if (up && pickCapacityUp > 1 && pick[currFloor - 1]) {
                        pickCapacityUp--;
                        pickCapacityDown++;
                    }*/
                    if (pick[currFloor - 1]) {

                        pickPassengers(currFloor);
                    }
                    definePath();
                } else {
                    if (currFloor > 1) {
                        goDown();
                    } else {
                        stay();
                    }
                }
                Thread.sleep(500);
                if (up) currFloor++;
                else if (down) currFloor--;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    void definePath() {
        /*if (up) {
            if (pickCapacityUp != 0 || dropCapacityUp != 0) goUp();
            else if (pickCapacityDown != 0 || dropCapacityDown != 0) goDown();
            else stay();
        } else if (down) {
            if (pickCapacityDown != 0 || dropCapacityDown != 0) goDown();
            else if (pickCapacityUp != 0 || dropCapacityUp != 0) goUp();
            else stay();

        } else {
            if (pickCapacityDown != 0) goDown();
            else if (pickCapacityUp != 0) goUp();
            else stay();
        }*/
        if (pickCapacityStay != 0) stay();
        else if (pickCapacityUp != 0 || dropCapacityUp != 0) goUp();
        else if (pickCapacityDown != 0 || dropCapacityDown != 0) goDown();
        else stay();
    }

    void pickPassengers(int currentFloor) {
        Floor fl = floors.get(currentFloor - 1);
        if (fl.getIsCalled().get()) {

            while (fl.getIsCalled().get()) {
                if (currCapacity + 1 <= maxCapacity) {
                    int toFloor = fl.getIn();
                    if (toFloor != currentFloor) {

                        if (toFloor > currFloor) dropCapacityUp++;
                        else if (toFloor < currFloor) dropCapacityDown++;
                        order.put(toFloor, order.getOrDefault(toFloor, 0) + 1);
                        currCapacity++;
                        System.out.println(currentThread().getName() + "\t \t peek pass on " + currentFloor + " floor (" + currCapacity + ")");
                    }
                    if (!fl.getIsCalled().get()) pick[currentFloor - 1] = false;
                    if (up) pickCapacityUp--;
                    else if (down) pickCapacityDown--;
                    else pickCapacityStay--;
                } else{
                    System.out.println(currentThread().getName() + "\u001B[31m" + "can't peek" + "\u001B[0m");
                    return;
                }
            }
        }
    }

    private void dropPassengers(int currentFloor) {
        int countOfDroppedPass = order.remove(currentFloor);
        currCapacity -= countOfDroppedPass;
        if (up) dropCapacityUp -= countOfDroppedPass;
        if (down) dropCapacityDown -= countOfDroppedPass;
        if(dropCapacityUp < 0){
            System.out.println("break");
        }
        System.out.println(currentThread().getName() + "\t \t drop " + countOfDroppedPass + " pass on " + currentFloor + " floor");
    }

    public void addToOrder(Integer toFloor) {
        if (toFloor > currFloor) pickCapacityUp++;
        else if (toFloor < currFloor) pickCapacityDown++;
        else pickCapacityStay++;
        definePath();
        pick[toFloor - 1] = true;
    }

    void goUp() {
        up = true;
        down = false;
        states = States.driving_up;
    }

    void goDown() {
        up = false;
        down = true;
        states = States.riding_down;
    }

    void stay() {
        up = down = false;
    }

    boolean isStay() {
        return (up && down);
    }
}
