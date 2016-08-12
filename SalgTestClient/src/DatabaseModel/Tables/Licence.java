package DatabaseModel.Tables;

import java.sql.Timestamp;
import DatabaseModel.Table;

public class Licence extends Table {
	public Integer id;
	public String licence;
	public Timestamp expiryDate;
}
