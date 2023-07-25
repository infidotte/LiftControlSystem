package org.example.Entity;

import lombok.Data;
import org.example.Data.States;

import java.util.List;
import java.util.*;

@Data
public class MyCab extends Thread {

    private final List<Floor> floors;

    private int currFloor = 1;

    private States states;

    boolean up = false;
    boolean down = false;

    private Map<Integer, Integer> order = new HashMap<>(20);

    private boolean[] pick = new boolean[20];

    private int pickCapacityUp = 0;
    private int pickCapacityDown = 0;
    private boolean pickCapacityStay;

    private int dropCapacityUp = 0;
    private int dropCapacityDown = 0;
    private boolean dropCapacityStay;



    private int maxCapacity;
    private int currCapacity = 0;
    private boolean overWeight = false;

    public MyCab(List<Floor> floors, String name, int maxCapacity) {
        super(name);
        this.floors = floors;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void run() {
        System.out.println(String.format("lift on %d", currFloor));
        while (true) {
            move();
            catchSleep(500);

            Floor currentFloor = floors.get(currFloor-1);
            if (isStay()) {
                dropPerson(currentFloor);
                pickPerson(currentFloor);
            }else {
                System.out.println(
                        String.format(
                                "floor: %s \t \t \t pu: %s, pd: %s, du: %s, dd: %s",
                                currFloor, pickCapacityUp, pickCapacityDown, dropCapacityUp, dropCapacityDown
                        )
                );
            }
        }
    }

    private void pickPerson(Floor currentFloor){
        while (currentFloor.getIsCalled().get() && currCapacity < maxCapacity){
            int toFloor = currentFloor.getIn();
            order.put(toFloor, order.getOrDefault(toFloor, 0) + 1);
            currCapacity++;
            System.out.println(String.format("pick person on %d (%d)", currFloor, currCapacity));
        }
        if(!currentFloor.getIsCalled().get()){
            if(currCapacity == maxCapacity){
                overWeight = true;
            }else {
                pickCapacityStay = false;
            }
        }
    }
    private void dropPerson(Floor currentFloor){
        int number = currentFloor.getNumber();
        if(order.containsKey(number)){
            int count = order.remove(number);
            currCapacity-=count;
            dropCapacityStay = false;
            System.out.println(String.format("drop %d people on %d", count, currFloor));
        }
    }

    private void move(){
        countPickCapacity();
        countDropCapacity();
        chooseDirection();


    }

    private void chooseDirection(){
        int sumUp = pickCapacityUp+dropCapacityUp;
        int sumDown = pickCapacityDown+dropCapacityDown;
        if(pickCapacityStay || dropCapacityStay) stay();
        else if(sumUp>=sumDown && (sumUp != 0 || sumDown != 0)) goUp();
        else goDown();
    }

    private void countPickCapacity() {
        pickCapacityUp = pickCapacityDown = 0;
        pickCapacityStay = false;
        if(!overWeight){
            for (Floor floor :
                    floors) {
                if (floor.getIsCalled().get()) {
                    int i = floor.getNumber();
                    if (i < currFloor) pickCapacityDown++;
                    else if (i > currFloor) pickCapacityUp++;
                    else pickCapacityStay = true;
                }
            }
        }
    }
    private void countDropCapacity(){
        dropCapacityUp = dropCapacityDown = 0;
        dropCapacityStay = false;
        for(Map.Entry<Integer, Integer> entry : order.entrySet()) {
            Integer floor = entry.getKey();
            if(floor < currFloor) dropCapacityDown++;
            else if(floor > currFloor) dropCapacityUp++;
            else dropCapacityStay = true;
        }
    }
    void goUp() {
        up = true;
        down = false;
        states = States.driving_up;
        currFloor++;
    }

    void goDown() {
        up = false;
        down = true;
        states = States.riding_down;
        if(currFloor == 1) stay();
        else currFloor--;
    }

    void stay() {
        up = down = false;
        states = States.standing_with_doors_open;
    }
    private boolean isStay(){
        return !(up || down);
    }
    private void catchSleep(int time) {
        try {
            sleep(time);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
