
public class ExecutionOfOpcode
{

	/**
	 * Execute current instructions operation by get method and arithematic
	 * operators. ans sets the value in current instructions destination using
	 * set method
	 * 
	 * @param instruction
	 * @return
	 */
	public Instruction executeInstruction(Instruction instruction)
	{

		switch (instruction.getOperation())
			{
			// Register to Register Instruction (Source1 and Source2)
			case TypesOfOperations.ADD:
				instruction.setDestination(instruction.getSrc1().getValue() + instruction.getSrc2().getValue());
				break;
			case TypesOfOperations.SUB:
				instruction.setDestination(instruction.getSrc1().getValue() - instruction.getSrc2().getValue());
				break;
			case TypesOfOperations.MOVC:
				if (instruction.getSrc1() != null)
					instruction.setDestination(instruction.getSrc1().getValue());
				else
					instruction.setDestination(instruction.getLiteral());
				break;
			case TypesOfOperations.MUL:
				instruction.setDestination(instruction.getSrc1().getValue() * instruction.getSrc2().getValue());
				break;
			case TypesOfOperations.AND:
				instruction.setDestination(instruction.getSrc1().getValue() & instruction.getSrc2().getValue());
				break;
			case TypesOfOperations.OR:
				instruction.setDestination(instruction.getSrc1().getValue() | instruction.getSrc2().getValue());
				break;
			case TypesOfOperations.EXOR:
				instruction.setDestination(instruction.getSrc1().getValue() ^ instruction.getSrc2().getValue());
				break;

			case TypesOfOperations.LOAD:
				if (instruction.getSrc2() != null)
					instruction.setMemoryAddress(instruction.getSrc1().getValue() + instruction.getSrc2().getValue());
				else
					instruction.setMemoryAddress(instruction.getSrc1().getValue() + instruction.getLiteral());
				break;
			case TypesOfOperations.STORE:
				if (instruction.getSrc2() != null)
					instruction.setMemoryAddress(instruction.getSrc1().getValue() + instruction.getSrc2().getValue());
				else
					instruction.setMemoryAddress(instruction.getSrc1().getValue() + instruction.getLiteral());
				break;
			}
		return instruction;
	}

	/**
	 * This method checks the control flow instruction ans 
	 * calculate the program counter on the basis of current PC and literal value
	 * 
	 * @param instruction
	 * @param pDestination
	 *            previous destination
	 * @param currentPC
	 *            program counter
	 * @param registerValue
	 * @param specialRegister
	 * @return calculated program counter by adding literal values 
	 */
	public Integer predictBranch(Instruction instruction, Integer pDestination, Integer currentPC,
			Integer registerValue, Integer specialRegister)
	{
		switch (instruction.getOperation())
			{
			// Control Flow Instruction(Conditional/ unconditional jump
			// instruction)
			case TypesOfOperations.BNZ:
				if (pDestination != 0)
					currentPC = currentPC + instruction.getLiteral() - 8; // Relative
																			// address
				break;
			case TypesOfOperations.BZ:
				if (pDestination == 0)
					currentPC = currentPC + instruction.getLiteral() - 8; // Relative
																			// address
				break;
			case TypesOfOperations.JUMP:
				// X is Special register used to store next PC address in case
				// of BAL instruction
				if (!instruction.getDestination().getKey().equals("X"))
					currentPC = registerValue + instruction.getLiteral() - 8;
				else
					currentPC = specialRegister + instruction.getLiteral();
				break;
			case TypesOfOperations.BAL:

				currentPC = registerValue + instruction.getDestination().getValue();
				break;
			}
		return currentPC;
	}
}