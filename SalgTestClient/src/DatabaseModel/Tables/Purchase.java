package DatabaseModel.Tables;

import java.util.Date;
import DatabaseModel.Table;

public class Purchase extends Table {
	public Integer id;
	public String orderNumber;
	public Double price;
	public Date orderDate;
}
