package DatabaseModel.Tables;

import DatabaseModel.Table;
import DatabaseModel.Annotations.*;

/**
 * A single sale that is a part of a single Purchase containing one or more sales.
 */
public class Sale extends Table {

	@AutoIncrement
	@PrimaryKey
	public Integer id;

	public String orderNumber;

	public Integer productId;

	@Precision(decimals = 4, integers = 53)
	public Double price;

}
