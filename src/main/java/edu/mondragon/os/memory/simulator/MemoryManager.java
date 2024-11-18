package edu.mondragon.os.memory.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private Memory mainMemory;
    private Memory secondaryMemory;
    // TODO your code goes here
    private Map<Program, List<ProgramSection>> memoryTable;
    private Map<Program, List<ProgramSection>> secondMemoryTable;

    public MemoryManager(
            Memory mainMemory, Memory secondaryMemory) {

        this.mainMemory = mainMemory;
        this.secondaryMemory = secondaryMemory;

        // TODO your code goes here
        memoryTable = new HashMap<>();
        secondMemoryTable = new HashMap<>();
    }

    public synchronized void start(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        int[] sections = program.getSectionSizes();
        List<ProgramSection> programSections = new ArrayList<>();
        
        for(int i = 0; i < sections.length; i++){
            List<Block> memoryGaps = mainMemory.getGaps();
            Iterator<Block> it = memoryGaps.iterator();
            boolean foundGap = false;
            while(!foundGap && it.hasNext()){
                Block gapBlock = it.next();
                if(gapBlock.getSize() > sections[i]){
                    Block newBlock = new Block(gapBlock.start(), (gapBlock.start() + sections[i]));
                    mainMemory.allocate(program, newBlock);
                    programSections.add(new ProgramSection(i, newBlock, sections[i]));
                    foundGap = true;
                }
            }

            if(!foundGap){
                throw new MemoryException("There is no space for Program " + program.getPid());
            }
        }
        memoryTable.put(program, programSections);
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

    public synchronized void sleep(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> programSections = memoryTable.get(program);
        List<ProgramSection> secondaryProgramSections = new ArrayList<>();

        if(programSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running");
        }
        // Verificar si cada sección puede caber en algún bloque libre
        for (ProgramSection section : programSections) {
            boolean canFit = secondaryMemory.getGaps().stream().anyMatch(gap -> gap.getSize() >= section.getSize());
            if (!canFit) {
                throw new MemoryException("No suitable memory block for section of size " + section.getSize() + 
                                        " in Program " + program.getPid() + " in secondary memory.");
            }
        }

        Iterator<ProgramSection> itPS = programSections.iterator();

        while (itPS.hasNext()) {

            // boolean foundSpace = false;
            boolean foundGap = false;

            ProgramSection section = itPS.next();
            
            List<Block> memoryGaps = secondaryMemory.getGaps();
            Iterator<Block> it = memoryGaps.iterator();

            while(!foundGap && it.hasNext() ){
                Block gapBlock = it.next();
                if(gapBlock.getSize() > section.getSize()){
                    Block newBlock = new Block(gapBlock.start(), (gapBlock.start() + section.getSize()));
                    secondaryMemory.allocate(program, newBlock);
                    secondaryProgramSections.add(new ProgramSection(section.getNumber(), newBlock, section.getSize()));
                    foundGap = true;
                }
            }
            if(!foundGap){
                secondMemoryTable.put(program, secondaryProgramSections);
                throw new MemoryException("Program Section " + section.getNumber() + " from Program " + program.getPid() +
                                        " couldn't fit in secondary memory.");
            }

        }
        secondMemoryTable.put(program, secondaryProgramSections);
        memoryTable.remove(program);

        for(ProgramSection ps : programSections){
            mainMemory.free(program, ps.getBlock());
        }
    }

    public synchronized void awake(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> secondaryProgramSections = secondMemoryTable.get(program);
        List<ProgramSection> programSections = new ArrayList<>();

        if(secondaryProgramSections == null){
            throw new MemoryException("Program " + program.getPid() + " is not running");
        }
        // Verificar si cada sección puede caber en algún bloque libre
        for (ProgramSection section : secondaryProgramSections) {
            boolean canFit = mainMemory.getGaps().stream().anyMatch(gap -> gap.getSize() >= section.getSize());
            if (!canFit) {
                throw new MemoryException("No suitable memory block for section of size " + section.getSize() + 
                                        " in Program " + program.getPid() + " in main memory.");
            }
        }

        Iterator<ProgramSection> itPS = secondaryProgramSections.iterator();

        while (itPS.hasNext()) {
            boolean foundGap = false;

            ProgramSection section = itPS.next();
            
            List<Block> memoryGaps = mainMemory.getGaps();
            Iterator<Block> it = memoryGaps.iterator();

            while(!foundGap && it.hasNext() ){
                Block gapBlock = it.next();
                if(gapBlock.getSize() > section.getSize()){
                    Block newBlock = new Block(gapBlock.start(), (gapBlock.start() + section.getSize()));
                    mainMemory.allocate(program, newBlock);
                    programSections.add(new ProgramSection(section.getNumber(), newBlock, section.getSize()));
                    foundGap = true;
                }
            }
            if(!foundGap){
                memoryTable.put(program, programSections);
                throw new MemoryException("Program Section " + section.getNumber() + " from Program " + program.getPid() +
                                        " couldn't fit in main memory.");
            }

        }
        memoryTable.put(program, programSections);
        secondMemoryTable.remove(program);

        for(ProgramSection ps : secondaryProgramSections){
            secondaryMemory.free(program, ps.getBlock());
        }

    }

    public synchronized void end(Program program)
            throws MemoryException, InterruptedException {

        // TODO your code goes here
        List<ProgramSection> programSections = memoryTable.get(program);
        List<ProgramSection> secondProgramSections = secondMemoryTable.get(program);
        if(programSections != null){
            for(ProgramSection ps : programSections){
                mainMemory.free(program, ps.getBlock());
            }
            memoryTable.remove(program);
        }
        if(secondProgramSections != null){
            for(ProgramSection ps : secondProgramSections){
                secondaryMemory.free(program, ps.getBlock());
            }
            secondMemoryTable.remove(program);
        }
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
