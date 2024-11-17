package edu.mondragon.os.memory.simulator;

public class ProgramSection {
    private int number;
    private Block block;
    private int size;
    
    public ProgramSection(int number, Block block, int size) {
        this.number = number;
        this.block = block;
        this.size = size;
    }
    
    public int getNumber() {
        return number;
    }

    public Block getBlock() {
        return block;
    }

    public int getSize() {
        return size;
    }

    public int getPhysicalAddress(int logical_address) throws MemoryException{
        if(logical_address > size){
            throw new MemoryException("Logical address out of bounds");
        }
        return block.start() + logical_address;
    }
}
