package org.example;

import org.example.Utility.Manager;

public class Main {
    public static void main(String[] args){
        Thread manager = new Thread(new Manager());
        manager.start();
    }

}