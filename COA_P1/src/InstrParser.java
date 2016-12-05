
/**
 *
 */
public class InstrParser
{
	/**
	 * This function parse every instruction
	 * <p>
	 * Creates instruction derived from Instruction class
	 * 
	 * @param instruction
	 * @param Program
	 *            Counter s
	 * @return instruction
	 */
	public Instruction parseInstruction(String instr, int pcCounter)
	{
		Instruction instruction = null;
		String[] spiltedInstructionArray = instr.split(" ");
		if (!instr.contentEquals(" "))
		{
			switch (spiltedInstructionArray[0])
				{
				case TypesOfOperations.ADD:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.SUB:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.MOVC:
					if (isNumber(spiltedInstructionArray[2]))
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								null, null, Integer.parseInt(spiltedInstructionArray[2]), instr);
					} else
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null), null, null, instr);
					}
					break;
				case TypesOfOperations.MUL:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.AND:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.OR:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.EXOR:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
							new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					break;
				case TypesOfOperations.LOAD:
					if (isNumber(spiltedInstructionArray[3]))
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null), null,
								Integer.parseInt(spiltedInstructionArray[3]), instr);
					} else
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					}
					break;
				case TypesOfOperations.STORE:
					if (isNumber(spiltedInstructionArray[3]))
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null), null,
								Integer.parseInt(spiltedInstructionArray[3]), instr);
					} else
					{
						instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[2], null),
								new RegisgerName_Value<String, Integer>(spiltedInstructionArray[3], null), null, instr);
					}
					break;
				case TypesOfOperations.BZ:
					instruction = new Instruction(spiltedInstructionArray[0], null, null, null, Integer.parseInt(spiltedInstructionArray[1]),
							instr);
					break;
				case TypesOfOperations.BNZ:
					instruction = new Instruction(spiltedInstructionArray[0], null, null, null, Integer.parseInt(spiltedInstructionArray[1]),
							instr);
					break;
				case TypesOfOperations.JUMP:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							null, null, Integer.parseInt(spiltedInstructionArray[2]), instr);
					break;
				case TypesOfOperations.BAL:
					instruction = new Instruction(spiltedInstructionArray[0], new RegisgerName_Value<String, Integer>(spiltedInstructionArray[1], null),
							null, null, Integer.parseInt(spiltedInstructionArray[2]), instr);
					break;
				case TypesOfOperations.HALT:
					instruction = new Instruction(spiltedInstructionArray[0], null, null, null, null, instr);
					break;
				default:
					instruction = new Instruction();
					break;
				}
		} else
		{
			System.out.println("No incoming instruction, Adding NOP");
			instruction = new Instruction();
		}
		return instruction;

	}
	
	
	/**
	 * This function takes string and return its integer value
	 * 
	 * @param anystring
	 * @return number
	 */
	private static boolean isNumber(String str)
	{
		try
		{
			Integer.parseInt(str);
		} catch (NumberFormatException ex)
		{
			return false;
		}
		return true;
	}
}