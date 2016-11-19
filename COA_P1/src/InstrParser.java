
/**
 * @author Chetan
 *
 */
public class InstrParser
{

	/**
	 * This function takes string and return its integer value
	 * 
	 * @param any
	 *            string
	 * @return number
	 */
	private static boolean isNumeric(String str)
	{
		try
		{
			Integer.parseInt(str);
		} catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	/**
	 * This function parse every instruction
	 * <p>
	 * Creates instruction derived from Instruction class
	 * 
	 * @param instruction
	 * @param Program
	 *            Counter
	 * @return instruction
	 */
	public Instruction parseInstruction(String instr, int pcCounter)
	{
		Instruction instruction = null;
		String[] instrArray = instr.split(" ");

		if (!instr.contentEquals(" "))
		{
			switch (instrArray[0])
				{
				case TypesOfOperations.ADD:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.SUB:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.MOVC:
					if (isNumeric(instrArray[2]))
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								null, null, Integer.parseInt(instrArray[2]), instr);
					} else
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								new KeyValue<String, Integer>(instrArray[2], null), null, null, instr);
					}
					break;
				case TypesOfOperations.MUL:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.AND:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.OR:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.EXOR:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							new KeyValue<String, Integer>(instrArray[2], null),
							new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					break;
				case TypesOfOperations.LOAD:
					if (isNumeric(instrArray[3]))
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								new KeyValue<String, Integer>(instrArray[2], null), null,
								Integer.parseInt(instrArray[3]), instr);
					} else
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								new KeyValue<String, Integer>(instrArray[2], null),
								new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					}
					break;
				case TypesOfOperations.STORE:
					if (isNumeric(instrArray[3]))
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								new KeyValue<String, Integer>(instrArray[2], null), null,
								Integer.parseInt(instrArray[3]), instr);
					} else
					{
						instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
								new KeyValue<String, Integer>(instrArray[2], null),
								new KeyValue<String, Integer>(instrArray[3], null), null, instr);
					}
					break;

				case TypesOfOperations.BZ:
					instruction = new Instruction(instrArray[0], null, null, null, Integer.parseInt(instrArray[1]),
							instr);
					break;
				case TypesOfOperations.BNZ:
					instruction = new Instruction(instrArray[0], null, null, null, Integer.parseInt(instrArray[1]),
							instr);
					break;
				case TypesOfOperations.JUMP:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							null, null, Integer.parseInt(instrArray[2]), instr);
					break;
				case TypesOfOperations.BAL:
					instruction = new Instruction(instrArray[0], new KeyValue<String, Integer>(instrArray[1], null),
							null, null, Integer.parseInt(instrArray[2]), instr);
					break;
				case TypesOfOperations.HALT:
					instruction = new Instruction(instrArray[0], null, null, null, null, instr);
					break;
				default:
					instruction = new Instruction();
					break;
				}
		} else
		{
			instruction = new Instruction();
		}
		return instruction;

	}
}