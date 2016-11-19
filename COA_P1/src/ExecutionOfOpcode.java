
public class ExecutionOfOpcode
{

	// In EX stage: Perform Arithmetic operation on input operands and store in
	// destination field of Instruction Object
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
			// Move literal/ Source value into destination field
			case TypesOfOperations.MOV:
			case TypesOfOperations.MOVC:
				if (instruction.getSrc1() != null)
					instruction.setDestination(instruction.getSrc1().getValue());
				else
					instruction.setDestination(instruction.getLiteral());
				break;

			// Memory Instruction(Load from memory/ Store in memory)
			case TypesOfOperations.LOAD:
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
	 * @param instruction
	 * @param pDestination
	 * @param currentPC
	 * @param registerValue
	 * @param specialRegister
	 * @return
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