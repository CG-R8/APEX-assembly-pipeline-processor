
public class ExecutionOfOpcode
{

	/**
	 * This method checks the control flow instruction and calculate the program
	 * counter on the basis of current PC and literal value
	 * <p>
	 * Special case for "X" register which sets when "BAL" instruction executed
	 * 
	 * @param branchInstruction
	 * @param pastDestination
	 *            previous destination
	 * @param currentPC
	 *            program counter
	 * @param registerValue
	 * @param xRegister
	 * @return calculated program counter by adding literal values
	 */
	public Integer evaluateBranchInstr(Instruction branchInstruction, Integer pastDestination, Integer currentPC,
			Integer registerValue, Integer xRegister)
	{
		Integer branchLiteral = branchInstruction.getLiteral();
		switch (branchInstruction.getOperation())
			{
			case TypesOfOperations.BAL:
				currentPC = registerValue + branchInstruction.getDestination().getValue();
				break;
			case TypesOfOperations.JUMP:
				if (!branchInstruction.getDestination().getKey().equals("X"))
					currentPC = registerValue + branchLiteral - 8;
				else
					currentPC = xRegister + branchLiteral;
				break;
			case TypesOfOperations.BZ:
				if (pastDestination == 0)
					currentPC = currentPC + branchLiteral - 8;
				break;
			case TypesOfOperations.BNZ:
				if (pastDestination != 0)
					currentPC = currentPC + branchLiteral - 8;
				break;

			}
		return currentPC;
	}

	/**
	 * Execute current instructions operation by get method and arithmetic
	 * operators. and sets the value in current instructions destination using
	 * set method.
	 * <p>
	 * Also perform addition for LOAD and STORE memory addresses
	 * 
	 * @param instruction
	 * @return instruction with calculated result
	 */
	public Instruction executeInstruction(Instruction instruction)
	{
		switch (instruction.getOperation())
			{
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

}