package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

final class SqliteBD {
	
	
	private static final Semaphore sm = new Semaphore(1);
	
	private static final String nomeBanco = "serialprodutosbottomup.db";
	
	
	public static synchronized final Connection openConnectionAndLock(){
		Connection c = null;
		
		try{
			sm.acquire();
			
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+nomeBanco);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return c;
	}
	
	public static synchronized final void closeAndUnlock(Connection c){
		try{ 
			if(c == null){
				return;
			}
			
			c.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			
			sm.release();
			
		}
	}
	
	public static synchronized final void commite(Connection c,boolean roolBack){
		try{ 
			
			if(c == null){
				return;
			}
			
			c.commit();
			
		}catch(Exception ex){
			if(roolBack){
				
				try {
					c.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}finally{
			
			try{
				c.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
	}
	
	public static synchronized final void commiteAndUnlock(Connection c,boolean roolBack){
		try{ 
			if(c == null){
				return;
			}
			
			c.commit();
			
		}catch(Exception ex){
			if(roolBack){
				
				try {
					c.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}finally{
			
			try{
				c.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			sm.release();
			
		}
	}
	

}
