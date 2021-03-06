package DatabaseModel.Tables;

import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

/**
 * User definition.
 */
public class User extends Table {

	@PrimaryKey
	@AutoIncrement
	public Integer id;

	public String login;
	public String password;
	
	@Nullable
	public Integer lisenceId;
}
