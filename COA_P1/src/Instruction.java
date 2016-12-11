import java.util.Map;

/**
 * This class have all the details of single instruction This class also contain
 * all get and set methods for different members of class KeyValue<String,
 * Integer> src1;
 */
public class Instruction
{
	private int instructionAddress;
	private String instrOperation;
	private RegisgerName_Value<String, Integer> instrdestination;
	private RegisgerName_Value<String, Integer> instrSource1;
	private RegisgerName_Value<String, Integer> instrSource2;
	private Integer instrLiteral;
	private Integer instrMemAddress;
	private String instrContent;
	private boolean isInstrNOP;

	private boolean sourceValid;

	private RegisgerName_Value<String, Integer> physicalDestination;
	private RegisgerName_Value<String, Integer> physicalSource1;
	private RegisgerName_Value<String, Integer> physicalSource2;

	public boolean justAddedToQ;

	private boolean src1Valid;
	private boolean src2Valid;
	private boolean qDispatchable;

	private int destinationRobIndex;
	private int source1RobIndex;
	private int source2RobIndex;
	private int renamedSlot;

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
	public Instruction(String operation, RegisgerName_Value<String, Integer> destination, RegisgerName_Value<String, Integer> src1, RegisgerName_Value<String, Integer> src2, Integer literal, String content)
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

	// Returns true if source 1 and 2 are valid
	public boolean isSourceValid()
	{
		return this.sourceValid;
	}

	// Set if source 1 and 2 are valid
	public void isSourceValid(boolean value)
	{
		sourceValid = value;
	}

	public int getInstructionAddress()
	{
		// TODO Auto-generated method stub
		return instructionAddress;
	}

	public RegisgerName_Value<String, Integer> getPhysicalDestination()
	{
		return physicalDestination;
	}

	public void setPhysicalDestination(RegisgerName_Value<String, Integer> physicalDestination)
	{
		this.physicalDestination = physicalDestination;
	}

	public RegisgerName_Value<String, Integer> getPhysicalSource1()
	{
		return physicalSource1;
	}

	public void setPhysicalSource1(RegisgerName_Value<String, Integer> physicalSource1)
	{
		this.physicalSource1 = physicalSource1;
	}

	public RegisgerName_Value<String, Integer> getPhysicalSource2()
	{
		return physicalSource2;
	}

	public void setPhysicalSource2(RegisgerName_Value<String, Integer> physicalSource2)
	{
		this.physicalSource2 = physicalSource2;
	}

	public void setPhysicalDestination(int i)
	{

		this.physicalDestination = new RegisgerName_Value<String, Integer>(this.getPhysicalDestination().getKey(), i);
	}

	public boolean isJustAddedToQ()
	{
		return justAddedToQ;
	}

	public void setJustAddedToQ(boolean justAddedToQ)
	{
		this.justAddedToQ = justAddedToQ;
	}

	public boolean isSrc1Valid()
	{
		return src1Valid;
	}

	public void setSrc1Valid(boolean src1Valid)
	{
		this.src1Valid = src1Valid;
	}

	public boolean isSrc2Valid()
	{
		return src2Valid;
	}

	public void setSrc2Valid(boolean src2Valid)
	{
		this.src2Valid = src2Valid;
	}

	public boolean isqDispatchable()
	{
		return qDispatchable;
	}

	public void setqDispatchable(boolean qDispatchable)
	{
		this.qDispatchable = qDispatchable;
	}

}