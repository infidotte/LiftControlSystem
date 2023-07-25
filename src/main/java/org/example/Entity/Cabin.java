package org.example.Entity;

import org.example.Data.States;

import java.util.List;
import java.util.*;

public class Cabin extends Thread {

    //region fields of TZ
    private int currFloor = 1;
    private States states;
    private int currCapacity = 0;
    private final int maxCapacity;
    private boolean sensor;
    //endregion

    //region others fields
    private final List<Floor> floors;
    private Map<Integer, Integer> order = new HashMap<>(20);
    Queue<Integer> mainOrder;
    private boolean up = false;
    private boolean down = false;
    //endregion

    //region constructor
    public Cabin(List<Floor> floors, String name, int maxCapacity, Queue<Integer> mainOrder) {
        super(name);
        this.mainOrder = mainOrder;
        this.floors = floors;
        this.maxCapacity = maxCapacity;
    }
    //endregion

    //region main methods
    @Override
    public void run() {
        while (true) {
            //if(isStay()) setStates(States.standing_with_doors_open); is optional bcz make a lot of trash in console

            //print state, handles passengers, move
            if (order.containsKey(currFloor)) {
                dropPassengers(currFloor);
            }
            if (floors.get(currFloor - 1).getIsCalled().get()) {
                pickPassengers(currFloor);
            }
            chooseDirection();
            catchSleep(500);
            if (up) currFloor++;
            else if (down) currFloor--;
            updateFloors();
        }
    }

    void chooseDirection() {
        //has a passengers who must be picked on upper floors
        boolean anyPickOnUpperFloors = floors.stream().anyMatch(f -> f.getNumber() > currFloor
                && f.getIsCalled().get() && f.isSubscriber(this));
        //has a passengers who must be picked on down floors
        boolean anyPickOnDownFloors = floors.stream().anyMatch(f -> f.getNumber() < currFloor
                && f.getIsCalled().get() && f.isSubscriber(this));
        //has a passengers who must be dropped on upper floors
        boolean anyDropOnUpFloors = order.keySet().stream().anyMatch(k -> k > currFloor);
        //has a passengers who must be dropped on down floors
        boolean anyDropOnDownFloors = order.keySet().stream().anyMatch(k -> k < currFloor);

        if (isStay()) {
            if (anyDropOnUpFloors || anyPickOnUpperFloors) goUp();
            if (anyDropOnDownFloors || anyPickOnDownFloors) goDown();
        } else {
            if (!(anyPickOnUpperFloors || anyPickOnDownFloors || anyDropOnDownFloors || anyDropOnUpFloors)) stay();
            if (up) {
                if (anyPickOnUpperFloors || anyDropOnUpFloors) goUp();
                else goDown();
            } else if (down) {
                if (anyDropOnDownFloors || anyPickOnDownFloors) goDown();
                else goUp();
            }
        }

    }

    void pickPassengers(int currentFloor) {
        sensor = true;
        Floor floor = floors.get(currentFloor - 1);
        if (floor.getIsCalled().get() && floor.isSubscriber(this)) {

            while (floor.getIsCalled().get()) {
                if (currCapacity < maxCapacity) {
                    int toFloor = floor.getIn();
                    pressFloorButton(toFloor);
                } else {
                    System.out.println(currentThread().getName() + "\u001B[31m" + "can't peek" + "\u001B[0m");
                    mainOrder.add(currentFloor);
                    floor.unSubscribe();
                    return;
                }
            }
            floor.unSubscribe();
        }
        sensor = false;
    }

    private void dropPassengers(int currentFloor) {
        sensor = true;
        int countOfDroppedPassengers = order.remove(currentFloor);
        currCapacity -= countOfDroppedPassengers;
        System.out.println(currentThread().getName() + "\t \t drop " + countOfDroppedPassengers + " pass on " + currentFloor + " floor");
        sensor = false;
    }

    private void updateFloors() {
        floors.forEach(floor -> {
            if (Objects.equals(getName(), "firstLift")) {
                floor.setCabOneCurrentFloor(currFloor);
                floor.setCabOneCurrentState(states);
            }
            if (Objects.equals(getName(), "secondLift")) {
                floor.setCabTwoCurrentFloor(currFloor);
                floor.setCabTwoCurrentState(states);
            }
        });
    }
    //endregion

    //region methods of movement
    private void goUp() {
        up = true;
        down = false;
        setStates(States.driving_up);
    }

    private void goDown() {
        up = false;
        down = true;
        setStates(States.riding_down);
    }

    private void stay() {
        up = down = false;
        setStates(States.standing_with_doors_open);
    }
    //endregion

    //region methods of TZ
    private void pressFloorButton(int toFloor) {
        if (toFloor != currFloor) {
            order.put(toFloor, order.getOrDefault(toFloor, 0) + 1);
            currCapacity++;
            System.out.println(currentThread().getName() + "\t \t peek pass  (" + currCapacity + ")");
        }
    }

    private void callDispatcher() {
        System.out.println("Call a dispatcher");
    }

    private void openDoors() {
        setStates(States.closing_doors);
        catchSleep(100);
    }

    private void closeDoors() {
        //check if sensor doesn't catch any people
        //else retry
        if (!sensor) {
            setStates(States.closing_doors);
            catchSleep(100);
        } else {
            catchSleep(100);
            closeDoors();
        }

    }
    //endregion

    //region functional
    private void catchSleep(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region getters and setters
    public boolean isStay() {
        return !(up || down);
    }

    public int getCurrFloor() {
        return currFloor;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    private void setStates(States state) {
        this.states = state;
        System.out.println(String.format("%s has %s state on %s", currentThread().getName(), states, currFloor));
    }
    //endregion
}
