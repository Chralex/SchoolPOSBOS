package DatabaseModel.Tables;

import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

public class Product extends Table {
	
	@PrimaryKey
	@AutoIncrement
	public Integer id;
	
	public String name;
	
	@Precision( decimals = 4, integers = 53 )
	public Double price;
}
