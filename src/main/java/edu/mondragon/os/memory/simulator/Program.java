package edu.mondragon.os.memory.simulator;

import java.util.Random;

public class Program extends Thread {

    private Random rand;
    private int section_sizes[];
    private char pid;
    private API api;

    public Program(char pid, API api) {

        super("Program " + pid);
        this.pid = pid;
        this.api = api;
        rand = new Random();
        section_sizes = new int[1];
        section_sizes[0] = rand.nextInt(400, 1000);
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            try {
                Thread.sleep(rand.nextInt(1000));
                int action = rand.nextInt(100);
                if (action < 80) {
                    int section = rand.nextInt(section_sizes.length);
                    int logical_address = rand.nextInt(section_sizes[section]);
                    if (action < 40)
                        api.read(this, section, logical_address);
                    else
                        api.write(this, section, logical_address);
                } else if (action < 90) {
                    api.sleep(this);
                    Thread.sleep(rand.nextInt(1000));
                    api.awake(this);
                } else {
                    interrupt();
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }
        Thread.interrupted();
        try {
            api.end(this);
        } catch (InterruptedException e) {
        }
    }

    public char getPid() {
        return pid;
    }

    public int[] getSectionSizes() {
        return section_sizes;
    }
}
