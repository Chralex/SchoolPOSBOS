package DatabaseModel;

import java.util.ArrayList;

public class DatabaseModel {

	public ArrayList<Table> tables;
	public ArrayList<Table> tableData;

	public DatabaseModel() {

	}

	public DatabaseModel(ArrayList<Table> tables) {
		this.tables = tables;
	}

	public DatabaseModel(ArrayList<Table> tables, ArrayList<Table> tableData) {
		this.tables = tables;
		this.tableData = tableData;
	}

}
