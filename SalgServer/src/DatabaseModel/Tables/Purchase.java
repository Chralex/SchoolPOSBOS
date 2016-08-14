package DatabaseModel.Tables;

import java.util.Date;

import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

/**
 * Holds information of combined sales, seen as an entire order or a collection of sales.
 * Several sales points to a single purchase by order number.
 */
public class Purchase extends Table {
	
	@PrimaryKey
	@AutoIncrement
	public Integer id;
	
	public String orderNumber;
	
	@Precision( decimals = 4, integers = 53 )
	public Double price;
	
	public Date orderDate;
	
	public Integer userId;
}
