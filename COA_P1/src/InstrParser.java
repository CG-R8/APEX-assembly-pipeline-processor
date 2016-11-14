
public class InstrParser {

	// Check if it contains literal value field
	private static boolean isNumeric(String str)  
	{  
		try{  
			Integer.parseInt(str);  
		}  
		catch(NumberFormatException nfe){  
			return false;  
		}  
		return true;  
	}

	// Parse instruction and return Instruction Object to be passed into different stages
	public Instruction parseInstruction(String instr, int pcCounter){
		Instruction instruction = null;
		String [] instrArray = instr.split(" ");
		switch(instrArray[0]){
		case TypesOfOperations.ADD: //ADD R2 R2 R5
		case TypesOfOperations.SUB:
		case TypesOfOperations.MUL: 
		case TypesOfOperations.AND:
		case TypesOfOperations.OR:
		case TypesOfOperations.EXOR:
			instruction = new Instruction(instrArray[0], 
					new KeyValue<String, Integer>(instrArray[1], null), 
					new KeyValue<String, Integer>(instrArray[2], null), 
					new KeyValue<String, Integer>(instrArray[3], null), null, instr);
			break;			
		case TypesOfOperations.LOAD:  //LOAD R5 R2 32	
		case TypesOfOperations.STORE: //STORE R4 R3 20 & STORE R4 R5 R6
			if(isNumeric(instrArray[3])){
				instruction = new Instruction(instrArray[0], 
						new KeyValue<String, Integer>(instrArray[1], null), 
						new KeyValue<String, Integer>(instrArray[2], null), 
						null, Integer.parseInt(instrArray[3]), instr);
			}
			else{
				instruction = new Instruction(instrArray[0], 
						new KeyValue<String, Integer>(instrArray[1], null), 
						new KeyValue<String, Integer>(instrArray[2], null), 
						new KeyValue<String, Integer>(instrArray[3], null), null, instr);
			}
			break;	
		case TypesOfOperations.MOVC: //MOVC R1 2
		case TypesOfOperations.MOV: //MOV R2 R1
			if(isNumeric(instrArray[2])){
				instruction = new Instruction(instrArray[0], 
						new KeyValue<String, Integer>(instrArray[1], null), 
						null, null, Integer.parseInt(instrArray[2]), instr);
			}
			else{
				instruction = new Instruction(instrArray[0], 
						new KeyValue<String, Integer>(instrArray[1], null), 
						new KeyValue<String, Integer>(instrArray[2], null), null, null, instr);
			}
			break;	
		case TypesOfOperations.BZ: //BZ 4
		case TypesOfOperations.BNZ: //BNZ -8
			instruction = new Instruction(instrArray[0], null, null, null, 
					Integer.parseInt(instrArray[1]), instr);
			break;	
		case TypesOfOperations.JUMP: //JUMP R1 20075	&   JUMP X 0
		case TypesOfOperations.BAL:  //BAL R7 2
			instruction = new Instruction(instrArray[0], 
					new KeyValue<String, Integer>(instrArray[1], null), null, null, 
					Integer.parseInt(instrArray[2]), instr);
			break;
		case TypesOfOperations.HALT: //HALT
			instruction = new Instruction(instrArray[0], null, null, null, null, instr);
			break;
		default:instruction = new Instruction();
		break;
		}
		return instruction;
	}
}