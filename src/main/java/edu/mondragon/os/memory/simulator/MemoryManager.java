package edu.mondragon.os.memory.simulator;

public class MemoryManager {

    private Memory mainMemory;
    // private Memory secondaryMemory;
    // TODO your code goes here

    public MemoryManager(
            Memory mainMemory, Memory secondaryMemory) {

        this.mainMemory = mainMemory;
        // this.secondaryMemory = secondaryMemory;

        // TODO your code goes here
    }

    public synchronized void start(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }

    public synchronized void write(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }

    public synchronized void read(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }

    public synchronized void sleep(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }

    public synchronized void awake(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }

    public synchronized void end(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
    }
}
