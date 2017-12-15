package DBCommands;

public class Parser {
	
	private String query;
	
	public Parser(String query){
		this.query = query.replaceAll(";", "").replaceAll("\n", "").toLowerCase();
	}
	
	public String getCommand(){
		return query.split(" ")[0];
	}
	
	public boolean isTable(){
		if(query.split(" ")[1].equals("table")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getName(){
		return query.split(" ")[2].split("\\(")[0];
	}
	
	public String[][] getTableContent(){
		String tableContent = query.split("\\(")[1];
		tableContent = tableContent.replaceAll("\\)", "");
		while(tableContent.charAt(0) == ' '){
			tableContent = tableContent.replaceFirst(" ", "");
		}
		String[][] tempTable = new String[100][2];
		int i = 0;
		while(true){
			try{
				String temp = tableContent.split(",")[i];
				while(temp.charAt(0) == ' '){
					temp = temp.replaceFirst(" ", "");
				}
				tempTable[i][0] = temp.split(" ")[0];
				tempTable[i][1] = temp.split(" ")[1];
				i++;
			}
			catch(Exception e){
				break;
			}		
		}
		String[][] table = new String[i][2];
		int j = 0;
		while(i > 0){
			table[j][0] = tempTable[j][0];
			table[j][1] = tempTable[j][1];
			j++;
			i--;
		}
		return table;
	}
	
	public String getTableName(){
		if(query.contains("update")) {
			return query.replaceAll(" ", "").split("update")[1].split("set")[0];
		} else if(query.contains("where")) {
			return query.replaceAll(" ", "").split("where")[0].split("from")[1];
		}
		else if(query.contains("from")){
			return query.replaceAll(" ", "").split("from")[1];
		}
		else if(query.contains("insert")){
			if(query.split("values")[0].contains("(")) {
				return query.replaceAll(" ", "").split("\\(")[0].split("into")[1];
			} else {
				return query.replaceAll(" ", "").split("into")[1].split("values")[0];
			}
		}
		return null;
	}
	
	public String getColoumnNames(){
		return query.replaceAll(" ", "").split("from")[0].split("select")[1];
	}
	
	public String getCondition(){
		return query.replaceAll(" ", "").split("where")[1];
	}
	
	public String getCreateTableQuery(){
		return query.split("as")[1].replaceFirst(" ", "");
	}
	
	public String[][] getUpdates(){
		String tempUpdates;
		if(!query.contains("where")){
			tempUpdates = query.split("set")[1].replaceAll(" ", "");
		}
		else{
			tempUpdates = query.split("where")[0].split("set")[1].replaceAll(" ", "");
		}
		String[][] tempUpdate = new String[100][2];
		int i = 0;
		while(true){
			try{
				String temp = tempUpdates.split(",")[i];
				tempUpdate[i][0] = temp.split("=")[0];
				tempUpdate[i][1] = temp.split("=")[1];
				i++;
			}
			catch(Exception e){
				break;
			}		
		}
		String[][] updates = new String[i][2];
		int j = 0;
		while(i > 0){
			updates[j][0] = tempUpdate[j][0];
			updates[j][1] = tempUpdate[j][1];
			j++;
			i--;
		}
		return updates;
	}
	
	public boolean noColNames(){
		String tableName = this.getTableName();
		if(query.split(tableName)[1].split("values")[0].equals(" ")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String[] getValues(){
		String tempValues = query.split("values")[1].replaceAll("\\(", "").replaceAll(" ", "").replaceAll("\\)", "");
		int i = 0;
		String[] valArr = new String[100];
		while(true){
			try{
				valArr[i] = tempValues.split(",")[i];
				i++;
			}
			catch(Exception e){
				break;
			}
		}
		String[] values = new String[i];
		int j = 0;
		while(i > 0){
			values[j] = valArr[j];
			j++;
			i--;
		}
		return values;
	}
	
	public String[][] getColAndValues(){
		String tableName = this.getTableName();
		String tempCol = query.split(tableName)[1].split("values")[0].replaceAll("\\(", "").replaceAll(" ", "").replaceAll("\\)", "");
		String tempValues = query.split("values")[1].replaceAll("\\(", "").replaceAll(" ", "").replaceAll("\\)", "");
		int i = 0;
		String[] colArr = new String[100];
		String[] valArr = new String[100];
		while(true){
			try{
				colArr[i] = tempCol.split(",")[i];
				valArr[i] = tempValues.split(",")[i];
				i++;
			}
			catch(Exception e){
				break;
			}
		}
		String[][] colVal = new String[i][2];
		int j = 0;
		while(i > 0){
			colVal[j][0] = colArr[j];
			colVal[j][1] = valArr[j];
			j++;
			i--;
		}
		return colVal;
	}

	public String getDatabaseName() {
		return query.split("database")[1].replaceAll(" ", "");
	}
}
