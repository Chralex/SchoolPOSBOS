package DatabaseModel.Tables;

import java.sql.Timestamp;
import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

public class Licence extends Table {

	@PrimaryKey
	@AutoIncrement
	public Integer id;

	public String licence;
	public Timestamp expiryDate;
}
