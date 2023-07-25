import org.example.Entity.Cabin;
import org.example.Entity.Floor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CabinTest {
    List<Floor> floors = manager();
    Cabin fl = new Cabin(floors, "firstLift", 5);
    private List<Floor> manager() {
        List<Floor> floors = new ArrayList<>(20);
        for (int i = 1; i <= 20; i++) {
            floors.add(new Floor(i));
        }
        return floors;
    }

    @Test
    public void customFloors() throws InterruptedException {
        fl.start();
        fl.join();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("Write fromFloor: ");
            int fromFloor = scanner.nextInt();
            System.out.println("Write toFloor: ");
            int toFloor = scanner.nextInt();
            floors.get(fromFloor).addToQueue(toFloor);
            fl.addToOrder(fromFloor);
            System.out.println(String.format(" \t a man from %s floor want to %s floor", fromFloor, toFloor));
        }
    }

    @Test
    public void pickAndDropTest() throws InterruptedException {

        floors.get(8).addToQueue(4);
        fl.addToOrder(8);

        floors.get(4).addToQueue(9);
        fl.addToOrder(4);
        fl.start();
        fl.join();
    }
    @Test
    public void sameFloorToDropTest() throws InterruptedException {
        List<Floor> floors = manager();
        floors.get(4).addToQueue(4);
        fl.addToOrder(4);
        fl.start();
        fl.join();
    }
    @Test
    public void onCurrFloorPickTest() throws InterruptedException {
        List<Floor> floors = manager();
        floors.get(1).addToQueue(5);
        fl.addToOrder(1);
        floors.get(5).addToQueue(2);
        fl.addToOrder(5);

        fl.start();

        fl.join();
    }
}
