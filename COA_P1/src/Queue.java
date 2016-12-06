
import java.util.ArrayList;

public class Queue {

	private static int iQMAX = 8;
	private static int lSQMAX = 4;
	private static ArrayList<Instruction> isssueQueue = new ArrayList<Instruction>(iQMAX);
	private static ArrayList<Instruction> loadStoreQueue = new ArrayList<Instruction>(lSQMAX);

	// Check if Issue queue is available (Issue can contain up to 8 instructions)
	private static boolean isIQAvailable() {
		return isssueQueue.size() < iQMAX;
	}

	// Check if Load Store queue is available (Load Store queue can contain up to 4 instructions)
	private static boolean isLSQAvailable() {
		return loadStoreQueue.size() < lSQMAX;
	}

	// Move instruction from issue queue to VFU if source operands are validated(valid bit = 1) or 
	//if there is a valid source in physical register file
	public static Instruction pullIQInstruction(String fuType) {
		Instruction instruction = new Instruction();
		for (Instruction instr : isssueQueue) {
			if (instr.getOperation().equals(fuType)) {
				
//				if (!instr.isSourceValid())
//					instr = Rename.readSourceOperandsFromPRF(instr);

				if (instr.isSourceValid()) {
					instruction = instr;
					break;
				}
			}
		}
		if (!instruction.isNOP())
			isssueQueue.remove(instruction);

		return instruction;
	}

	// Move instruction from load store queue to VFU if source operands are validated(valid bit = 1) or 
	//if there is a valid source in physical register file
	public static Instruction pullLSQInstruction() {
		Instruction instruction = new Instruction();
		if (loadStoreQueue.size() > 0) {
			instruction = loadStoreQueue.get(0);

//			if (!instruction.isSourceValid())
//				instruction = Rename.readSourceOperandsFromPRF(instruction);

			if (instruction.isSourceValid()) {
				loadStoreQueue.remove(instruction);
			} else {
				instruction = new Instruction();
			}
		}
		return instruction;
	}

	// Add instruction in Issue Queue / Load Store Queue based on Instruction operation type
	public static boolean addToQueue(Instruction instruction) {
		boolean addedToQueue = false;
		// Read operands from Physical register file
//		if (!instruction.isSourceValid()) {
//			instruction = Rename.readSourceOperandsFromPRF(instruction);
//		}
//		if (instruction.getOperation().equals(Consts.MEMORY)) {
			
			System.out.println("In the add Q function"+ " "+instruction.getContent());
			if (instruction.getOperation().equals("LOAD")) {

			if (isLSQAvailable()) {
				// Check if there is empty slot in LSQ
				loadStoreQueue.add(instruction);
				addedToQueue = true;
			}

		} else {
			if (isIQAvailable()) {
				// Check if there is empty slot in IQ
				isssueQueue.add(instruction);
				addedToQueue = true;
			}
		}
		return addedToQueue;
	}

	//Return Issue Queue
	public static ArrayList<Instruction> retrieveIsssueQueue(){
		return isssueQueue;
	}
	
	//Return Load Store Queue
	public static ArrayList<Instruction> retrieveLoadStoreQueue(){
		return loadStoreQueue;
	}
}