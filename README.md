# Memory management

This is a simulator written in java that mimics the memory management tasks of an operating system.

You will have to get together in teams of 3 or 4 people to program the memory manager of this operating system ( `MemoryManager.java` ). You can add more files and classes. If you need to modify any of the other classes provided, let the teacher know.

## MemoryManager Class

- `void start(Program program)`: Allocates one or more physical blocks for the new program in the main memory.
- `void write(Program program, int section, int logical_address)`: The program intends to write in one of its sections with the indicated logical address.
- `void read(Program program, int section, int logical_address)`: The program intends to read in one of its sections with the indicated logical address.
- `void sleep(Program program)`: The programs indicates that is going to sleep. During this period it can be relocated to the secondary memory.
- `void awake(Program program)`: The program indicates that it is going to wake up. It must be restored to main memory.
- `void end(Program program)`: The program notifies that is going to finish its execution so that the physical memory blocks allocated for it can be freed.

## Memory Class

- `void allocate(Program owner, Block newBlock)`: Allocate a Block of memory for this program.
- `void free(Program owner, Block block2remove)`: Free a memory block.
- `void read(Program program, int physical_address)`: Read from a physical memory address. The program must be the owner of a memory block that contains this address.
- `void write(Program program, int physical_address)`: Write in a physical memory address. The program must be the owner of a memory block that contains this address.
- `List<Block> getGaps()`: Get a List of the current empty memory blocks.

## Grade

You will be graded according to the level of complexity that you achieve with your memory manager:

- Level 1: Implement a basic algorithm to allocate memory for the programs. Memory access must be protected so that each program cannot access memory allocated for others.
- Level 2: Use segmentation or paging techniques. You will need a logical to physical address mapping.
- Level 3: Use at least one advanced memory management technique:
    * segmentation + paging
    * virtual memory
    * advanced paging or segmentation algorithms.
    * ...

On the last day you will have to present your algorithm and be able to identify its strengths and weaknesses.