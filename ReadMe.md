Here you can see some using examples


Put in you BD.java or wherever you want




Mapping A SQLITE OBJECT:

@DBTable(name = "user")
public class User implements Serializable{
    
    @DBID
    private long id;
	
}

If you want to save or merge a java object just use BD.save(user) or BD.merge(user)


TO GET A LIST OF SOME OBJECT:

private static final SqliteBD bd = new SqliteBD();
private List<User> getUsers(int first,int maxResult,String... ignoredFields)
	Connection con = bd.openConnectionAndLock();
	
	List<User> ret = new ArrayList<User>();
	
	try{
	
		String sql = null;
		if(first > 0 && maxResult > 0){
		   sql = "SELECT * FROM user LIMIT "+first+","+maxResult; 
		}else{
		   sql = "SELECT * FROM user"; 
		}
		
		PreparedStatement ps = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_READ_ONLY);
			
		ResultSet rs = ps.executeQuery();
		
		if(rs != null){
			
			//Here you are filling the objects
			List<User> objs =  ReflectionUtil.fillDatabaseListOfObject(User.class,rs,ignoredFields);
			if(objs != null && objs.size() > 0){
				ret.addAll(objs);
			}
			
		}
	
	}catch(Exception ex){
		ex.printStackTrace();
	}finally{
		bd.closeAndUnlock(con);
	}
	
	return ret;
}

COUNT EXAMPLE:

private static final SqliteBD bd = new SqliteBD();
        
private long numberOfUsers(String... ignoredFields){
	Connection con = bd.openConnectionAndLock();
	long ret = 0;
	
	try{
			
		
		String sql = "SELECT COUNT(id) as 'total' FROM user";
		
		PreparedStatement ps = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, 
					ResultSet.CONCUR_READ_ONLY);
					
		ResultSet rs = ps.executeQuery();
		
		if(rs != null){
			
			CountBDResult countResult =  ReflectionUtil.fillDatabaseObject(CountBDResult.class,rs,ignoredFields);
			ret = countResult.getTotal();
			
		}
		
		
	}catch(Exception ex){
		
		ex.printStackTrace();
		
	}finally{
		bd.closeAndUnlock(con);
	}
	
	return ret;
	

}

GET ONE OBJECT EXAMPLE:

	private static final SqliteBD bd = new SqliteBD();
	
	public User getUserById(long id,String... ignoredFields){
		Connection con = bd.openConnectionAndLock();
		
		User ret = null;
		
		try{
		
			String sql = "SELECT * FROM user WHERE id=?";
			
			PreparedStatement ps = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1,id);
                        
			ResultSet rs = ps.executeQuery();
			
			
			
			if(rs != null){
				
				User obj =  ReflectionUtil.fillDatabaseObject(User.class,rs,ignoredFields);
				if(obj != null && obj.getId() > 0){
					ret = obj;
				}
				
			}
		
		
		}
		catch(Exception ex){
			ex.printStackTrace();
		}finally{
			bd.closeAndUnlock(con);
		}
		
		
		return ret;
	}
		
UPDATE JUST SOME COLUMNS:

    private static final SqliteBD bd = new SqliteBD(); 

	public int changeNameUser(long idUser,String newName){
		
		int ret = 0;
		Connection con = bd.openConnectionAndLock();
		try{
			con.setAutoCommit(false);
			
			String sql = "UPDATE user SET name=? WHERE id=?";
                
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,newName);
			ps.setLong(1,idUser);
			
			ret = ps.executeUpdate();	
                
			bd.commite(con,true);
		
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			bd.closeAndUnlock(con);
		}
		
		return ret;
	}