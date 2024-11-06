package edu.mondragon.os.memory.simulator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for Memory.
 */
public class MemoryTest {

    @Test
    public void checkMemory()
            throws InterruptedException, MemoryException {

        Memory mainMemory = new Memory(4000, 100, "Main");
        Memory secondaryMemory = new Memory(10000, 1000, "Secondary");

        OperatingSystem os = new OperatingSystem(mainMemory, secondaryMemory);

        Program programA = new Program('A', os);
        Program programB = new Program('B', os);
        Block block1 = new Block(0, 999);
        mainMemory.allocate(programA, block1);
        Assert.assertThrows(MemoryException.class, () -> {
            Block block2 = new Block(999, 1999);
            mainMemory.allocate(programB, block2);
        });
        Block block3 = new Block(3999, 3999);
        mainMemory.allocate(programB, block3);

        Assert.assertThrows(MemoryException.class, () -> {
            mainMemory.write(programB, 500);
        });
    }
}
