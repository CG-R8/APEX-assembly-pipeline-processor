
/**
 *
 */
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Simulator
{

	static int currentPC;
	static int currentFilePointer;
	static int[] memory;
	static Map<String, Instruction> stages;
	static Map<String, Instruction> latches;
	static Map<String, Integer> registerFile;
	static int specialRegister;
	static boolean isComplete;
	static boolean isValidSource;
	static public KeyValue<String, Integer> forwardingReg = null;
	static public KeyValue<String, Integer> forwardingRegMEMtoEX = null;

	/**
	 * Fetch stage: Check current instruction in Decode stage is present and is
	 * NOP? then check the registers values and set {@isSourceValid} flag
	 * 
	 * 
	 */
	private static void fetchStage()
	{
		InstrParser parser = new InstrParser();
		// Check if the current cycle have anything in decode stage and is it
		// NOP
		if (stages.containsKey("D") && !stages.get("D").isNOP())
		{
			stages.put("D", getSRCFromRegister(stages.get("D")));
		}
		if (isValidSource)
		{
			Instruction instruction;
			try
			{
				instruction = parser.parseInstruction(getContent(), currentPC);

				if (stages.containsKey("F"))
				{
					latches.put("F", stages.get("F"));
				}
				stages.put("F", instruction);
			} catch (IOException e)
			{
				System.err.println("Got exception In fetch stage");
				e.printStackTrace();
			}
		}
	}

	/**
	 * This function decodes the instruction
	 * <p>
	 * This function also set flag for source is valid or not
	 */
	private static void decodeStage()
	{
		if (isValidSource)
		{
			if (latches.containsKey("F") && !latches.get("F").isNOP())
			{
				try
				{
					latches.put("F", getSRCFromRegister(latches.get("F")));
				} catch (Exception e)
				{
					System.err.println("Error while reading values from Registers in Decode function");
					e.printStackTrace();
				}
			}
			moveInstruction("D", "F");
		} else
		{
			latches.put("D", new Instruction());
		}
	}

	private static void execute1()
	{
		Integer registerVal = 0;
		Integer dest = null;
		boolean flushRegisterValues = false;
		boolean branchFUflag = false;
		ExecutionOfOpcode functionUnit = new ExecutionOfOpcode();
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|"
				+ TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;

		if (latches.containsKey("D"))
		{
			if (!latches.get("D").isNOP())
			{
				// Check if the instruction in Decode stage is not Control flow
				// instruction
				if (!controlFlowInstruction.contains(latches.get("D").getOperation()))
				{
					Instruction instructionEx = latches.get("D");
					// TODO check the src 1 and src 2 with forwarding registers
					if ((forwardingReg != null) && (forwardingRegMEMtoEX != null)
							&& forwardingReg.getKey().equals(forwardingRegMEMtoEX.getKey()))
					{
						forwardingRegMEMtoEX.setValue(forwardingReg.getValue());
					}

					if ((instructionEx.getSrc1() != null)
							&& (instructionEx.getSrc1().getKey().equals(forwardingReg.getKey())))
						instructionEx.setSrc1(forwardingReg.getValue());
					if ((instructionEx.getSrc2() != null)
							&& (instructionEx.getSrc2().getKey().equals(forwardingReg.getKey())))
						instructionEx.setSrc2(forwardingReg.getValue());
					if ((instructionEx.getSrc1() != null)
							&& (instructionEx.getSrc1().getKey().equals(forwardingRegMEMtoEX.getKey())))
					{
						instructionEx.setSrc1(forwardingRegMEMtoEX.getValue());
					}
					if ((instructionEx.getSrc2() != null)
							&& (instructionEx.getSrc2().getKey().equals(forwardingRegMEMtoEX.getKey())))
					{

						instructionEx.setSrc2(forwardingRegMEMtoEX.getValue());
					}

					latches.put("D", functionUnit.executeInstruction(latches.get("D")));
				} else
				// Here we have the branch instruction
				{
					branchFUflag = true;
					latches.put("E", stages.get("E"));
					stages.put("E", new Instruction()); // Add NOP in the ALU1
				}
			}
			if (!branchFUflag)
			{
				moveInstruction("E", "D");
				if (flushRegisterValues)
					flushRegister();
			}
		}
	}

	private static void execute2Stage()
	{

		if (latches.containsKey("E"))
		{
			if (!latches.get("E").isNOP())
			{
				latches.put("E", executeInstruction2Method(latches.get("E")));
			}
			moveInstruction("E2", "E");
		} else
		{
			latches.put("E2", new Instruction());
		}
	}

	private static void branchStage()
	{
		Integer registerVal = 0;
		Integer dest = null;
		boolean flushRegisterValues = false;
		ExecutionOfOpcode functionUnit = new ExecutionOfOpcode();
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|"
				+ TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;

		Instruction branchInstr = new Instruction();
		branchInstr = latches.get("D");

		if (latches.containsKey("D"))
		{
			if (!latches.get("D").isNOP())
			{
				if (controlFlowInstruction.contains(latches.get("D").getOperation()))
				{
					if (stages.containsKey("B1"))
						latches.put("B1", stages.get("B1"));
					else
						latches.put("B1", new Instruction());
					stages.put("B1", latches.get("D"));
				} else
				{
					if (stages.containsKey("B1"))
						latches.put("B1", stages.get("B1"));
					else
						latches.put("B1", new Instruction());
					stages.put("B1", new Instruction());
				}
			} else
			{
				if (stages.containsKey("B1"))
					latches.put("B1", stages.get("B1"));
				else
					latches.put("B1", new Instruction());
				stages.put("B1", new Instruction());
			}
		}

		if (latches.containsKey("D"))
		{
			if (!latches.get("D").isNOP() && controlFlowInstruction.contains(latches.get("D").getOperation()))
			{
				if ((forwardingReg != null) && (forwardingRegMEMtoEX != null)
						&& forwardingReg.getKey().equals(forwardingRegMEMtoEX.getKey()))
				{
					forwardingRegMEMtoEX.setValue(forwardingReg.getValue());
				}
				if ((branchInstr.getDestination() != null)
						&& (branchInstr.getDestination().getKey().equals(forwardingReg.getKey())))
					branchInstr.setDestination(forwardingReg.getValue());
				if ((branchInstr.getDestination() != null)
						&& (branchInstr.getDestination().getKey().equals(forwardingRegMEMtoEX.getKey())))
				{
					branchInstr.setDestination(forwardingRegMEMtoEX.getValue());
				}
				if (latches.get("D").getOperation().equals(TypesOfOperations.BAL))
				{
					specialRegister = currentPC - 4;
				}
				registerVal = latches.get("D").getLiteral();
				if (latches.get("D").getDestination() != null
						&& registerFile.containsKey(latches.get("D").getDestination().getKey()))
				{
					registerVal = registerFile.get(latches.get("D").getDestination().getKey());

					if (registerVal == null)
						registerVal = 0;
				}
				if (stages.get("E2").getDestination() != null)
				{
					dest = stages.get("E2").getDestination().getValue();
				}
				Integer pcCounter = functionUnit.evaluateBranchInstr(latches.get("D"), dest, currentPC, registerVal,
						specialRegister);
				if (currentPC != pcCounter)
				{
					currentPC = pcCounter;
					currentFilePointer = (currentPC - 4000) / 4;
					flushRegisterValues = true;
				}
				if (flushRegisterValues)
				{
					flushRegister();
					currentPC = currentPC - 4;
				}
			}
		}
	}

	private static void delayStage()
	{
		if (latches.containsKey("B1"))
		{
			if (stages.containsKey("Dly"))
				latches.put("Dly", stages.get("Dly"));
			else
				latches.put("Dly", new Instruction());

			stages.put("Dly", latches.get("B1"));
		} else
		{
			// latches.put("B", branchDelayMethod(latches.get("B")));
			stages.put("Dly", new Instruction());
		}
	}

	private static Instruction executeInstruction2Method(Instruction instruction)
	{
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|"
				+ TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		if (!controlFlowInstruction.contains(instruction.getOperation()))
			if (latches.containsKey("E"))
			{
				forwardingReg = instruction.getDestination();
			}
		return instruction;
	}

	private static Instruction branchDelayMethod(Instruction instruction)
	{
		return instruction;
	}

	private static void memoryStage()
	{
		if (latches.containsKey("Dly") && !latches.get("Dly").isNOP())
		{
			moveInstruction("M", "Dly");
		} else
		{
			if (latches.containsKey("E2"))
			{
				if (!latches.get("E2").isNOP())
				{
					latches.put("E2", performMemoryOperation(latches.get("E2")));
					// TODO mem to EX forwarding
					forwardingRegMEMtoEX = latches.get("E2").getDestination();
				}
				moveInstruction("M", "E2");
			}
		}
	}

	private static void writebackStage()
	{
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|"
				+ TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		if (latches.containsKey("M"))
		{
			moveInstruction("W", "M");
		}
		if (stages.containsKey("W") && !stages.get("W").isNOP())
		{
			// Check instruction in W is one of the control flow instr or not
			// and not STORE
			if (!controlFlowInstruction.contains(stages.get("W").getOperation())
					&& !stages.get("W").getOperation().equals(TypesOfOperations.STORE))
			{
				KeyValue<String, Integer> destinationReg = stages.get("W").getDestination();
				registerFile.put(destinationReg.getKey(), destinationReg.getValue());
			}
			if (stages.get("W").getOperation().equals(TypesOfOperations.HALT))
			{
				isComplete = true;
			}
		}
	}

	/**
	 * Initialize the current pc , filepointer Create registerFile = new
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
		isComplete = false;
		isValidSource = true;
		System.out.println("-----Initialization Completed------");
	}

	/**
	 * Main for LOOP which simulate 'n' no. of cycles
	 * 
	 * @param noCycles
	 *            : number of cycles
	 * @throws IOException
	 */
	private static void Simulate(int noCycles) throws IOException
	{
		for (int i = 1; i < noCycles; i++)
		{
			System.out.println("---------------------------- Cycle : " + i + "--------------------------------");
			fetchStage();
			decodeStage();
			execute1();
			execute2Stage();
			branchStage();
			delayStage();
			memoryStage();
			writebackStage();
			Display();
			System.out.println("-----------------------------------------------------------------------------");
			if (isComplete)
				break;
		}
	}

	/**
	 * Displays the output of current instruction cycle
	 */
	private static void Display()
	{
		StringBuilder memoryValues = new StringBuilder();
		System.out.println("\nPipleline Stages: ");
		if (stages.get("F") != null)
		System.out.println("--------Fetch-----------> " + stages.get("F").getContent());
		if (stages.get("D") != null)
			System.out.println("--------Decode----------> " + stages.get("D").getContent());
		if (stages.get("E") != null)
			System.out.println("--------Execution1------> " + stages.get("E").getContent());
		if (stages.get("E2") != null)
			System.out.println("--------Execution2------> " + stages.get("E2").getContent());
		if (stages.get("B1") != null)
			System.out.println("--------Branch----------> " + stages.get("B1").getContent());
		if (stages.get("Dly") != null)
			System.out.println("--------Delay-----------> " + stages.get("Dly").getContent());
		if (stages.get("M") != null)
			System.out.println("--------Memory----------> " + stages.get("M").getContent());
		if (stages.get("W") != null)
			System.out.println("--------Writeback-------> " + stages.get("W").getContent());

		System.out.println("\nRegister File Details: \n");
		for (Entry<String, Integer> register : registerFile.entrySet())
		{
			System.out.print(register.getKey() + " : " + register.getValue() + "|\t|");
		}
		System.out.println("Special Register X:" + specialRegister);
		System.out.println("\n0 to 99 Memory Address Details: ");
		for (int i = 0; i < 100; i++)
		{
			memoryValues.append(" [" + i + " - " + memory[i] + "] ");
			if (i > 0 && i % 10 == 0)
				memoryValues.append("\n");
		}
		System.out.println(memoryValues);

	}

	public static void main(String[] args)
	{
		Scanner scanner = null;
		try
		{
			while (true)
			{
				System.out.println("------------------------------Apex Simulator----------------------------------");
				System.out.println("1 : Initialize\n2 : Simulate <no_of_cycles>\n3 : Display");
				scanner = new Scanner(System.in);
				switch (scanner.nextLine())
					{
					case "1":
						Initialize();
						break;
					case "2":
						Initialize();
						System.out.print("Please enter no of cycles : ");
						Simulate(Integer.parseInt(scanner.nextLine()));
						break;
					case "3":
						Display();
						break;
					}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
				scanner.close();
		}
	}

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
			instr = instr.replaceAll("#", "");
			instr = instr.replaceAll(",", "");
			currentFilePointer++;
			currentPC = currentPC + 4;
			System.out.println("******************" + instr + "***********************");
			System.out.println("****************" + currentPC + "*****************");

		} catch (Exception ex)
		{
			// ex.printStackTrace();
		}
		return instr;
	}

	/**
	 * This function moves the instruction to next stage using latches
	 * 
	 * @param cStage
	 *            Current stage
	 * @param pStage
	 *            past stage
	 *            <p>
	 *            Example moveInstruction(E,D)
	 *            <p>
	 *            If stages contain E --> latches.put(E, stages.get(E)); <br>
	 *            If latches contain D -> stages.put(E, latches.get(D));
	 */
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

	/**
	 * Read value from Register file if Register file contains key(R0, R1 ..)
	 * 
	 * @param pair
	 * @return value of register
	 */
	private static Integer readRegister(KeyValue<String, Integer> pair)
	{
		if (pair != null && registerFile.containsKey(pair.getKey()))
		{
			return registerFile.get(pair.getKey());
		}
		return null;
	}

	/**
	 * Check the flow dependencies for both sources of passed instruction with A
	 * E M "stages" This method is used to set isSourceValid value Special case
	 * for STORE type instruction
	 * 
	 * @param instruction
	 * @return instruction
	 */

	private static Instruction getSRCFromRegister(Instruction instruction)
	{
		KeyValue<String, Integer> src1 = instruction.getSrc1();
		KeyValue<String, Integer> src2 = instruction.getSrc2();
		KeyValue<String, Integer> destination = instruction.getDestination();
		boolean isSrc1Valid = true, isSrc2Valid = true, isDestValid = true;
		boolean isBalValid = true;

		if (src1 != null)
		{
			isSrc1Valid = checkFlowDependencies(src1, "E");
			instruction.setSrc1(readRegister(src1));
		}
		if (src2 != null)
		{
			isSrc2Valid = checkFlowDependencies(src2, "E");
			instruction.setSrc2(readRegister(src2));
		}
		if (instruction.getOperation().equals(TypesOfOperations.BAL))
		{
			isBalValid = checkFlowDependencies(destination, "E");
			instruction.setDestination(-1);
		}
		if (instruction.getOperation().equals(TypesOfOperations.STORE))
		{
			isDestValid = checkFlowDependencies(destination, "E") && checkFlowDependencies(destination, "E2")
					&& checkFlowDependencies(destination, "M");
			if (!isDestValid)
			{
				isDestValid = true;
				instruction.setDestination(-1);
			} else
			{
				instruction.setDestination(readRegister(destination));
			}
			isValidSource = isSrc1Valid && isSrc2Valid && isDestValid;
			return instruction;
		}
		isValidSource = isSrc1Valid && isSrc2Valid && isBalValid;
		return instruction;
	}

	/**
	 * This function checks the flow dependencies by comparing the sources of
	 * instruction in Decode stage with destination of next instructions in
	 * "stage"
	 * <p>
	 * If instruction is not STORE If destination of instruction is not NULL If
	 * there is same Register in both instruction
	 * 
	 * @param src
	 *            It is the hashmap Keyval which contain register name and its
	 *            value
	 * @param stage
	 *            It is the string represent "F" or "D" or ....
	 * @return True -if dependencies else false
	 * 
	 */
	private static boolean checkFlowDependencies(KeyValue<String, Integer> src, String stage)
	{
		boolean isDependent = true;

		try
		{
			isDependent = stages.containsKey(stage) && stages.get(stage).getOperation() != null
			// If instruction is not STORE
					&& !stages.get(stage).getOperation().equals(TypesOfOperations.STORE)
					// If destination of instruction is not NULL
					&& stages.get(stage).getDestination() != null
					// If there is same Register in both instruction
					&& stages.get(stage).getDestination().getKey().equals(src.getKey());
			// TODO what if the branch is dependent

			return !(isDependent);
		} catch (Exception e)
		{
			System.err.println("Error while checking the flow dependancies");
			e.printStackTrace();
			System.exit(0);
		}
		return true;
	}

	/**
	 * To process LOAD and STORE operations
	 * 
	 * @param instruction
	 * @return instruction
	 */
	private static Instruction performMemoryOperation(Instruction instruction)
	{
		try
		{
			if (instruction.getOperation() != null && instruction.getOperation().equals(TypesOfOperations.STORE))
			{
				Instruction lastInstructionM = stages.get("M");
				if (!lastInstructionM.isNOP() && lastInstructionM.getOperation().equals("LOAD")
						&& (lastInstructionM.getDestination().getKey().equals(instruction.getDestination().getKey())))
				{
					instruction.setDestination(lastInstructionM.getDestination().getValue());
				} else if ((!lastInstructionM.isNOP())
						&& lastInstructionM.getDestination().getKey().equals(instruction.getDestination().getKey()))
				{
					instruction.setDestination(lastInstructionM.getDestination().getValue());
				} else
				{
					instruction.setDestination(readRegister(instruction.getDestination()));
				}
				memory[instruction.getMemoryAddress()] = instruction.getDestination().getValue();
			}
			if (instruction.getOperation() != null && instruction.getOperation().equals(TypesOfOperations.LOAD))
			{
				instruction.setDestination(memory[instruction.getMemoryAddress()]);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return instruction;
	}

	/**
	 * Flushes the values of register and Fill NOP in F and D of current cycle
	 * and previous cycle
	 * 
	 */
	private static void flushRegister()
	{
		stages.put("F", new Instruction());
		stages.put("D", new Instruction());
		latches.put("F", new Instruction());
		latches.put("D", new Instruction());
	}
}