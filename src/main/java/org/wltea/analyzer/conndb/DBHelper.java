package org.wltea.analyzer.conndb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBHelper {

	
	
	
	private static Connection conn;
	
	
	private static Connection getConn()throws Exception{
		
		Connection con = null;  //创建用于连接数据库的Connection对象  
        try {  
            Class.forName("com.mysql.jdbc.Driver");// 加载Mysql数据驱动  
              
            conn = DriverManager.getConnection(  
                    "jdbc:mysql://localhost:3306/test", "root", "qin");// 创建数据连接  
		
	}catch(Exception e){
		e.printStackTrace();
	}
        
        
        return conn;
	}
	
	public static String getKey(String key)throws Exception{
		//System.out.println("连接数据库了");
		String data="";
		conn=getConn();
		String sql="select * from lucenedic ";
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet  rs=ps.executeQuery();
		if(rs.next()){
			data=rs.getString(key);
		}
		rs.close();
		ps.close();
		conn.close();
		return data;
	}
	
	
	
	public static void main(String[] args)throws Exception {
		
		System.out.println(getKey("ext"));
		System.out.println(getKey("stopword"));
		System.out.println(getKey("synonym"));
		
	}
	
	
}
