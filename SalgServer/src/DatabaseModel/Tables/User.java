package DatabaseModel.Tables;
import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

public class User extends Table {
	
	@PrimaryKey
	@AutoIncrement
	public Integer id;
	
	public String login;
	public String password;
	public Integer lisenceId;
}
