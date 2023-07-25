package org.example;

import org.example.Entity.Cabin;
import org.example.Entity.Floor;
import org.example.Interface.Subscriber;
import org.example.Utility.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread manager = new Thread(new Manager());
        manager.start();
    }

}