package DBCommands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DB {

	private String databaseName;
	private final String[] XSDSyntax = {"<?xml version = \"1.0\"?>\n\n<xs:schema xmlns:xs = \"http://www.w3.org/2001/XMLSchema\">\n\t<xs:element name = '",
			"'>\n\t\t<xs:complexType>\n\t\t\t<xs:sequence>\n\t\t\t\t<xs:element name = 'item' type = 'ItemType' minOccurs = '0'\n\t\t\t\t\tmaxOccurs = 'unbounded' />\n\t\t\t</xs:sequence>\n\t\t</xs:complexType>\n\t</xs:element>\n\n\t<xs:complexType name = \"ItemType\">\n\t\t<xs:sequence>\n",
			"\t\t\t<xs:element name = \"", "\" type = \"xs:", "\"/>\n", "\t\t</xs:sequence>\n\t</xs:complexType>\n</xs:schema>"};
	
	public DB(String databaseName) {
		this.databaseName = databaseName;
		File databasesFolder = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "Database");
		if(!databasesFolder.exists()) databasesFolder.mkdir();
	}

	public void createDatabase() {
		File databaseFolder = new File(getPath(null));
		databaseFolder.mkdir();
	}

	public String getPath(String tableName) {
		StringBuffer path = new StringBuffer();
		path.append(System.getProperty("user.dir"));
		path.append(System.getProperty("file.separator") + "Database");
		path.append(System.getProperty("file.separator") + databaseName);
		if(tableName != null) {
			path.append(System.getProperty("file.separator") + tableName);
		}
		return path.toString();
	}

	public void dropDatabase() {
		File databaseFolder = new File(getPath(null));
		File[] filesInDBFolder = databaseFolder.listFiles();
		for(int i = 0; i < filesInDBFolder.length; i++) filesInDBFolder[i].delete();
		databaseFolder.delete();
	}
	
	public boolean exists() {
		File databaseFolder = new File(getPath(null));
		return databaseFolder.exists();
	}

	public void createTable(String tableName, String[][] colNamesAndTypes) {
		try {
			writeTableSchema(tableName, colNamesAndTypes);
			writeEmptyTable(tableName);
		} catch (IOException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
	}

	private void writeTableSchema(String tableName, String[][] colNamesAndTypes) throws IOException {
		File xsd = new File(getPath(tableName) + ".xsd");
		xsd.createNewFile();
		Writer writer = new OutputStreamWriter(new FileOutputStream(xsd), "ISO-8859-1");
		writer.write(XSDSyntax[0]);
		writer.write(tableName);
		writer.write(XSDSyntax[1]);
		for(int i = 0; i < colNamesAndTypes.length; i++) {
			writer.write(XSDSyntax[2]);
			writer.write(colNamesAndTypes[i][0].toLowerCase());
			writer.write(XSDSyntax[3]);
			writer.write(colNamesAndTypes[i][1]);
			writer.write(XSDSyntax[4]);
		}
		writer.write(XSDSyntax[5]);
		writer.close();
	}

	private void writeEmptyTable(String tableName) throws IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		File xml = new File(getPath(tableName) + ".xml");
		xml.createNewFile();
		Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element rootElement = xmlDoc.createElement(tableName);
		xmlDoc.appendChild(rootElement);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(xmlDoc);
		StreamResult result = new StreamResult(xml);
		transformer.transform(source, result);
	}

	public void dropTable(String tableName) {
		File tableXML = new File(getPath(tableName) + ".xml");
		tableXML.delete();
		
		File tableXSD = new File(getPath(tableName) + ".xsd");
		tableXSD.delete();
	}

	public void insertIntoTable(String tableName, String[][] colNamesAndValues) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			Element itemElement = xmlDoc.createElement("item");
			String[] colNames = getColumnsNames(tableName);
			for(int i = 0; i < colNames.length; i++) {
				Element value = xmlDoc.createElement(colNames[i]);
				for(int j = 0; j < colNamesAndValues.length; j++) {
					if(colNames[i].equals(colNamesAndValues[j][0])) {
						value.setTextContent(colNamesAndValues[j][1]);
						break;
					}
				}
				itemElement.appendChild(value);
			}
			root.appendChild(itemElement);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(xmlDoc);
			StreamResult result = new StreamResult(tableXML);
			transformer.transform(source, result);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void insertIntoTableWithoutColumns(String tableName, String[] values) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			Element itemElement = xmlDoc.createElement("item");
			String[] colNames = getColumnsNames(tableName);
			for(int i = 0; i < colNames.length; i++) {
				Element value = xmlDoc.createElement(colNames[i]);
				value.setTextContent(values[i]);
				itemElement.appendChild(value);
			}
			root.appendChild(itemElement);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(xmlDoc);
			StreamResult result = new StreamResult(tableXML);
			transformer.transform(source, result);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
	}

	public String[] getColumnsNames(String tableName) throws SAXException, IOException, ParserConfigurationException {
		File tableXSD = new File(getPath(tableName) + ".xsd");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXSD);
        doc.getDocumentElement().normalize();
        Element complexType = (Element) doc.getElementsByTagName("xs:complexType").item(1);
        Element sequence = (Element) complexType.getElementsByTagName("xs:sequence").item(0);
        NodeList elements = sequence.getElementsByTagName("xs:element");
        String[] colNames = new String[elements.getLength()];
        for(int i = 0; i < elements.getLength(); i++) {
        	Element column = (Element) elements.item(i);
        	colNames[i] = column.getAttribute("name");
        }
		return colNames;
	}

	public Object[][] selectFromTable(String tableName, String[] colNames) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			Object[][] selected = new Object[allItems.getLength()][colNames.length];
			for (int i = 0; i < allItems.getLength(); i++) {
				Node column = allItems.item(i);
				if (column.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) column;
					for (int j = 0; j < colNames.length; j++) {
						if (isInt(e.getElementsByTagName(colNames[j]).item(0).getTextContent())) {
							selected[i][j] = Integer
									.valueOf(e.getElementsByTagName(colNames[j]).item(0).getTextContent());
						} else {
							selected[i][j] = e.getElementsByTagName(colNames[j]).item(0).getTextContent();

						}
					}
				}
			}
			return selected;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[][] selectAllFromTable(String tableName) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			NodeList allColumns = allItems.item(0).getChildNodes();
			Object[][] selected = new Object[allItems.getLength()][allColumns.getLength()];
			String[] colTypes = getColumnsTypes(tableName);
			for (int i = 0; i < allItems.getLength(); i++) {
				Node row = allItems.item(i);
				NodeList values = row.getChildNodes();
				if (row.getNodeType() == Node.ELEMENT_NODE) {
					for (int j = 0; j < allColumns.getLength(); j++) {
						if(colTypes[j].equals("int")) {
							selected[i][j] = Integer.valueOf(values.item(j).getTextContent());
						} else {
							selected[i][j] = values.item(j).getTextContent();

						}
					}
				}
			}
			return selected;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[][] selectFromTableWithCondition(String tableName, String[] colNames, Condition condition) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			Object[][] selectedWithNull = new Object[allItems.getLength()][colNames.length];
			String[] colTypes = getColumnsTypes(tableName);
			int selectedRowsNumber = 0;
			for (int i = 0; i < allItems.getLength(); i++) {
				Node column = allItems.item(i);
				if (column.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) column;
					boolean satisfyCondition = false;
					String valueTested = e.getElementsByTagName(condition.getColName()).item(0).getTextContent();
					boolean isInt = isInt(valueTested);
					if (condition.getOperator() == '=') {
						satisfyCondition = valueTested.equals(condition.getOperand());
					} else if (condition.getOperator() == '>' && isInt) {
						satisfyCondition = Integer.valueOf(valueTested) > Integer.valueOf(condition.getOperand());

					} else if (condition.getOperator() == '<' && isInt && isInt(condition.getOperand())) {
						satisfyCondition = Integer.valueOf(valueTested) < Integer.valueOf(condition.getOperand());

					} else {
						System.out.println("type mismatch");
					}
					if (satisfyCondition) {
						for (int j = 0; j < colNames.length; j++) {
							if (colTypes[j].equals("int")) {
								selectedWithNull[i][j] = Integer
										.valueOf(e.getElementsByTagName(colNames[j]).item(0).getTextContent());
							} else {
								selectedWithNull[i][j] = e.getElementsByTagName(colNames[j]).item(0).getTextContent();
							}
							selectedRowsNumber++;
						}
					}
				}
			}
			
			Object[][] selected = new Object[selectedRowsNumber][colNames.length];
			int k = 0;
			for(int i = 0; i < selectedWithNull.length; i++) {
				if(selectedWithNull[i][0] != null) {
					for(int j = 0; j < colNames.length; j++) {
						selected[k][j] = selectedWithNull[i][j];
					}
					k++;
				}
			}
			return selected;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String[] getColumnsTypes(String tableName) throws SAXException, IOException, ParserConfigurationException {
		File tableXSD = new File(getPath(tableName) + ".xsd");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXSD);
        doc.getDocumentElement().normalize();
        Element complexType = (Element) doc.getElementsByTagName("xs:complexType").item(1);
        Element sequence = (Element) complexType.getElementsByTagName("xs:sequence").item(0);
        NodeList elements = sequence.getElementsByTagName("xs:element");
        String[] colNames = new String[elements.getLength()];
        for(int i = 0; i < elements.getLength(); i++) {
        	Element column = (Element) elements.item(i);
        	colNames[i] = column.getAttribute("type");
        }
		return colNames;
	}

	public Object[][] selectAllFromTableWithCondition(String tableName, Condition condition) {
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			NodeList allColumns = allItems.item(0).getChildNodes();
			Object[][] selectedWithNull = new Object[allItems.getLength()][allColumns.getLength()];
			int selectedRowsNumber = 0;
			for (int i = 0; i < allItems.getLength(); i++) {
				Node column = allItems.item(i);
				if (column.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) column;
					boolean satisfyCondition = false;
					String valueTested = e.getElementsByTagName(condition.getColName()).item(0).getTextContent();
					boolean isInt = isInt(valueTested);
					if (condition.getOperator() == '=') {
						satisfyCondition = valueTested.equals(condition.getOperand());
					} else if (condition.getOperator() == '>' && isInt) {
						satisfyCondition = Integer.valueOf(valueTested) > Integer.valueOf(condition.getOperand());

					} else if (condition.getOperator() == '<' && isInt && isInt(condition.getOperand())) {
						satisfyCondition = Integer.valueOf(valueTested) < Integer.valueOf(condition.getOperand());

					} else {
					}
					if (satisfyCondition) {
						selectedRowsNumber++;
						for (int j = 0; j < allColumns.getLength(); j++) {
							if (isInt(allColumns.item(j).getTextContent())) {
								selectedWithNull[i][j] = Integer.valueOf(allColumns.item(j).getTextContent());
							} else {
								selectedWithNull[i][j] = allColumns.item(j).getTextContent().replaceAll("'", "");
							}
						}
					}
				}
			}

			Object[][] selected = new Object[selectedRowsNumber][allColumns.getLength()];
			int k = 0;
			for(int i = 0; i < selectedWithNull.length; i++) {
				if(selectedWithNull[i][0] != null) {
					for(int j = 0; j < allColumns.getLength(); j++) {
						selected[k][j] = selectedWithNull[i][j];
					}
					k++;
				}
			}
			return selected;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public int deleteFromTable(String tableName, Condition condition) {
		int numOfDeleted = 0;
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			
			for (int i = 0; i < allItems.getLength(); i++) {
				Node row = allItems.item(i);
				if (row.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) row;
					boolean satisfyCondition = false;
					String valueTested = e.getElementsByTagName(condition.getColName()).item(0).getTextContent();
					boolean isInt = isInt(valueTested);
					if (condition.getOperator() == '=') {
						satisfyCondition = valueTested.equals(condition.getOperand());
					} else if (condition.getOperator() == '>' && isInt) {
						satisfyCondition = Integer.valueOf(valueTested) > Integer.valueOf(condition.getOperand());
					} else if (condition.getOperator() == '<' && isInt && isInt(condition.getOperand())) {
						satisfyCondition = Integer.valueOf(valueTested) < Integer.valueOf(condition.getOperand());
					} else {
						System.out.println("type mismatch");
					}
					if (satisfyCondition) {
						numOfDeleted++;
						i--;
						root.removeChild(row);
					}
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(new DOMSource(xmlDoc), new StreamResult(tableXML));
		} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError
				| TransformerException e) {
			e.printStackTrace();
		}
		return numOfDeleted;

	}

	public int deleteAllFromTable(String tableName) {
		int numOfDeleted = 0;
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			int rowsNumber = allItems.getLength();

			for(int i = 0; i < rowsNumber; i++) {
				Node row = allItems.item(0);
				if (row.getNodeType() == Node.ELEMENT_NODE) {
					numOfDeleted++;
					root.removeChild(row);
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(new DOMSource(xmlDoc), new StreamResult(tableXML));
		} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError
				| TransformerException e) {
			e.printStackTrace();
		}
		return numOfDeleted;

	}

	public int updateTable(String tableName, String[][] colNamesAndNewValues, Condition condition) {
		int numOfUpdated = 0;
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			if(!tableXML.exists()) return 0;
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			for (int i = 0; i < allItems.getLength(); i++) {
				Node column = allItems.item(i);
				if (column.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) column;
					boolean satisfyCondition = false;
					String valueTested = e.getElementsByTagName(condition.getColName()).item(0).getTextContent();
					boolean isInt = isInt(valueTested);
					if (condition.getOperator() == '=') {
						satisfyCondition = valueTested.equals(condition.getOperand());
					} else if (condition.getOperator() == '>' && isInt) {
						satisfyCondition = Integer.valueOf(valueTested) > Integer.valueOf(condition.getOperand());
					} else if (condition.getOperator() == '>' && isInt && isInt(condition.getOperand())) {
						satisfyCondition = Integer.valueOf(valueTested) < Integer.valueOf(condition.getOperand());
					} 
					if (satisfyCondition) {
						numOfUpdated++;
						for (int j = 0; j < colNamesAndNewValues.length; j++) {
							e.getElementsByTagName(colNamesAndNewValues[j][0]).item(0)
									.setTextContent(colNamesAndNewValues[j][1]);
						}
					}
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(new DOMSource(xmlDoc), new StreamResult(tableXML));
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
		return numOfUpdated;

	}

	public int updateAllTable(String tableName, String[][] colNamesAndNewValues) {
		int numOfUpdated = 0;
		try {
			File tableXML = new File(getPath(tableName) + ".xml");
			if(!tableXML.exists()) return 0;
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tableXML);
			Node root = xmlDoc.getFirstChild();
			NodeList allItems = root.getChildNodes();
			for (int i = 0; i < allItems.getLength(); i++) {
				Node column = allItems.item(i);
				if (column.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) column;
					numOfUpdated++;
					for (int j = 0; j < colNamesAndNewValues.length; j++) {
						e.getElementsByTagName(colNamesAndNewValues[j][0]).item(0)
								.setTextContent(colNamesAndNewValues[j][1]);
					}

				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(new DOMSource(xmlDoc), new StreamResult(tableXML));
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
		return numOfUpdated;

	}

	private boolean isInt(String str) {
		if(str.contains("'")){
			return false ;
		}
		else return true;
	}
}
