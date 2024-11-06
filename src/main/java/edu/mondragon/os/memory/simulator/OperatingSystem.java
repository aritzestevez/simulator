package edu.mondragon.os.memory.simulator;

import java.util.ArrayList;
import java.util.List;

public class OperatingSystem implements API {

    private MemoryManager memoryManager;
    private List<Program> programs;

    public OperatingSystem(
            Memory mainMemory, Memory secondaryMemory) {

        programs = new ArrayList<Program>();
        memoryManager = new MemoryManager(mainMemory, secondaryMemory);
    }

    public void startProgram(Program program)
            throws InterruptedException {

        System.out.println("Starting " + program.getName());
        try {
            memoryManager.start(program);
            programs.add(program);
            program.start();
        } catch (MemoryException e) {
            printException(e);
        }
    }

    public void halt()
            throws InterruptedException {

        System.out.println("OS halting");

        for (Program program : programs) {
            program.interrupt();
        }

        for (Program program : programs) {
            program.join();
        }
    }

    @Override
    public void write(Program program, int section, int logical_address)
            throws InterruptedException {

        try {
            memoryManager.write(program, section, logical_address);
        } catch (MemoryException e) {
            printException(e);
            program.interrupt();
        }
    }

    @Override
    public void read(Program program, int section, int logical_address)
            throws InterruptedException {

        try {
            memoryManager.read(program, section, logical_address);
        } catch (MemoryException e) {
            printException(e);
            program.interrupt();
        }
    }

    @Override
    public void sleep(Program program)
            throws InterruptedException {

        System.out.println(program.getName() + " sleeping");
        try {
            memoryManager.sleep(program);
        } catch (MemoryException e) {
            printException(e);
            program.interrupt();
        }
    }

    @Override
    public void awake(Program program)
            throws InterruptedException {

        System.out.println(program.getName() + " awaken");
        try {
            memoryManager.awake(program);
        } catch (MemoryException e) {
            printException(e);
            program.interrupt();
        }
    }

    @Override
    public void end(Program program)
            throws InterruptedException {

        System.out.println(program.getName() + " ending");
        try {
            memoryManager.end(program);
        } catch (MemoryException e) {
            printException(e);
            program.interrupt();
        }
    }

    private void printException(Exception e) {
        System.out.println("\t❗ error: " + e.getMessage());
    }
}
