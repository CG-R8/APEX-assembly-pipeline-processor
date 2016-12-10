
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
	static int[] memoryBlocks;
	static Map<String, Instruction> stages;
	static Map<String, Instruction> latches;
	static Map<String, Integer> registerFile;

	static Map<String, Integer> freePhysicalRegister;
	static Map<String, Integer> URF = new HashMap<String, Integer>(16);;
	static int specialRegister;
	static boolean isComplete;
	static boolean isValidSource;
	static public RegisgerName_Value<String, Integer> forwardingReg = null;
	static public RegisgerName_Value<String, Integer> forwardingRegMEMtoEX = null;
	private static boolean isFetchInstruction;
	public static boolean isALU1FUAvailable;
	public static Map<String, Integer> physicalRegisterFile = new HashMap<String, Integer>(16);
	private static boolean isMULFUAvailable=true;
	private static int mulCounter = 1;

	private static void fetchStage()
	{
		InstrParser parser = new InstrParser();
		Instruction instruction;

		if (isFetchInstruction)
		{
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
				e.printStackTrace();
			}
		}
	}

	private static void decodeStage1()
	{

		moveInstruction("D1", "F");

		if (stages.get("D1") != null)
			System.out.println("--------Decode 1----------> " + stages.get("D1").getContent());
	}

	private static void decodeStage2()
	{
		moveInstruction("D2", "D1");
		Instruction instruction = stages.get("D2");
		if (instruction != null && !instruction.isNOP())
		{
			isFetchInstruction = false;

			if (Rename.isROBAvailable())
			{
				instruction = Rename.renameInstruction(instruction);
				Queue.markAllJustaddedFalse();
				instruction = getSourceValues(instruction);
				instruction.setJustAddedToQ(true);
				isFetchInstruction = Queue.addToQueue(instruction);
			}
		}
		// Remove read if valid bit is set as 1
		// Rename.retireROBEntry();
		if (stages.get("D2") != null)
			System.out.println("--------Decode 2----------> " + stages.get("D2").getContent());

	}

	

	private static void dispatch()
	{

		/*
		 * Check issue Q is full HOLD the current instuction 
		 * 
		 * If IQ not Full send
		 * renamed instruction. To IQ tail 
		 * 
		 * Send the renamed instruction to
		 * ROB.tail 
		 * Set that entries ROB valid bit 0 
		 * 
		 * Check if already there is
		 * instruction which is in Decode but not in IQ Push suh instruction
		 * into IQ and then take new instruction in Decode
		 */
		
		if (isALU1FUAvailable)
		{
			Instruction instruction = Queue.pullIQInstruction("ALU");
			// if the source valid bits are not 1
			if (instruction.isJustAddedToQ())
			{
				instruction.setJustAddedToQ(false);
				isFetchInstruction = Queue.addToQueue(instruction);
			} else
			{
				if (instruction.isqDispatchable())
				{
					if (!instruction.isNOP())
						execute1(instruction);
				} else 
				{
					if (!instruction.isNOP())
					isFetchInstruction = Queue.addToQueue(instruction);
				}

			}
		}
				if (isMULFUAvailable)
				{
					Instruction  instruction=Queue.pullIQInstruction("MUL");
					if(instruction.isJustAddedToQ())
					{
						instruction.setJustAddedToQ(false);
						isFetchInstruction = Queue.addToQueue(instruction);
					}else
					{
					if(!instruction.isNOP())
						multiplication();
					}
				}else if(mulCounter>1)
				{
					multiplication();
				}
	}

	private static void execute1(Instruction instruction)
	{
		ExecutionOfOpcode functionUnit = new ExecutionOfOpcode();
		if (instruction != null && !instruction.isNOP())
		{
			instruction = functionUnit.executeInstruction(instruction);
			latches.put("I", instruction);
		}
		moveInstruction("E", "I");

		if (stages.get("E") != null)
			System.out.println("--------ALU1 ----------> " + stages.get("E").getContent());
	}

	
	
	
	
	private static void multiplication()
	{
		ExecutionOfOpcode functionUnit = new ExecutionOfOpcode();
		Instruction instruction= new Instruction();
		if(mulCounter==1)
		{
			instruction=Queue.pullIQInstruction("MUL");
			if(!instruction.isNOP())
			{
				instruction = functionUnit.executeInstruction(instruction);
				stages.put("MUL", instruction);
			}
		}
		if(mulCounter==4)
		{
			//write result to URF.
			System.out.println("=====Committing the MUL value===========================================");
			mulCounter=1;
			URF.put(stages.get("MUL").getPhysicalDestination().getKey(),stages.get("MUL").getDestination().getValue());
			stages.put("MUL", new Instruction());
		}
		if(stages.containsKey("MUL")&& !stages.get("MUL").isNOP())
		mulCounter++;
		
		if (stages.get("MUL") != null)
			System.out.println("--------MUL  ----------> " + stages.get("MUL").getContent());
	}
	

	private static void execute2Stage()
	{
		if (latches.containsKey("E"))
		{

			if (latches.get("E") != null)
				System.out.println("--------ALU 2----------> " + latches.get("E").getContent());
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
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;

		Instruction branchInstr = new Instruction();
		branchInstr = latches.get("I");

		if (latches.containsKey("I"))
		{
			if (!latches.get("I").isNOP())
			{
				if (controlFlowInstruction.contains(latches.get("I").getOperation()))
				{
					if (stages.containsKey("B1"))
						latches.put("B1", stages.get("B1"));
					else
						latches.put("B1", new Instruction());
					stages.put("B1", latches.get("I"));
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

		if (latches.containsKey("I"))
		{
			if (!latches.get("I").isNOP() && controlFlowInstruction.contains(latches.get("I").getOperation()))
			{
				if ((forwardingReg != null) && (forwardingRegMEMtoEX != null) && forwardingReg.getKey().equals(forwardingRegMEMtoEX.getKey()))
				{
					forwardingRegMEMtoEX.setValue(forwardingReg.getValue());
				}
				if ((branchInstr.getDestination() != null) && (branchInstr.getDestination().getKey().equals(forwardingReg.getKey())))
					branchInstr.setDestination(forwardingReg.getValue());
				if ((branchInstr.getDestination() != null) && (branchInstr.getDestination().getKey().equals(forwardingRegMEMtoEX.getKey())))
				{
					branchInstr.setDestination(forwardingRegMEMtoEX.getValue());
				}
				if (latches.get("I").getOperation().equals(TypesOfOperations.BAL))
				{
					specialRegister = currentPC - 4;
				}
				registerVal = latches.get("I").getLiteral();
				if (latches.get("I").getDestination() != null && registerFile.containsKey(latches.get("I").getDestination().getKey()))
				{
					registerVal = registerFile.get(latches.get("I").getDestination().getKey());

					if (registerVal == null)
						registerVal = 0;
				}
				if (stages.get("E2").getDestination() != null)
				{
					dest = stages.get("E2").getDestination().getValue();
				}
				Integer pcCounter = functionUnit.evaluateBranchInstr(latches.get("I"), dest, currentPC, registerVal, specialRegister);
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
			stages.put("Dly", new Instruction());
		}
	}

	private static Instruction executeInstruction2Method(Instruction instruction)
	{
		Integer aluResultValue = 0;
		String aluResultRegister;
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		if (!controlFlowInstruction.contains(instruction.getOperation()))
			if (latches.containsKey("E"))
			{
				forwardingReg = instruction.getDestination();
				aluResultValue = instruction.getDestination().getValue();
				aluResultRegister = instruction.getPhysicalDestination().getKey().toString();
				URF.put(aluResultRegister, aluResultValue);
			}
		

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
					forwardingRegMEMtoEX = latches.get("E2").getDestination();
				}
				moveInstruction("M", "E2");
			}
		}
	}

	private static void writebackStage()
	{
		String controlFlowInstruction = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		if (latches.containsKey("M"))
		{
			moveInstruction("W", "M");
		}
		if (stages.containsKey("W") && !stages.get("W").isNOP())
		{
			getSourceValues(stages.get("W"));

			// Check instruction in W is one of the control flow instr or not
			// and not STORE
			if (!controlFlowInstruction.contains(stages.get("W").getOperation()) && !stages.get("W").getOperation().equals(TypesOfOperations.STORE))
			{
				RegisgerName_Value<String, Integer> destinationReg = stages.get("W").getDestination();
				registerFile.put(destinationReg.getKey(), destinationReg.getValue());
			}
			if (stages.get("W").getOperation().equals(TypesOfOperations.HALT))
			{
				stages.put("F", new Instruction());
				stages.put("D", new Instruction());
				stages.put("E", new Instruction());
				stages.put("E2", new Instruction());
				stages.put("B1", new Instruction());
				stages.put("Dly", new Instruction());
				stages.put("M", new Instruction());
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
		memoryBlocks = new int[4000];
		registerFile = new HashMap<String, Integer>();
		stages = new HashMap<String, Instruction>();
		latches = new HashMap<String, Instruction>();
		specialRegister = 0;
		isComplete = false;
		isValidSource = true;
		isFetchInstruction = true;
		isALU1FUAvailable = true;
		int counter = 0;
		while (counter < 16)
		{
			physicalRegisterFile.put("P" + counter, 0);
			counter++;
		}

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
		for (int i = 1; i <= noCycles; i++)
		{
			if (i == 23)
			{
				System.out.println("");
			}
			System.out.println("---------------------------- Cycle : " + i + "--------------------------------");
			fetchStage();
			decodeStage1();
			decodeStage2();
			dispatch();
//			multiplication();
			// execute1();
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
		if (stages.get("D1") != null)
			System.out.println("--------Decode1----------> " + stages.get("D1").getContent());
		if (stages.get("D2") != null)
			System.out.println("--------Decode2----------> " + stages.get("D2").getContent());
		// if (stages.get("I") != null)
		// System.out.println("--------IssueQeue------> " +
		// stages.get("I").getContent());
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
		System.out.println("\n U R F : ");
		for (Entry<String, Integer> register : URF.entrySet())
		{
			System.out.print(register.getKey() + " : " + register.getValue() + "|\t|");
		}
		System.out.println("\nR A T : ");
		for (Entry<String, String> register : Rename.getRenameTable().entrySet())
		{
			System.out.print(register.getKey() + " : " + register.getValue() + "|\t|");
		}
		// System.out.println("Special Register X:" + specialRegister);
		// System.out.println("\n0 to 99 Memory Address Details: ");
		System.out.println("\nIssue Queue: ");
		for (Instruction instruction : Queue.retrieveIsssueQueue())
		{
			System.out.println(instruction.getContent());
		}

		// for (int i = 0; i < 100; i++)
		// {
		// memoryValues.append(" [" + i + " - " + memoryBlocks[i] + "] ");
		// if (i > 0 && i % 10 == 0)
		// memoryValues.append("\n");
		// }

		//
		// System.out.println("Physical Register File:");
		//
		// StringBuilder rntValues = new StringBuilder();
		// for(Entry<String, String> renameEntry :
		// Rename.getRenameTable().entrySet()){
		// rntValues.append(renameEntry.getKey() + " : " +
		// renameEntry.getValue() + " | ");
		//
		// }

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
				System.out.println(" 1 : Initialize\n 2 : Simulate \n 3 : Display\n 4 : Exit");
//				scanner = new Scanner(System.in);
//				switch (scanner.nextLine())
				switch ("2")

					{
					case "1":
						Initialize();
						break;
					case "2":
						Initialize();
						System.out.print("Please enter no of cycles : ");
//						Simulate(Integer.parseInt(scanner.nextLine()));
						Simulate(30);

						break;
					case "3":
						Display();
						break;
					case "4":
						System.exit(0);
						;
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
			System.out.println("-------------------------Instruction Address : " + currentPC + "------------------------");
			lines.close();

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
	public static Integer readRegister(RegisgerName_Value<String, Integer> pair)
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
		RegisgerName_Value<String, Integer> src1 = instruction.getSrc1();
		RegisgerName_Value<String, Integer> src2 = instruction.getSrc2();
		RegisgerName_Value<String, Integer> destination = instruction.getDestination();
		boolean isSrc1Valid = true, isSrc2Valid = true, isDestValid = true;
		boolean isBalValid = true;

		if (src1 != null)
		{
			isSrc1Valid = checkFlowDependencies(src1, "E") && checkFlowDependencies(src1, "I");
			instruction.setSrc1(readRegister(src1));
		}
		if (src2 != null)
		{
			isSrc2Valid = checkFlowDependencies(src2, "E") && checkFlowDependencies(src2, "I");
			instruction.setSrc2(readRegister(src2));
		}
		if (instruction.getOperation().equals(TypesOfOperations.BAL))
		{
			isBalValid = checkFlowDependencies(destination, "E") && checkFlowDependencies(destination, "I");
			instruction.setDestination(-1);
		}
		if (instruction.getOperation().equals(TypesOfOperations.STORE))
		{
			isDestValid = checkFlowDependencies(destination, "E") && checkFlowDependencies(destination, "E2") && checkFlowDependencies(destination, "M");
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
	private static boolean checkFlowDependencies(RegisgerName_Value<String, Integer> src, String stage)
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
				if (!lastInstructionM.isNOP() && lastInstructionM.getOperation().equals("LOAD") && (lastInstructionM.getDestination().getKey().equals(instruction.getDestination().getKey())))
				{
					instruction.setDestination(lastInstructionM.getDestination().getValue());
				} else if ((!lastInstructionM.isNOP()) && lastInstructionM.getDestination().getKey().equals(instruction.getDestination().getKey()))
				{
					instruction.setDestination(lastInstructionM.getDestination().getValue());
				} else
				{
					instruction.setDestination(readRegister(instruction.getDestination()));
				}
				memoryBlocks[instruction.getMemoryAddress()] = instruction.getDestination().getValue();
			}
			if (instruction.getOperation() != null && instruction.getOperation().equals(TypesOfOperations.LOAD))
			{
				instruction.setDestination(memoryBlocks[instruction.getMemoryAddress()]);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return instruction;
	}
	private static Instruction getSourceValues(Instruction instruction)
	{
		RegisgerName_Value<String, Integer> pSrc1 = instruction.getPhysicalSource1();
		RegisgerName_Value<String, Integer> pSrc2 = instruction.getPhysicalSource2();
		boolean src1_local_valid = false;
		boolean src2_local_valid = false;

		if(pSrc1!=null)
		{
			for (Entry<String, Integer> entry : URF.entrySet())
			{
				if(entry.getKey().equals(pSrc1.getKey()))
				{
					instruction.setPhysicalSource1(new RegisgerName_Value<String, Integer>(entry.getKey(), entry.getValue()));
					instruction.setSrc1Valid(true);
					src1_local_valid = true;
					break;
				}
			}
		}
		if(pSrc2!=null)
		{
			for (Entry<String, Integer> entry : URF.entrySet())
			{
				if(entry.getKey().equals(pSrc2.getKey()))
				{
					instruction.setPhysicalSource2(new RegisgerName_Value<String, Integer>(entry.getKey(), entry.getValue()));
					instruction.setSrc2Valid(true);
					src2_local_valid = true;
					break;
				}
			}
		}
		if(pSrc1==null && (pSrc2==null))
		{
			instruction.setqDispatchable(true);
		}else
		if(("STORE|LOAD|BAL").contains(instruction.getOperation()))
		{
			instruction.setqDispatchable(src1_local_valid);
		}
		else
		{
			instruction.setqDispatchable(src2_local_valid && src1_local_valid);
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