
/**
 *
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;

public class Simulator
{
	static int currentFilePointer;
	static int currentPC;
	static int[] memory;
	static Map<String, Integer> registerFile;
	static Map<String, Instruction> stages;
	static Map<String, Instruction> latches;
	static int specialRegister;
	static boolean stopExecution;
	static boolean isSourceValid;


	/**
	 * 
	 * Read the line store it in instr Increment the current file pointer
	 * Increment the PC
	 * 
	 * @return instruction
	 * @throws IOException
	 */
	private static String getContent() throws IOException
	{
		String instr = "";
		try
		{
			Stream<String> lines = Files.lines(Paths.get("input.txt"));
			instr = lines.skip(currentFilePointer).findFirst().get();
			currentFilePointer++;
			currentPC = currentPC + 4;
			System.out.println("******************" + instr + "***********************");
			System.out.println("****************" + currentPC + "*****************");

		} catch (Exception ex)
		{
		}
		return instr;
	}

	// Move Instruction to next stage using latches(temporary storage)
	private static void moveInstruction(String cStage, String pStage)
	{
		if (stages.containsKey(cStage))
		{
			latches.put(cStage, stages.get(cStage));
		}
		if (latches.containsKey(pStage))
		{
			stages.put(cStage, latches.get(pStage));
		}
	}

	// Read value from Register file if Register file contains key(R1, R2 ..
	// etc)
	private static Integer readRegister(KeyValue<String, Integer> pair)
	{
		if (pair != null && registerFile.containsKey(pair.getKey()))
		{
			return registerFile.get(pair.getKey());
		}
		return null;
	}

	// Check flow dependency and read sources from register file
	// and in case of STORE read register value and store in Destination
	private static Instruction getSRCFromRegister(Instruction instruction)
	{
		KeyValue<String, Integer> src1 = instruction.getSrc1();
		KeyValue<String, Integer> src2 = instruction.getSrc2();
		KeyValue<String, Integer> destination = instruction.getDestination();
		boolean isSrc1Valid = true, isSrc2Valid = true, isDestValid = true;

		if (src1 != null)
		{
			isSrc1Valid = checkFlowDependencies(src1, "E") && checkFlowDependencies(src1, "M")
					&& checkFlowDependencies(src1, "A");
			instruction.setSrc1(readRegister(src1));
		}
		if (src2 != null)
		{
			isSrc2Valid = checkFlowDependencies(src2, "E") && checkFlowDependencies(src2, "M")
					&& checkFlowDependencies(src2, "A");
			instruction.setSrc2(readRegister(src2));
		}
		if (instruction.getOperation().equals(TypesOfOperations.STORE))
		{
			isDestValid = checkFlowDependencies(destination, "E") && checkFlowDependencies(destination, "M")
					&& checkFlowDependencies(destination, "A");
			instruction.setDestination(readRegister(destination));
			isSourceValid = isSrc1Valid && isSrc2Valid && isDestValid;
			return instruction;
		}
		isSourceValid = isSrc1Valid && isSrc2Valid;
		return instruction;
	}

	// Check flow dependencies(Compare Instruction's source that is in Decode
	// stage with Instruction's destination of Next stages)
	private static boolean checkFlowDependencies(KeyValue<String, Integer> src, String stage)
	{
		return !(stages.containsKey(stage) && stages.get(stage).getOperation() != null
				&& !stages.get(stage).getOperation().equals(TypesOfOperations.STORE)
				&& stages.get(stage).getDestination() != null
				&& stages.get(stage).getDestination().getKey().equals(src.getKey()));
	}

	// Perform memory operation in LOAD STORE operation(Store value in memory in
	// case of STORE and
	// Read value from memory into destination in case of LOAD instruction)
	private static Instruction performMemoryOperation(Instruction instruction)
	{
		if (instruction.getOperation() != null && instruction.getOperation().equals(TypesOfOperations.STORE))
		{
			memory[instruction.getMemoryAddress()] = instruction.getDestination().getValue();
		}
		if (instruction.getOperation() != null && instruction.getOperation().equals(TypesOfOperations.LOAD))
		{
			instruction.setDestination(memory[instruction.getMemoryAddress()]);
		}
		return instruction;
	}

	// Flush register values(Fill Fetch and Decode stage with NOP instruction)
	private static void flushRegister()
	{
		stages.put("F", new Instruction());
		stages.put("D", new Instruction());
		latches.put("F", new Instruction());
		latches.put("D", new Instruction());
	}

	// Fetch Stage - Read Instruction from Instruction file
	private static void fetchInstruction() throws IOException
	{
		InstrParser parser = new InstrParser();
		// Check if the current cycle have anything in decode stage and is it
		// NOP
		if (stages.containsKey("D") && !stages.get("D").isNOP())
		{
			stages.put("D", getSRCFromRegister(stages.get("D")));
		}
		if (isSourceValid)
		{
			Instruction instruction = parser.parseInstruction(getContent(), currentPC);
			if (stages.containsKey("F"))
			{
				latches.put("F", stages.get("F"));
			}
			stages.put("F", instruction);
		}
	}

	// Decode Stage - Read value from register file and store values in SRC1 and
	// SRC2
	private static void decodeInstruction()
	{
		if (isSourceValid)
		{
			if (latches.containsKey("F") && !latches.get("F").isNOP())
			{
				// find the Fetch instruction and pass it to the
				// getSRCFromRegister
				latches.put("F", getSRCFromRegister(latches.get("F")));
				moveInstruction("D", "F");
			}
		} else
		{
			latches.put("D", new Instruction());// Add NOP in latch for the next
												// Stage to consume
		}
	}

	private static Instruction nothingtodo(Instruction instruction)
	{
		return instruction;
	}

	// Execute Instruction based on operation
	private static void executeInstruction()
	{
		Integer registerVal = 0;
		Integer dest = null;
		boolean flushRegisterValues = false;
		ExecutionOfOpcode functionUnit = new ExecutionOfOpcode();
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|"
				+ TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;

		if (latches.containsKey("D"))
		{
			if (!latches.get("D").isNOP())
			{
				// Check if the instruction in Decode stage is Control flow
				// instruction
				if (!controlFlowInstruction.contains(latches.get("D").getOperation()))
				{
					latches.put("D", functionUnit.executeInstruction(latches.get("D")));
				} else
				// Here we have the branch instruction
				{
					if (latches.get("D").getOperation().equals(TypesOfOperations.BAL))
					{
						specialRegister = currentPC - 1;
					}
					if (latches.get("D").getDestination() != null
							&& registerFile.containsKey(latches.get("D").getDestination().getKey()))
					{
						registerVal = registerFile.get(latches.get("D").getDestination().getKey());

						if (registerVal == null)
							registerVal = 0;
					}
					if (stages.get("E").getDestination() != null)
					{
						dest = stages.get("E").getDestination().getValue();
					}
					Integer pcCounter = functionUnit.predictBranch(latches.get("D"), dest, currentPC, registerVal,
							specialRegister);
					if (currentPC != pcCounter)
					{
						currentPC = pcCounter;
						currentFilePointer = (currentPC - 4000) / 4; //// update
																		//// file
																		//// pointer
						flushRegisterValues = true;
					}
				}
			}
			moveInstruction("E", "D");
			if (flushRegisterValues)
				flushRegister();
		}

	}

	private static void ALU()
	{

		if (latches.containsKey("E"))
		{
			if (!latches.get("E").isNOP())
			{
				latches.put("E", nothingtodo(latches.get("E")));
			}
			moveInstruction("A", "E");
		}

	}

	// Memory Operation
	private static void memory()
	{
		if (latches.containsKey("A"))
		{
			if (!latches.get("A").isNOP())
				latches.put("A", performMemoryOperation(latches.get("A")));

			moveInstruction("M", "A");
		}
	}

	// Write back in register file
	private static void writeback()
	{
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|"
				+ TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		if (latches.containsKey("M"))
		{
			moveInstruction("W", "M");
		}
		if (stages.containsKey("W") && !stages.get("W").isNOP())
		{
			if (!controlFlowInstruction.contains(stages.get("W").getOperation())
					&& !stages.get("W").getOperation().equals(TypesOfOperations.STORE))
			{
				KeyValue<String, Integer> destinationReg = stages.get("W").getDestination();
				registerFile.put(destinationReg.getKey(), destinationReg.getValue());
			}
			if (stages.get("W").getOperation().equals(TypesOfOperations.HALT))
			{
				stopExecution = true;
			}
		}
	}

	// Sets default value
	/**
	 * Initialise the current pc , filepointer Create registerFile = new
	 * HashMap<String, Integer>() stages = new HashMap<String, Instruction>();
	 * latches = new HashMap<String, Instruction>(); specialRegister = 0;
	 * stopExecution = false; isSourceValid = true;
	 */
	private static void Initialize()
	{
		currentPC = 3996;
		currentFilePointer = 0;
		memory = new int[4000];
		registerFile = new HashMap<String, Integer>();
		stages = new HashMap<String, Instruction>();
		latches = new HashMap<String, Instruction>();
		specialRegister = 0;
		stopExecution = false;
		isSourceValid = true;
	}

	// Simulate instructions for n cycle
	private static void Simulate(int n) throws IOException
	{
		for (int i = 0; i < n; i++)
		{
			if (i>=99)
			{
				System.out.println("----Cycle 86 + ---");
			}
			fetchInstruction();
			decodeInstruction();
			executeInstruction();
			ALU();
			memory();
			writeback();
			System.out.println("----------------------------" + i + "-----------------------------");
			Display();
			if (stopExecution)
				break;
		}
	}

	// Display Result at the end of n cycle
	private static void Display()
	{
		StringBuilder memoryValues = new StringBuilder();
		System.out.println("\nPipleline Stages: ");

		System.out.println("--------F----------->" + stages.get("F").getContent());
		if (stages.get("D") != null)
			System.out.println("--------D----------->" + stages.get("D").getContent());
		if (stages.get("E") != null)
			System.out.println("--------E----------->" + stages.get("E").getContent());
		if (stages.get("A") != null)
			System.out.println("--------A----------->" + stages.get("A").getContent());
		if (stages.get("M") != null)
			System.out.println("--------M----------->" + stages.get("M").getContent());
		if (stages.get("W") != null)
			System.out.println("--------W----------->" + stages.get("W").getContent());

		// for (Entry<String, Instruction> stage : stages.entrySet()) {
		// System.out.println(stage.getKey() + " : " +
		// stage.getValue().getContent());
		// }
		System.out.println("\nRegister File: ");
		for (Entry<String, Integer> register : registerFile.entrySet())
		{
			System.out.println(register.getKey() + " : " + register.getValue());
		}
		System.out.println("\nMemory Address: ");
		// for(int i=0;i<100; i++){
		// memoryValues.append(" [" + i + " - " + memory[i] + "] ");
		// if(i > 0 && i % 10 == 0)
		// memoryValues.append("\n");
		// }
		System.out.println(memoryValues);
		// System.out.println("X:"+ specialRegister);
	}

	public static void main(String[] args)
	{
		Scanner scanner = null;
		try
		{
			// scanner = new Scanner(System.in);
			String input = null;
			// while (true)
			{
				System.out.println("-------------Input-----------\nInitialize\nSimulate <n>\nDisplay");
				// input = scanner.nextLine();
				switch ("S")
					{
					case "I":
						Initialize();
						break;
					case "S":
						Initialize();
						Simulate(Integer.parseInt("200"));
						break;
					case "D":
						Display();
						break;
					}
			}
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		} finally
		{
			if (scanner != null)
				scanner.close();
		}
	}
}