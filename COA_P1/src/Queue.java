
import java.util.ArrayList;

public class Queue
{

	private static int iQMAX = 20;
	private static int lSQMAX = 4;
	private static ArrayList<Instruction> isssueQueue = new ArrayList<Instruction>(iQMAX);
	private static ArrayList<Instruction> loadStoreQueue = new ArrayList<Instruction>(lSQMAX);

	// Check if Issue queue is available (Issue can contain up to 8
	// instructions)
	public static boolean isIQAvailable()
	{
		return isssueQueue.size() < iQMAX;
	}

	// Check if Load Store queue is available (Load Store queue can contain up
	// to 4 instructions)
	private static boolean isLSQAvailable()
	{
		return loadStoreQueue.size() < lSQMAX;
	}

	public static boolean isIQEmpty()
	{
		return isssueQueue.size() == 0;
	}
	public static void markAllJustaddedFalse()
	{
		int size = isssueQueue.size();
		System.out.println("Size of Issue Que : "+size);
		while(size!=0)
		{
			isssueQueue.get(size-1).setJustAddedToQ(false);
			size--;
		}	
	}
	public static Instruction pullIQInstruction(String fuType)
	{
		String instrOpertaionType = null;
		if (fuType.equals("BRANCH"))
		{
			instrOpertaionType = TypesOfOperations.BNZ + "|" + TypesOfOperations.BZ + "|" + TypesOfOperations.JUMP + "|" + TypesOfOperations.BAL + "|" + TypesOfOperations.HALT;
		} else if (fuType.equals("ALU"))
		{
			instrOpertaionType = TypesOfOperations.ADD + "|" + TypesOfOperations.SUB + "|" + TypesOfOperations.MOVC + "|" + TypesOfOperations.EXOR + "|" + TypesOfOperations.AND + "|" + TypesOfOperations.OR;
		} else if (fuType.equals("MEM"))
		{
			instrOpertaionType = TypesOfOperations.LOAD + "|" + TypesOfOperations.STORE;
		} else if (fuType.equals("MUL"))
		{
			instrOpertaionType = TypesOfOperations.MUL;
		} else
		{
			instrOpertaionType = "BAD";
			System.out.println("--------------BAD FU TYPE ----------------BAD------------------------");
		}

		Instruction instruction = new Instruction();
		for (Instruction instr : isssueQueue)
		{
			if (instrOpertaionType.contains(instr.getOperation()))
			{
				if (instr.isSourceValid())
				{
					instruction = instr;
					break;
				}
			}
		}
		if (!instruction.isNOP())
			isssueQueue.remove(instruction);

		return instruction;
	}

	// Move instruction from load store queue to VFU if source operands are
	// validated(valid bit = 1) or
	// if there is a valid source in physical register file
	public static Instruction pullLSQInstruction()
	{
		Instruction instruction = new Instruction();
		if (loadStoreQueue.size() > 0)
		{
			instruction = loadStoreQueue.get(0);

			// if (!instruction.isSourceValid())
			// instruction = Rename.readSourceOperandsFromPRF(instruction);

			if (instruction.isSourceValid())
			{
				loadStoreQueue.remove(instruction);
			} else
			{
				instruction = new Instruction();
			}
		}
		return instruction;
	}

	// Add instruction in Issue Queue / Load Store Queue based on Instruction
	// operation type
	public static boolean addToQueue(Instruction instruction)
	{
		boolean addedToQueue = false;
		// Read operands from Physical register file
		// if (!instruction.isSourceValid()) {
		// instruction = Rename.readSourceOperandsFromPRF(instruction);
		// }

		if (isIQAvailable())
		{
			System.out.println("In the add Q function" + " " + instruction.getContent());

			// Check if there is empty slot in IQ
			isssueQueue.add(instruction);
			addedToQueue = true;
		}
		return addedToQueue;
	}

	// Return Issue Queue
	public static ArrayList<Instruction> retrieveIsssueQueue()
	{
		return isssueQueue;
	}

	// Return Load Store Queue
	public static ArrayList<Instruction> retrieveLoadStoreQueue()
	{
		return loadStoreQueue;
	}
}