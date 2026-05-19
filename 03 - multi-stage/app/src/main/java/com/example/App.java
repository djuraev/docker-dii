package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello from Docker multi-stage demo!");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Args: " + String.join(", ", args));
    }
}
