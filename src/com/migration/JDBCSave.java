package com.migration;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.webspark.model.AddressModel;

public class JDBCSave {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_fresh";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "welcome";
	
	
	
	
	
	public void saveLedgers(List list, long ofc_id, List grpList, long to_org_id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

			Iterator it1 = grpList.iterator();
			while (it1.hasNext()) {
				GroupModel ledObj = (GroupModel) it1.next();

				PreparedStatement preparedStatement = conn
						.prepareStatement("insert into  i_group values (?, ?, ?, ?, ? , ?, ?,?)");

				try {

					preparedStatement.setLong(1, ledObj.getId());
					preparedStatement.setLong(2, ledObj.getAccount_class_id()); // Name
					preparedStatement.setInt(3, ledObj.getLevel()); // Opening
																	// Balance
					preparedStatement.setString(4, ledObj.getName()); // Status
					preparedStatement.setLong(5, ledObj.getParent_id());
//					preparedStatement.setLong(6, ledObj.getRoot_id()); // Group
																		// ID
					preparedStatement.setLong(7, ledObj.getStatus());
					preparedStatement.setLong(8, to_org_id);
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.out.println("Exception On Group."+e.getCause());
					// TODO: handle exception
				}
				preparedStatement.close();

			}

			Iterator it = list.iterator();
			while (it.hasNext()) {
				LedgerModel ledObj = (LedgerModel) it.next();

				PreparedStatement preparedStatement = conn
						.prepareStatement("insert into  i_ledger values (default, ?, ?, ?, ? , ?, ?,?)");

				try {

					preparedStatement.setDouble(1, 0.0);
					preparedStatement.setString(2, ledObj.getName()); // Name
					preparedStatement.setDouble(3, 0.0); // Opening Balance
					preparedStatement.setLong(4, 1); // Status
					preparedStatement.setObject(5, null);
					preparedStatement.setLong(6, ledObj.getGroup().getId()); // Group
																				// ID
					preparedStatement.setLong(7, ofc_id);
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.out.println("Exception On Ledger."+e.getCause());
					// TODO: handle exception
				}

				preparedStatement.close();

			}

			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
		System.out.println("Goodbye!");
	}// end main
	
	
	
	

	public void saveCustomer(List list, long ofc_id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CustomerModel custObj = (CustomerModel) it.next();
				AddressModel addObj = custObj.getAddress();
				LedgerModel ledObj = custObj.getLedger();
				
				try {
					// Country is needed
					
					long addr_id = 0;
					PreparedStatement preparedStatement=null;
					

						preparedStatement = conn
								.prepareStatement("insert into  s_address values (default, ?, ?, ?, ? , ?, ?,?,?,?,?,?,?,?,?,?)");

						/*preparedStatement.setString(1, addObj.getArea());
						preparedStatement.setString(2,
								addObj.getBuilding_name()); // Name
						preparedStatement.setString(3, addObj.getBuilding_no()); // Opening
																					// Balance
						preparedStatement.setString(4, addObj.getDistrict()); // Status
						preparedStatement.setString(5, addObj.getEmail());
						preparedStatement.setString(6, addObj.getFax()); // Group
																			// ID
						preparedStatement.setString(7, addObj.getMobile());
						preparedStatement.setString(8, addObj.getOtherDetails());
						preparedStatement.setString(9, addObj.getPhone());
						preparedStatement.setString(10, addObj.getPin());
						preparedStatement.setString(11, addObj.getPost());
						preparedStatement.setString(12, addObj.getState());
						preparedStatement.setString(13, addObj.getStreet());
						preparedStatement.setString(14, addObj.getZip());*/
						preparedStatement.setLong(15, 2);
						preparedStatement.executeUpdate();

						ResultSet rst = preparedStatement.getGeneratedKeys();
						if (rst.next())
							addr_id = rst.getLong(1);
						
					

					
					long ledg_id=0;
						
						preparedStatement = conn
								.prepareStatement("insert into  i_ledger values (default, ?, ?, ?, ? , ?, ?,?)");
						
						preparedStatement.setDouble(1, 0.0);
						preparedStatement.setString(2, ledObj.getName()); // Name
						preparedStatement.setDouble(3, 0.0); // Opening Balance
						preparedStatement.setLong(4, 1); // Status
						preparedStatement.setLong(5, addr_id);
						preparedStatement.setLong(6, ledObj.getGroup().getId()); // Group
																					// ID
						preparedStatement.setLong(7, ofc_id);
						preparedStatement.executeUpdate();
						
						rst = preparedStatement.getGeneratedKeys();
						if (rst.next())
							ledg_id = rst.getLong(1);
						

						
						preparedStatement = conn
								.prepareStatement("insert into  i_customer values (default, ? , ?, ?,?,?,?,?,?,?)");
						
						preparedStatement.setDouble(1, custObj.getCredit_limit());
						preparedStatement.setString(2,custObj.getCustomer_code()); // Name
						preparedStatement.setString(3, custObj.getDescription()); // Opening
																					// Balance
						preparedStatement.setInt(6, custObj.getMax_credit_period()); // Group
																			// ID
						preparedStatement.setString(7, custObj.getName());
						preparedStatement.setLong(9, custObj.getSales_type());
						preparedStatement.setLong(10, 2);
						preparedStatement.setLong(11, ledg_id);
						preparedStatement.setLong(12, 2);
						preparedStatement.executeUpdate();

						


				} catch (Exception e) {
					conn.rollback();
					System.out.println("Exception On Customer ."+e.getCause());
					// TODO: handle exception
				}

			}

			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
		System.out.println("Goodbye!");
	}// end main





	public void saveSupplier(List list, long ofc_id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

			Iterator it = list.iterator();
			while (it.hasNext()) {
				SupplierModel custObj = (SupplierModel) it.next();
				AddressModel addObj = custObj.getAddress();
				LedgerModel ledObj = custObj.getLedger();

				try {
					// Country is needed

					long addr_id = 0;

					try {

						PreparedStatement preparedStatement = conn
								.prepareStatement("insert into  s_address values (default, ?, ?, ?, ? , ?, ?,?,?,?,?,?,?,?,?,?)");

						/*preparedStatement.setString(1, addObj.getArea());
						preparedStatement.setString(2,
								addObj.getBuilding_name()); // Name
						preparedStatement.setString(3, addObj.getBuilding_no()); // Opening
																					// Balance
						preparedStatement.setString(4, addObj.getDistrict()); // Status
						preparedStatement.setString(5, addObj.getEmail());
						preparedStatement.setString(6, addObj.getFax()); // Group
																			// ID
						preparedStatement.setString(7, addObj.getMobile());
						preparedStatement
								.setString(8, addObj.getOtherDetails());
						preparedStatement.setString(9, addObj.getPhone());
						preparedStatement.setString(10, addObj.getPin());
						preparedStatement.setString(11, addObj.getPost());
						preparedStatement.setString(12, addObj.getState());
						preparedStatement.setString(13, addObj.getStreet());
						preparedStatement.setString(14, addObj.getZip());
						preparedStatement.setLong(15, 2);*/
						preparedStatement.executeUpdate();

						ResultSet rst = preparedStatement.getGeneratedKeys();
						if (rst.next())
							addr_id = rst.getLong(1);

					} catch (Exception e) {
						System.out.println("Exception On Supplier Address."+e.getCause());
						// TODO: handle exception
					}

					long ledg_id = 0;
					try {

						PreparedStatement preparedStatement = conn
								.prepareStatement("insert into  i_ledger values (default, ?, ?, ?, ? , ?, ?,?)");

						preparedStatement.setDouble(1, 0.0);
						preparedStatement.setString(2, ledObj.getName()); // Name
						preparedStatement.setDouble(3, 0.0); // Opening Balance
						preparedStatement.setLong(4, 1); // Status
						preparedStatement.setLong(5, addr_id);
						preparedStatement.setLong(6, ledObj.getGroup().getId()); // Group
																					// ID
						preparedStatement.setLong(7, ofc_id);
						preparedStatement.executeUpdate();

						ResultSet rst = preparedStatement.getGeneratedKeys();
						if (rst.next())
							ledg_id = rst.getLong(1);

					} catch (Exception e) {
						System.out.println("Exception On Supplier_Ledger."+e.getCause());
						// TODO: handle exception
					}

					try {

						PreparedStatement preparedStatement = conn
								.prepareStatement("insert into  i_supplier values (default, ?, ?, ?, ? , ?, ?,?,?,?,?,?,?,?,?,?,?,?,?)");

						preparedStatement.setString(1, custObj.getBank_name());
						preparedStatement.setString(2,
								custObj.getContact_person());
						preparedStatement.setString(3,
								custObj.getContact_person_email());
						preparedStatement.setString(4,
								custObj.getContact_person_fax());
						preparedStatement.setDouble(5,
								custObj.getCredit_limit());
						preparedStatement.setDouble(6,
								custObj.getCredit_period());
						preparedStatement
								.setString(7, custObj.getDescription());
						preparedStatement.setString(9, custObj.getName());
						preparedStatement.setString(13,
								custObj.getSupplier_code());
//						preparedStatement.setLong(14, custObj.getTax_group());
						preparedStatement.setString(15, custObj.getWebsite());
						preparedStatement.setLong(16, ledg_id);
						preparedStatement.setLong(17, 2);
						preparedStatement.setLong(18, 2);

						preparedStatement.executeUpdate();

					} catch (Exception e) {
						System.out.println("Exception On Supplier."+e.getCause());
						// TODO: handle exception
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
		System.out.println("Goodbye!");
	}// end main
	
	
	
	
	
	public void saveItems(List gps,List sgps,List itmList,List unitList, long ofc_id, long to_org_id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			
			Date today=new Date(new java.util.Date().getTime());
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			
			Iterator gpItr = gps.iterator();
			while (gpItr.hasNext()) {
				ItemGroupModel ledObj = (ItemGroupModel) gpItr.next();

				PreparedStatement preparedStatement = conn
						.prepareStatement("insert into  i_item_group values (?, ?, ?, ?, ? , ?)");
				try {
					preparedStatement.setLong(1, ledObj.getId());
					preparedStatement.setString(2, ledObj.getCode()); // Name
					preparedStatement.setString(3, ledObj.getName()); // Opening
					preparedStatement.setLong(4, 1); // Status
					preparedStatement.setLong(5, to_org_id);
					preparedStatement.setLong(6, 1); // Tax
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.out.println("Exception On Item Group."+e.getCause());
					// TODO: handle exception
				}
				preparedStatement.close();
			}
			
			
			Iterator sgpItr = sgps.iterator();
			while (sgpItr.hasNext()) {
				ItemSubGroupModel ledObj = (ItemSubGroupModel) sgpItr.next();
				
				PreparedStatement preparedStatement = conn
						.prepareStatement("insert into  i_item_subgroup values(?,?,?,?,?,?)");
				try {
					preparedStatement.setLong(1, ledObj.getId());
					preparedStatement.setString(2, ledObj.getCode()); // Name
					preparedStatement.setString(3, ledObj.getName()); // Opening
					preparedStatement.setLong(4, 1); // Status
					preparedStatement.setLong(5, ledObj.getGroup().getId());
					preparedStatement.setLong(6, 1); // Tax
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.out.println("Exception On Item Subgorup."+e.getCause());
					// TODO: handle exception
				}
				preparedStatement.close();
			}
			
			
			Iterator unitItr = unitList.iterator();
			while (unitItr.hasNext()) {
				UnitModel ledObj = (UnitModel) unitItr.next();
				
				PreparedStatement preparedStatement = conn
						.prepareStatement("insert into  i_unit values(?,?,?,?,?,?)");
				try {
					preparedStatement.setLong(1, ledObj.getId());
					preparedStatement.setString(2, ledObj.getDescription()); // Name
					preparedStatement.setString(3, ledObj.getName()); // Opening
					preparedStatement.setLong(4, 1); // Status
					preparedStatement.setString(5, ledObj.getSymbol());
					preparedStatement.setLong(6, to_org_id); // Org
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.out.println("Exception On Unit."+e.getCause());
					// TODO: handle exception
				}
				preparedStatement.close();

			}

			Iterator it = itmList.iterator();
			while (it.hasNext()) {
				ItemModel custObj = (ItemModel) it.next();

				try {
					// Country is needed
					
					long itm_id = 0;
					
					try {

						PreparedStatement preparedStatement = conn
								.prepareStatement("insert into  i_item values (default, ?, ?, ?, ? , ?, ?,?,?,?,?,?,?,?,?)");

						preparedStatement.setString(1, String.valueOf(custObj.getCess_enabled()));
						preparedStatement.setDouble(2, 0);
						preparedStatement.setString(3, custObj.getItem_code());
						preparedStatement.setString(4, custObj.getName());
						preparedStatement.setDouble(5, 0);
						preparedStatement.setDouble(6, custObj.getRate());
						preparedStatement.setDouble(7, custObj.getReorder_level());
						preparedStatement.setLong(9, 1);
						preparedStatement.setLong(10, ofc_id);
						preparedStatement.setLong(11, 1);
						preparedStatement.setLong(12, 1);
						preparedStatement.setLong(13, custObj.getSub_group().getId());
						preparedStatement.setLong(14, custObj.getUnit().getId());
						
						preparedStatement.executeUpdate();

						ResultSet rst = preparedStatement.getGeneratedKeys();
						if (rst.next())
							itm_id = rst.getLong(1);
						
						preparedStatement = conn.prepareStatement("insert into  i_item_stock values (default, ?, ?, ?, ? , ?, ?,?,?)");
						
						preparedStatement.setDouble(1, 0);
						preparedStatement.setDate(2, today);
						preparedStatement.setDate(3, today);
						preparedStatement.setLong(4, 0);
						preparedStatement.setDouble(5, 0);
						preparedStatement.setDouble(6, custObj.getRate());
						preparedStatement.setLong(7, 1);
						preparedStatement.setLong(8, itm_id);
						
						preparedStatement.executeUpdate();
						
						preparedStatement = conn.prepareStatement("insert into  i_item_unit_management values (default, ?, ?, ?, ? , ?, ?,?)");

						preparedStatement.setLong(1, custObj.getUnit().getId());
						preparedStatement.setLong(2,  custObj.getUnit().getId());
						preparedStatement.setDouble(3, 1);
						preparedStatement.setDouble(4, custObj.getRate());
						preparedStatement.setLong(5, 1);
						preparedStatement.setLong(6, 1);
						preparedStatement.setLong(7, itm_id);
						
						preparedStatement.executeUpdate();
						
						
						
						
						
					} catch (Exception e) {
						System.out.println("Exception On Item."+e.getCause());
						// TODO: handle exception
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
		System.out.println("Goodbye!");
	}// end main





}















