package edu.mondragon.os.memory.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private Memory mainMemory;
    // private Memory secondaryMemory;
    private Map<Program, List<ProgramSection>> memoryTable;
    private final int PAGE_SIZE = 40;

    public MemoryManager(
            Memory mainMemory, Memory secondaryMemory) {

        this.mainMemory = mainMemory;
        // this.secondaryMemory = secondaryMemory;

        memoryTable = new HashMap<>();
    }

    public synchronized void start(Program program) throws MemoryException, InterruptedException {
        int[] sections = program.getSectionSizes();
        List<ProgramSection> programSections = new ArrayList<>();
        List<Block> freePages = divideInPages(mainMemory.getGaps());

        for(int i = 0; i < sections.length; i++){
            programSections.add(getSection(freePages, i, sections[i]));
        }
    
        // Reservar los bloques de memoria para el programa
        for (ProgramSection ps : programSections) {
            Map<Integer, Block> blocks = ps.getBlockList();
            int groupStart = blocks.get(0).start();
            int groupEnd = blocks.get(0).end();

            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);

                // Si el bloque es consecutivo al anterior, extiende el grupo actual
                if (currentBlock.start() == groupEnd + 1) {
                    groupEnd = currentBlock.end();
                } else {
                    // Si no es consecutivo, reserva el bloque acumulado y empieza un nuevo grupo
                    mainMemory.allocate(program, new Block(groupStart, groupEnd));
                    groupStart = currentBlock.start();
                    groupEnd = currentBlock.end();
                }
            }
            // Reserva el último bloque acumulado
            mainMemory.allocate(program, new Block(groupStart, groupEnd));
        }
    
        // Actualizar la tabla de memoria
        memoryTable.put(program, programSections);
    }

    private List<Block> divideInPages(List<Block> gaps) {
        List<Block> pages = new ArrayList<>();
    
        for (Block gap : gaps) {
            int start = gap.start();
            int remainingSize = gap.getSize();
    
            while (remainingSize >= PAGE_SIZE) {
                Block page = new Block(start, start + PAGE_SIZE - 1);
                pages.add(page);    
                start += PAGE_SIZE;
                remainingSize -= PAGE_SIZE;
            }
        }
    
        return pages;
    }

    private ProgramSection getSection(List<Block> freePages, int sectionNum, int sectionSize) throws MemoryException{
        int remainingSize = sectionSize;
        Iterator<Block> it = freePages.iterator();
        Map<Integer, Block> pageList = new HashMap<>();
        int pageIdx = 0;
        
        while (remainingSize > 0 && it.hasNext()) {
            Block nextPage = it.next();
            pageList.put(pageIdx++, nextPage);
            it.remove();
            remainingSize -= PAGE_SIZE;
        }

        if(remainingSize > 0){
            throw new MemoryException("No space left");
        }

        return new ProgramSection(sectionNum, pageList, sectionSize);
    }
    

    public synchronized void write(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running in memory");
        }

        if(section > programSections.size()){
            throw new MemoryException("Program " + program.getPid() + " has no section " + section);
        }

        ProgramSection ps = getProgramSection(programSections, section);
        mainMemory.write(program, ps.getPhysicalAddress(logical_address, PAGE_SIZE));
    }

    public synchronized void read(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running in memory");
        }

        if(section > programSections.size()){
            throw new MemoryException("Program " + program.getPid() + " has no section " + section);
        }

        ProgramSection ps = getProgramSection(programSections, section);
        mainMemory.read(program, ps.getPhysicalAddress(logical_address, PAGE_SIZE));
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

        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running");
        }

        for (ProgramSection ps : programSections) {
            Map<Integer, Block> blocks = ps.getBlockList();
            int groupStart = blocks.get(0).start();
            int groupEnd = blocks.get(0).end();

            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);

                // Si el bloque es consecutivo al anterior, extiende el grupo actual
                if (currentBlock.start() == groupEnd + 1) {
                    groupEnd = currentBlock.end();
                } else {
                    // Si no es consecutivo, libera el bloque acumulado y empieza un nuevo grupo
                    mainMemory.free(program, new Block(groupStart, groupEnd));
                    groupStart = currentBlock.start();
                    groupEnd = currentBlock.end();
                }
            }
            // Libera el último bloque acumulado
            mainMemory.free(program, new Block(groupStart, groupEnd));
        }

        memoryTable.remove(program);
    }

    private ProgramSection getProgramSection(List<ProgramSection> psList, int sectionNum)
            throws MemoryException{
        for(ProgramSection ps : psList){
            if(ps.getNumber() == sectionNum){
                return ps;
            }
        }

        throw new MemoryException("Section " + sectionNum + " not found");
    }
}
