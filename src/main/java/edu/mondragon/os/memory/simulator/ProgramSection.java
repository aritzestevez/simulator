package edu.mondragon.os.memory.simulator;

import java.util.Map;

public class ProgramSection {
    private int number;
    private Map<Integer, Block> blockList;
    private int size;
    
    public ProgramSection(int number, Map<Integer, Block> blockList, int size) {
        this.number = number;
        this.blockList = blockList;
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getPhysicalAddress(int logical_address, int page_size) throws MemoryException{
        if(logical_address >= size){
            throw new MemoryException("Logical address out of bounds");            
        }
        
        int pageNum = logical_address / page_size;
        int offset = logical_address % page_size;
        return blockList.get(pageNum).start() + offset;
    }

    public Map<Integer, Block> getBlockList() {
        return blockList;
    }
}
