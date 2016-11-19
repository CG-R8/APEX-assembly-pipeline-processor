
/**
 * This class have all the details of single instruction
 * This class also contain all get and set methods for different members of class
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
	/**
	 * @param operation
	 * @param destination
	 * @param src1
	 * @param src2
	 * @param literal
	 * @param content
	 */
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
	public String getContent(){
		return content;
	}	
	public String getOperation(){
		return this.operation;
	}
	public Integer getMemoryAddress(){
		return this.memAddress;
	}
	public KeyValue<String, Integer> getSrc1(){
		return this.src1;	
	}
	public KeyValue<String, Integer> getSrc2(){
		return this.src2;	
	}
	public Integer getLiteral(){
		return this.literal;
	}
	public KeyValue<String, Integer> getDestination(){
		return this.destination;	
	}
	public void setSrc1(Integer value){
		this.src1.setValue(value);
	}
	public void setSrc2(Integer value){
		this.src2.setValue(value);
	}
	public void setDestination(Integer value){
		this.destination.setValue(value);
	}
	public void setMemoryAddress(Integer value){
		this.memAddress =  value;
	}	
	public boolean isNOP(){
		return this.isNOP;
	}
}