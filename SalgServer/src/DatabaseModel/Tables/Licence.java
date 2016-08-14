package DatabaseModel.Tables;

import java.sql.Timestamp;
import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

/**
 * Holds licence information. The same licence may be used by several users.
 * The expiry date may be null if there is no expiry date to the licence.
 */
public class Licence extends Table {

	@PrimaryKey
	@AutoIncrement
	public Integer id;

	public String licence;
	public Timestamp expiryDate;
}
