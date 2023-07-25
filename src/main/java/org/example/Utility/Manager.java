package org.example.Utility;

import org.example.Data.States;
import org.example.Entity.Cabin;
import org.example.Entity.Floor;
import org.example.Entity.MyCab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Manager implements Runnable {
    List<Floor> floors = new ArrayList<>(20);
    Cabin fl = new Cabin(floors, "firstLift", 5);
    Cabin sl = new Cabin(floors, "secondLift", 10);
    //MyCab fl = new MyCab(floors, "firstLift", 5);
    @Override
    public void run() {
        for (int i = 1; i <= 20; i++) {
            floors.add(new Floor(i));
        }
        fl.start();
        random();

    }
    private Cabin chooseLift(Cabin firstLift, Cabin secondLift, int fromFloor){
        int flFloor = firstLift.getCurrFloor();
        int slFloor = secondLift.getCurrFloor();

        if (flFloor < fromFloor && fromFloor < slFloor) return firstLift;
        else if (slFloor < fromFloor && fromFloor < flFloor) return secondLift;

        if (firstLift.getStates() == null) return firstLift;
        else if (secondLift.getStates() == null) return secondLift;

        return firstLift.getCurrCapacity() < secondLift.getCurrCapacity() ? firstLift : secondLift;
    }

    private void random(){
        /*floors.get(1).addToQueue(14);
        chooseLift(fl,sl,1).addToOrder(1);

        floors.get(15).addToQueue(1);
        chooseLift(fl,sl,15).addToOrder(15);*/

        boolean generator = true;
        while (generator) {
            Random random = new Random();

            int sf = random.nextInt(1, 21);
            int tf = random.nextInt(1, 21);
            /*while (sf == tf) {
                tf = random.nextInt(1, 20);
            }*/
            floors.get(sf-1).addToQueue(tf);
            //chooseLift(fl, sl, sf).addToOrder(sf);
            fl.addToOrder(sf);

            System.out.println(String.format(" \t a man from %s floor want to %s floor", sf, tf));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void custom(){
        boolean generator = true;
        Scanner scanner = new Scanner(System.in);
        while (generator) {
            System.out.println("Write: ");
            int fromFloor = scanner.nextInt();
            int toFloor = scanner.nextInt();

            floors.get(fromFloor-1).addToQueue(toFloor);
            //fl.addToOrder(fromFloor);

            System.out.println(String.format(" \t a man from %s floor want to %s floor", fromFloor, toFloor));
        }
    }
}
