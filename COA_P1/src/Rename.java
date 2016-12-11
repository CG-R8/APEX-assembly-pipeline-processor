
/**
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rename
{

	// region Private Member(s)

	private static int head = 0;
	private static int robCount = 0;
	private static int prfMAX = 16;
	public static ArrayList<ROBEntry> reorderBuffer = new ArrayList<ROBEntry>();
	private static Map<String, String> renameTable = new HashMap<String, String>(16);
	private static int counter = 0;

	// Check if Slot in ROB is available (it can have max of 16 entries)
	public static boolean isROBAvailable()
	{
		return (robCount < prfMAX);
	}

	// Rename Instruction (Add entry for Architectural register in Rename table)
	public static Instruction renameInstruction(Instruction instruction)
	{

		instruction = readSourceOperands(instruction);

		System.out.println("renameInstruction");
		String instrSrc1 = "", instrSrc2 = "", instrDest = "";
		Boolean isDestination = false;
		if (instruction.getOperation().equals("STORE"))
		{
			if (instruction.getDestination() != null)
			{
				String renamedDest = "";
				isDestination = false;
				renamedDest = settingPhysicalRegister(instruction.getDestination().getKey(), counter, isDestination);
				instruction.setPhysicalDestination(new RegisgerName_Value<String, Integer>(renamedDest, -1));
			}

			if (instruction.getSrc1() != null)
			{
				String renamedSrc1 = "";
				isDestination = false;
				renamedSrc1 = settingPhysicalRegister(instruction.getSrc1().getKey(), counter, isDestination);
				instruction.setPhysicalSource1(new RegisgerName_Value<String, Integer>(renamedSrc1, -1));

			}
			if (instruction.getSrc2() != null)
			{
				String renamedSrc2 = "";
				isDestination = false;
				renamedSrc2 = settingPhysicalRegister(instruction.getSrc2().getKey(), counter, isDestination);
				instruction.setPhysicalSource2(new RegisgerName_Value<String, Integer>(renamedSrc2, -1));

			}
		} else
		{

			if (instruction.getSrc1() != null)
			{
				String renamedSrc1 = "";
				isDestination = false;
				renamedSrc1 = settingPhysicalRegister(instruction.getSrc1().getKey(), counter, isDestination);
				instruction.setPhysicalSource1(new RegisgerName_Value<String, Integer>(renamedSrc1, -1));

			}
			if (instruction.getSrc2() != null)
			{
				String renamedSrc2 = "";
				isDestination = false;
				renamedSrc2 = settingPhysicalRegister(instruction.getSrc2().getKey(), counter, isDestination);
				instruction.setPhysicalSource2(new RegisgerName_Value<String, Integer>(renamedSrc2, -1));

			}
			if (instruction.getDestination() != null)
			{
				String renamedDest = "";
				isDestination = true;
				renamedDest = settingPhysicalRegister(instruction.getDestination().getKey(), counter, isDestination);
				instruction.setPhysicalDestination(new RegisgerName_Value<String, Integer>(renamedDest, -1));
			}
		}

		// ROBEntry entry = new ROBEntry();
		// entry.setInstructionAddress(instruction.getInstructionAddress());
		// entry.setDestinationAddress(instruction.getDestination().getKey());
		// entry.setStatus(false);
		// entry.setInstrROobContent(instruction.getContent());
		// reorderBuffer.add(robCount, entry);
		//
		// robCount++;

		return instruction;
	}

	public static String settingPhysicalRegister(String instrParameter, int counter2, Boolean isDestination)
	{
		String renamedParameter = "";
		String freephysicalRegister = new String();
		Boolean isArchRegPresent = false;
		if (renameTable.isEmpty())
		{
			System.out.println(" First Entry in physical register ");
			freephysicalRegister = Rename.getFreeRegister();
			renameTable.put(instrParameter, freephysicalRegister);
			renamedParameter = freephysicalRegister;
			Simulator.physicalRegisterFile.put(freephysicalRegister, 1);
			counter++;

		} else
		{
			if (isDestination)
			{
				for (Map.Entry<String, String> entry : renameTable.entrySet()) // check
				{
					if (instrParameter.equals(entry.getKey()))
					{
						isArchRegPresent = true;
						// ADD R1 -- R1 present in rename table. assign p4 at
						// same entry
						freephysicalRegister = Rename.getFreeRegister();
						entry.setValue(freephysicalRegister);
						renamedParameter = freephysicalRegister;
						Simulator.physicalRegisterFile.put(freephysicalRegister, 1);
						System.out.println("Destination Register : " + instrParameter + " : " + entry.getKey() + " Physical Reg : " + entry.getValue());

						if (entry.getValue() == null)
						{
							System.out.println("***************************************************************");
							System.out.println("**********RUNNING OUT OF PHYSICAL REGISTERS********************");
						}
					}

				}
				if (!isArchRegPresent)
				{
					freephysicalRegister = Rename.getFreeRegister();
					renameTable.put(instrParameter, freephysicalRegister);
					renamedParameter = freephysicalRegister;
					Simulator.physicalRegisterFile.put(freephysicalRegister, 1);
					counter++;
					System.out.println();

				}
			} else
			{
				// ADD R1 'R2'
				for (Map.Entry<String, String> entry : renameTable.entrySet())
				{
					if (instrParameter.equals(entry.getKey()))
					{
						isArchRegPresent = true;
						// Do nothing when a source register is present in
						// physical register.
						System.out.println("Register : " + instrParameter + " : " + entry.getKey() + " Physical Reg : " + entry.getValue());
						renamedParameter = entry.getValue();
					}

				}
				if (!isArchRegPresent)
				{
					System.out.println("Problem with input file");
					System.out.println("New physical register added : " + instrParameter + " : " + "P" + counter);
					renameTable.put(instrParameter, "P" + counter);
					Simulator.physicalRegisterFile.put("P" + counter, 1);
					counter++;
				}
			}
		}
		return renamedParameter;
	}

	public static String getFreeRegister()
	{
		for (Map.Entry<String, Integer> entry : Simulator.physicalRegisterFile.entrySet())
		{
			if (entry.getValue().equals(0))
			{
				return entry.getKey();
			}
		}
		return null;

	}

	public static Instruction readSourceOperands(Instruction instruction)
	{

		boolean isSourceValid = true;
		for (Map.Entry<String, String> entry : renameTable.entrySet())
		{

			if (instruction.getSrc1() != null && instruction.getSrc1().getKey().equals(entry.getKey()))
			{

			}
		}
		instruction.isSourceValid(isSourceValid);
		return instruction;
	}
	// // Returns PRF file
	// public static int[] getPhysicalRegFile()
	// {
	// return physicalRegFile;
	// }

	// Returns Rename table
	public static Map<String, String> getRenameTable()
	{
		return renameTable;
	}
	// endregion
}