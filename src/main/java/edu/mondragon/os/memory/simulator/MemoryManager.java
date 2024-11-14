package edu.mondragon.os.memory.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private Memory mainMemory;
    private Memory secondaryMemory;
    // TODO your code goes here
    private Map<Program, List<ProgramSection>> memoryTable;

    public MemoryManager(
            Memory mainMemory, Memory secondaryMemory) {

        this.mainMemory = mainMemory;
        // this.secondaryMemory = secondaryMemory;

        // TODO your code goes here
        memoryTable = new HashMap<>();
    }

    public synchronized void start(Program program)
        throws MemoryException, InterruptedException {

    int[] sections = program.getSectionSizes();
    List<ProgramSection> programSections = new ArrayList<>();

    // Verificar si hay suficiente memoria total disponible
    int totalProgramSize = Arrays.stream(sections).sum(); // Suma las secciones
    int totalAvailableMemory = mainMemory.getGaps().stream().mapToInt(Block::getSize).sum(); // Suma los gaps disponibles

    if (totalProgramSize > totalAvailableMemory) {
        throw new MemoryException("Not enough memory for Program " + program.getPid() + 
                                   ". Needed: " + totalProgramSize + ", Available: " + totalAvailableMemory);
    }

    // Verificar si cada sección puede caber en algún bloque libre
    for (int sectionSize : sections) {
        boolean canFit = mainMemory.getGaps().stream().anyMatch(gap -> gap.getSize() >= sectionSize);
        if (!canFit) {
            throw new MemoryException("No suitable memory block for section of size " + sectionSize + 
                                       " in Program " + program.getPid());
        }
    }

    // Proceder a intentar asignar las secciones del programa
    for (int i = 0; i < sections.length; i++) {
        List<Block> memoryGaps = mainMemory.getGaps();
        Iterator<Block> it = memoryGaps.iterator();
        boolean foundGap = false;
        while (!foundGap && it.hasNext()) {
            Block gapBlock = it.next();
            if (gapBlock.getSize() >= sections[i]) {
                Block newBlock = new Block(gapBlock.start(), (gapBlock.start() + sections[i]));
                mainMemory.allocate(program, newBlock);
                programSections.add(new ProgramSection(i, newBlock, sections[i]));
                foundGap = true;
            }
        }

        if (!foundGap) {
            throw new MemoryException("There is no space for Program " + program.getPid());
        }
    }

    memoryTable.put(program, programSections);
}

    }

    public synchronized void write(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running in memory");
        }

        if(section > programSections.size()){
            throw new MemoryException("Program " + program.getPid() + " has no section " + section);
        }

        ProgramSection ps = getProgramSection(programSections, section);
        mainMemory.write(program, ps.getPhysicalAddress(logical_address));
    }

    public synchronized void read(
            Program program, int section, int logical_address)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running in memory");
        }

        if(section > programSections.size()){
            throw new MemoryException("Program " + program.getPid() + " has no section " + section);
        }

        ProgramSection ps = getProgramSection(programSections, section);
        mainMemory.read(program, ps.getPhysicalAddress(logical_address));
    }
    public synchronized void sleep(Program program) throws MemoryException, InterruptedException {
        List<ProgramSection> programSections = memoryTable.get(program);
        if (programSections == null) {
            throw new MemoryException("Program " + program.getPid() + " is not running in memory");
        }

        for (ProgramSection ps : programSections) {
            secondaryMemory.allocate(program, ps.getBlock());
            // Assuming secondaryMemory has similar methods to mainMemory
            mainMemory.free(program, ps.getBlock());
        }
    }

    public synchronized void awake(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        
    }

    public synchronized void end(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> programSections = memoryTable.get(program);
        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running");
        }

        for(ProgramSection ps : programSections){
            mainMemory.free(program, ps.getBlock());
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
