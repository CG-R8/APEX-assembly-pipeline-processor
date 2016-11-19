
/**
 * This class have all the details of single instruction This class also contain
 * all get and set methods for different members of class KeyValue<String,
 * Integer> src1;
 */
public class Instruction
{
	private KeyValue<String, Integer> instrSource1;
	private KeyValue<String, Integer> instrSource2;
	private KeyValue<String, Integer> instrdestination;
	private Integer instrLiteral;
	private Integer instrMemAddress;
	private String instrContent;
	private String instrOperation;
	private boolean isInstrNOP;

	/**
	 * This function accept all the possible available values of instruction
	 * 
	 * @param operation
	 *            like ADD SUB MUL
	 * @param destination
	 *            Register : hashmap
	 * @param src1
	 *            Register : hashmap
	 * @param src2
	 *            Register : hashmap
	 * @param literal
	 *            valuse : integer
	 * @param content
	 *            : instruction as string "ADD R1 R2 R3"
	 */
	public Instruction(String operation, KeyValue<String, Integer> destination, KeyValue<String, Integer> src1,
			KeyValue<String, Integer> src2, Integer literal, String content)
	{
		this.instrSource1 = src1;
		this.instrSource2 = src2;
		this.instrdestination = destination;
		this.instrLiteral = literal;
		this.instrOperation = operation;
		this.instrContent = content;
		this.isInstrNOP = false;
	}

	/**
	 * @return instruction as string "ADD R1 R2 R3"
	 */
	public String getContent()
	{
		return instrContent;
	}

	/**
	 * @return type of operation : "ADD"
	 */
	public String getOperation()
	{
		return this.instrOperation;
	}

	/**
	 * @return first source's Register name and value : hashmap
	 */
	public KeyValue<String, Integer> getSrc1()
	{
		return this.instrSource1;
	}

	/**
	 * @return second source's Register name and value : hashmap
	 */
	public KeyValue<String, Integer> getSrc2()
	{
		return this.instrSource2;
	}

	public KeyValue<String, Integer> getDestination()
	{
		return this.instrdestination;
	}

	/**
	 * @return literal value
	 */
	public Integer getLiteral()
	{
		return this.instrLiteral;
	}

	/**
	 * @return memory address of instruction
	 */
	public Integer getMemoryAddress()
	{
		return this.instrMemAddress;
	}

	public void setSrc1(Integer value)
	{
		this.instrSource1.setValue(value);
	}

	public void setSrc2(Integer value)
	{
		this.instrSource2.setValue(value);
	}

	public void setDestination(Integer value)
	{
		this.instrdestination.setValue(value);
	}

	public void setMemoryAddress(Integer value)
	{
		this.instrMemAddress = value;
	}

	/**
	 * @return flag true : if NOP flag is set
	 */
	public boolean isNOP()
	{
		return this.isInstrNOP;
	}

	/**
	 * Create new instruction with NOP and NOP flag as true
	 */
	public Instruction()
	{
		this.instrContent = "NOP";
		this.isInstrNOP = true;
	}
}