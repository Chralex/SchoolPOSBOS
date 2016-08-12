package DatabaseModel.Tables;

import java.util.Date;

import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

public class Purchase extends Table {
	
	@PrimaryKey
	@AutoIncrement
	public Integer id;
	
	public String orderNumber;
	
	@Precision( decimals = 4, integers = 53 )
	public Double price;
	
	public Date orderDate;
}
