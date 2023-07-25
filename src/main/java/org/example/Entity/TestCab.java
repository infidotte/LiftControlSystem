package org.example.Entity;

import com.sun.source.tree.Tree;
import lombok.Data;
import org.example.Data.States;

import java.util.List;
import java.util.*;

@Data
public class TestCab extends Thread {
    private final List<Floor> floors;
    private int floor = 1;
    //private States state = States.wait_with_opened_doors;
    private int toFloor;
    boolean up = false;
    boolean down = false;
    private Map<Integer, Integer> order = new HashMap<>(20);
    private Set<Integer> orderToPeekUp = new TreeSet<>();
    private boolean[] peek = new boolean[20];
    private int peekCapacityU = 0;
    private int peekCapacityD = 0;
    private int dropCapacityU = 0;
    private int dropCapacityD = 0;
    private int maxCapacity = 5;
    private int currCapacity = 0;

    public TestCab(List<Floor> floors) {
        super();
        this.floors = floors;
    }

    @Override
    public void run() {
        while (true) {
            try {

                definePath();
                if(peekCapacityD+peekCapacityU!=0 || dropCapacityD+dropCapacityU!=0) {
                    if(up && floor<20) floor++;
                    else if(down) floor--;
                    System.out.println("lift 1 on " + floor + " floor");
                    if (order.containsKey(floor)) {
                        int dropedPass = order.remove(floor);
                        if(up) dropCapacityU--;
                        if(down) dropCapacityD--;
                        System.out.println("lift 1 drop pass on " + floor + " floor");
                    }
                    if(up && peekCapacityU > 1 && peek[floor-1]) {
                        peekCapacityU--;
                        peekCapacityD++;
                    }else if (peek[floor-1]) {
                        System.out.println("lift 1 peek pass on " + floor + " floor");
                        peekPassengers(floor);
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void definePath() {
        if(up) {
            if(peekCapacityU!=0 || dropCapacityU != 0) goUp();
            else if (peekCapacityD != 0 || dropCapacityD != 0) goDown();
            else stay();
        } else if (down) {
            if (peekCapacityD != 0 || dropCapacityD != 0) goDown();
            else if(peekCapacityU!=0 || dropCapacityU != 0) goUp();
            else stay();

        } else {
            if(peekCapacityD!=0) goDown();
            else if (peekCapacityU!=0) goUp();
        }
    }
    void peekPassengers(int lvl) {
        int tf = -1;
        List<Integer> targetFloors = new ArrayList<>();
        synchronized (floors) {
            if(floors.get(lvl).getIsCalled().get()) {
                Floor fl = floors.get(lvl);
                while (fl.getIsCalled().get() && maxCapacity > currCapacity) {
                    targetFloors.add(fl.getIn());
                    currCapacity++;
                }
                if(fl.getIsCalled().get() && currCapacity >= maxCapacity) System.out.println("\u001B[31m" + "can't peek" + "\u001B[0m");
            }
        }
        if(!targetFloors.isEmpty()) {
            for(int i : targetFloors) {
                order.put(i, order.getOrDefault(tf,0) + 1);
                if(i>floor) dropCapacityU++;
                else if(i<floor) dropCapacityD++;
            }
        }
        peek[lvl-1] = false;
        if(up) peekCapacityU--;
        if(down) peekCapacityD--;

    }

    public void addToOrder(Integer toFloor) {
        if(toFloor > floor && !peek[toFloor-1]) peekCapacityU++;
        else if (!peek[toFloor-1]) peekCapacityD++;
        peek[toFloor-1] = true;
    }

    void goUp() {up=true;down=false;}
    void goDown() {up=false;down=true;}

    boolean isStay() {return !(down||up);}
    void stay() {up=down=false;}
}