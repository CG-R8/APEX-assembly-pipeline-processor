
/**
 * @author Chetan
 *KeyValue<String, Integer> src1;
 */
public class Instruction {
	private String operation;
	private KeyValue<String, Integer> destination;
	private KeyValue<String, Integer> src1;
	private KeyValue<String, Integer> src2;
	private Integer literal;
	private Integer memAddress;
	private String content;
	private boolean isNOP;

	public Instruction(){
		this.content = "NOP";
		this.isNOP = true;
	}

	public Instruction(String operation,
			KeyValue<String, Integer> destination,
			KeyValue<String, Integer> src1, KeyValue<String, Integer> src2,
			Integer literal, String content) {
		this.operation = operation;
		this.destination = destination;
		this.src1 = src1;
		this.src2 = src2;
		this.literal = literal;
		this.content = content;
		this.isNOP = false;
	}

	// Returns Instruction's Operation type (ADD/SUB/MUL/AND/OR/EXOR/MOV/LOAD/STORE .. etc)
	public String getOperation(){
		return this.operation;
	}

	// Set value in Instruction's Destination field
	public void setDestination(Integer value){
		this.destination.setValue(value);
	}

	// Returns Instruction's Destination <key,value> field (Example: <R1, 100>)
	public KeyValue<String, Integer> getDestination(){
		return this.destination;	
	}	

	// Set value in Instruction's Source1 field
	public void setSrc1(Integer value){
		this.src1.setValue(value);
	}

	// Returns Instruction's Source1 <key,value> field (Example: <R1, 100>)
	public KeyValue<String, Integer> getSrc1(){
		return this.src1;	
	}

	// Set value in Instruction's Source2 field
	public void setSrc2(Integer value){
		this.src2.setValue(value);
	}

	// Returns Instruction's Source2 <key,value> field (Example: <R1, 100>)
	public KeyValue<String, Integer> getSrc2(){
		return this.src2;	
	}

	// Returns Instruction's literal value
	public Integer getLiteral(){
		return this.literal;
	}

	// Sets memory address for Store Instruction
	public void setMemoryAddress(Integer value){
		this.memAddress =  value;
	}

	// Returns memory address of Instruction if any
	public Integer getMemoryAddress(){
		return this.memAddress;
	}
	
	// Returns NOP (in case of halt instruction)
	public boolean isNOP(){
		return this.isNOP;
	}

	//Returns Instruction string(Example: MOVC R0 1)
	public String getContent(){
		return content;
	}
}