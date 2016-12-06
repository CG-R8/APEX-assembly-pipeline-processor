
/**
 * @author Balakrishna Lellapalli
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rename
{

	// region Private Member(s)

	private static int head = 0;
	private static int tail = 0;
	private static int robCount = 0;
	private static int prfMAX = 16;
	//	private static ArrayList<ROBEntry> reorderBuffer = new ArrayList<ROBEntry>();
	private static int[] physicalRegFile = new int[16];
	private static Map<String, String> renameTable = new HashMap<String, String>(16);
	private static int counter = 0;

	// endregion

	// region Public Method(s)

	// Check if Slot in ROB is available (it can have max of 16 entries)
	public static boolean isROBAvailable()
	{
		return (robCount < prfMAX);
	}

	// Read Source from Architectural register file if status bit is set as 1 in
	// rename table
	// Else set Reorder buffer index in the instruction (can read later from
	// Reorder buffer if the operand is valid)
	/*
	 public static Instruction readSourceOperands(Instruction instruction) {

		boolean isSourceValid = true;
		for (Map.Entry<String, String> entry : renameTable.entrySet()) {

			if (instruction.getSrc1() != null && instruction.getSrc1().getKey().equals(entry.getKey())) {
				String[] rntValue = entry.getValue().split(" ");

				if (rntValue[1].equals("0")) // Status bit in Rename table
					instruction.setSrc1(XSimulator.getValueFromARF(instruction.getSrc1().getKey()));

				else if (rntValue[1].equals("1"))  {
					instruction.setSource1RobIndex(Integer.valueOf(rntValue[0]));
					isSourceValid = false;
				}
			}

			if (instruction.getSrc2() != null && instruction.getSrc2().getKey().equals(entry.getKey())) {
				String[] rntValue = entry.getValue().split(" ");

				if (rntValue[1].equals("0"))
					instruction.setSrc2(XSimulator.getValueFromARF(instruction.getSrc2().getKey()));

				else if (rntValue[1].equals("1")) {
					instruction.setSource2RobIndex(Integer.valueOf(rntValue[0]));
					isSourceValid = false;
				}
			}
			//Set Source for STORE
			if(instruction.getOperation().equals(Consts.STORE)){

				if(instruction.getDestination() != null && 
						instruction.getDestination().getKey().equals(entry.getKey())){
					String[] rntValue = entry.getValue().split(" ");

					if (rntValue[1].equals("0"))
						instruction.setDestination(XSimulator.getValueFromARF(instruction.getDestination().getKey()));

					else if (rntValue[1].equals("1"))  {
						instruction.setDestinationRobIndex(Integer.valueOf(rntValue[0]));
						isSourceValid = false;
					}
				}
			}
		}
		instruction.isSourceValid(isSourceValid);
		return instruction;
	}
	 */
	// Read Source from Physical register file if status bit is set as 1 in
	// Reorder buffer
	/*
	 * public static Instruction readSourceOperandsFromPRF(Instruction
	 * instruction) { boolean isSourceValid = true;
	 * 
	 * if (instruction.getSrc1() != null && instruction.getSrc1().getValue() ==
	 * null) { if
	 * (reorderBuffer.get(instruction.getSource1RobIndex()).getStatus())
	 * instruction.setSrc1(physicalRegFile[instruction.getSource1RobIndex()]);
	 * else isSourceValid = false; }
	 * 
	 * if (instruction.getSrc2() != null && instruction.getSrc2().getValue() ==
	 * null) { if
	 * (reorderBuffer.get(instruction.getSource2RobIndex()).getStatus())
	 * instruction.setSrc2(physicalRegFile[instruction.getSource2RobIndex()]);
	 * else isSourceValid = false; }
	 * 
	 * // FOR STORE if (instruction.getOperation().equals(Consts.STORE)) { if
	 * (reorderBuffer.get(instruction.getDestinationRobIndex()).getStatus())
	 * instruction.setDestination(physicalRegFile[instruction.
	 * getDestinationRobIndex()]); else isSourceValid = false; }
	 * instruction.isSourceValid(isSourceValid); return instruction; }
	 */

	// Rename Instruction (Add entry for Architectural register in Rename table)
	public static Instruction renameInstruction(Instruction instruction)
	{

		// instruction = readSourceOperands(instruction);
		System.out.println("renameInstruction");
		String instrSrc1 = "", instrSrc2 = "", instrDest = "";
		Boolean isDestination = false;
		if (instruction.getDestination() != null)
		{
			isDestination = true;
			settingPhysicalRegister(instruction.getDestination().getKey(), counter,isDestination);
		}
		if (instruction.getSrc1() != null)
		{
			isDestination = false;
			settingPhysicalRegister(instruction.getSrc1().getKey(), counter,isDestination);
		}
		if (instruction.getSrc2() != null)
		{
			isDestination = false;
			settingPhysicalRegister(instruction.getSrc2().getKey(), counter,isDestination);

		}

		// String rntValue = tail + " " + 1;
		// renameTable.put(instruction.getDestination().getKey(), rntValue);
		// instruction.setDestinationRobIndex(tail);
		//
		// ROBEntry entry = new ROBEntry();
		// entry.setInstructionAddress(instruction.getInstructionAddress());
		// entry.setDestinationAddress(instruction.getDestination().getKey());
		// entry.setStatus(false);
		// reorderBuffer.add(tail, entry);
		//
		// if (tail < prfMAX)
		// tail++;
		//
		// if (tail == prfMAX)
		// tail = 0;
		//
		// robCount++;

		return instruction;
	}

	public static void settingPhysicalRegister(String instrParameter, int counter2, Boolean isDestination)
	{
		Boolean isArchRegPresent=false;
		if(renameTable.isEmpty())
		{
			System.out.println(" First Entry in physical register ");
			renameTable.put(instrParameter, "P"+counter);
			counter++;

		}else
		{
			if(isDestination)
			{
				for (Map.Entry<String, String> entry : renameTable.entrySet()) 
				{
					if(instrParameter.equals(entry.getKey()))
					{
						isArchRegPresent=true;
						// ADD R1  -- R1 present in rename table. assign p4 at same entry
						entry.setValue("P" + counter++);
						System.out.println("Destination Register : "+instrParameter+" : "+entry.getKey()+" Physical Reg : "+entry.getValue());
					}

				}
				if(!isArchRegPresent)
				{
					renameTable.put(instrParameter, "P"+counter);
					counter++;

				}
			}
			else
			{
				//ADD R1 'R2'
				for (Map.Entry<String, String> entry : renameTable.entrySet()) 
				{
					if(instrParameter.equals(entry.getKey()))
					{
						isArchRegPresent=true;
						//Do nothing when a source register is present in physical register.
						System.out.println("Register : "+instrParameter+" : "+entry.getKey()+" Physical Reg : "+entry.getValue());
					}


				}
				if(!isArchRegPresent)
				{
					System.out.println("New physical register added : "+instrParameter+" : "+ "P"+counter);
					renameTable.put(instrParameter, "P"+counter);
					counter++;
				}
			}
		}
	}

	/*
	 * // Forward result in Physical register file and set status bit of ROB to
	 * 1 public static void forwardResult(Instruction instruction) {
	 * 
	 * if (instruction.getOperation().equals(Consts.STORE)) {
	 * Simulator.saveValueInMemory(instruction.getMemoryAddress(),
	 * instruction.getDestination().getValue()); return; } if
	 * (instruction.getOperation().equals(Consts.LOAD)) {
	 * instruction.setDestination(Simulator.getValueFromMemory(instruction.
	 * getMemoryAddress())); }
	 * 
	 * reorderBuffer.get(instruction.getDestinationRobIndex()).setResult(
	 * instruction.getDestination().getValue());
	 * reorderBuffer.get(instruction.getDestinationRobIndex()).setStatus(true);
	 * 
	 * physicalRegFile[instruction.getDestinationRobIndex()] =
	 * instruction.getDestination().getValue(); }
	 * 
	 * // Move head to next index and update ROB count, If instruction is in
	 * Head // of the ROB and destination operand is valid private static
	 * boolean retireROBFields() { if (robCount > 0 &&
	 * reorderBuffer.get(head).getStatus()) {
	 * Simulator.saveValueInARF(reorderBuffer.get(head).getDestinationAddress(),
	 * physicalRegFile[head]); for (Map.Entry<String, String> entry :
	 * renameTable.entrySet()) { String[] rntValue =
	 * entry.getValue().split(" "); if (Integer.parseInt(rntValue[0]) == head) {
	 * entry.setValue(head + " " + 0); break; } } if (head < prfMAX) head++;
	 * 
	 * if (head == prfMAX) head = 0;
	 * 
	 * robCount--; return true; } return false; }
	 * 
	 * // Remove ROB entry from ROB if status bit is 1 and it is the head of ROB
	 * public static void retireROBEntry() {
	 * 
	 * while (true) { if (!retireROBFields()) break; } }
	 */
	// Returns PRF file
	public static int[] getPhysicalRegFile()
	{
		return physicalRegFile;
	}

	// Returns Rename table
	public static Map<String, String> getRenameTable()
	{
		return renameTable;
	}
	// endregion
}