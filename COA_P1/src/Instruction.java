
/**
 * This class have all the details of single instruction This class also contain
 * all get and set methods for different members of class KeyValue<String,
 * Integer> src1;
 */
public class Instruction
{
	private String instrOperation;
	private RegisgerName_Value<String, Integer> instrdestination;
	private RegisgerName_Value<String, Integer> instrSource1;
	private RegisgerName_Value<String, Integer> instrSource2;
	private Integer instrLiteral;
	private Integer instrMemAddress;
	private String instrContent;
	private boolean isInstrNOP;

	public Instruction()
	{
		this.instrContent = "NOP";
		this.isInstrNOP = true;
	}

	/**
	 * @param operation
	 * @param destination
	 * @param src1
	 * @param src2
	 * @param literal
	 * @param content
	 */
	public Instruction(String operation, RegisgerName_Value<String, Integer> destination, RegisgerName_Value<String, Integer> src1,
			RegisgerName_Value<String, Integer> src2, Integer literal, String content)
	{
		this.instrOperation = operation;
		this.instrdestination = destination;
		this.instrSource1 = src1;
		this.instrSource2 = src2;
		this.instrLiteral = literal;
		this.instrContent = content;
		this.isInstrNOP = false;
	}

	public String getContent()
	{
		return instrContent;
	}

	public String getOperation()
	{
		return this.instrOperation;
	}

	public Integer getMemoryAddress()
	{
		return this.instrMemAddress;
	}

	public RegisgerName_Value<String, Integer> getSrc1()
	{
		return this.instrSource1;
	}

	public RegisgerName_Value<String, Integer> getSrc2()
	{
		return this.instrSource2;
	}

	public Integer getLiteral()
	{
		return this.instrLiteral;
	}

	public RegisgerName_Value<String, Integer> getDestination()
	{
		return this.instrdestination;
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

	public boolean isNOP()
	{
		return this.isInstrNOP;
	}
}