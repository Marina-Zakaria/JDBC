package DBCommands;

public class Condition {

	private char operator;
	private String colName;
	private String operand;

	/**
	 * @return the operator
	 */
	public char getOperator() {
		return operator;
	}

	/**
	 * @return the colName
	 */
	public String getColName() {
		return colName;
	}

	/**
	 * @return the operand
	 */
	public String getOperand() {
		return operand;
	}

	public Condition(String condition) {
		super();
		if (condition.contains("=")) {
			operator = '=';
		} else if (condition.contains(">")) {
			operator = '>';
		} else if (condition.contains("<")) {
			operator = '<';
		}

		String operatorString = "" + operator;
		colName = condition.split(operatorString)[0];
		operand = condition.split(operatorString)[1];

	}
}
