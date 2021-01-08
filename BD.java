package bd;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import bd.annotations.DBID;
import bd.annotations.DBObject;
import bd.annotations.DBTable;


public final class BD {
	
	private static final SqliteBD bd = new SqliteBD();
	
	public static void main(String[] args){
		
           
		
	}
	
	public final static <T> T getDBOject(Class<T> returnType,String fieldName,Object obj,String... ignoredFields){
		T ret = null;
		
		Connection con = bd.openConnectionAndLock();
		try{
			
			Field f = obj.getClass().getDeclaredField(fieldName);
			
			if(f != null && f.isAnnotationPresent(DBObject.class)){
				
				boolean travar = !f.isAccessible();
				if(travar){
					f.setAccessible(true);
				}
				
				try{
				
					long idObjecto = f.getLong(obj);
					
					if(idObjecto <= 0){
						return null;
					}
					
					DBObject dbO = f.getAnnotation(DBObject.class);
					String tabela = dbO.tabela();
					String chavePrimaria = dbO.chavePrimaria();
					
					String sql = "SELECT * FROM "+tabela+" WHERE excluido=false AND "+chavePrimaria+"="+idObjecto;
					
					PreparedStatement ps = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, 
		                    ResultSet.CONCUR_READ_ONLY);
					
					ResultSet rs = ps.executeQuery();
				
					if(rs != null){
						ret = ReflectionUtil.fillDatabaseObject(returnType,rs,ignoredFields);
					}
					
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				if(travar){
					f.setAccessible(false);
				}
				
				
			}
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			
		}finally{
			
			bd.closeAndUnlock(con);
			
		}
		return ret;
	}
        
        
        
	
	public final static <T> T save(T obj,String... ignoredFields){
		
		Connection con = bd.openConnectionAndLock();
		try{
			con.setAutoCommit(false);
			
			String sql = "";
			
			Class<T> cl = (Class<T>) obj.getClass();
			
			if(cl.isAnnotationPresent(DBTable.class)){
				
				DBTable tbl = cl.getAnnotation(DBTable.class);
				
				String nomeTabela = tbl.name();
				
				Field f = ReflectionUtil.findFieldByAnnotation(cl,DBID.class);
				
				if(f != null){
					
					DBID id = f.getAnnotation(DBID.class);
					
					String idNome = id.name();
					if(idNome.isEmpty()){
						idNome = f.getName();
					}
					
					f.setAccessible(true);
					long idLong = f.getLong(obj);
					f.setAccessible(false);
					
					
					
					Map<String,Boolean> mi = new HashMap<String,Boolean>();
					if(ignoredFields != null && ignoredFields.length > 0){
						for(String c : ignoredFields){
							mi.put(c,false);
						}
					}
					
					Field[] fields = cl.getDeclaredFields();
					
					List<Object> params = new ArrayList<Object>();
					
					if(idLong <= 0){
						//NOVO
						sql += "INSERT INTO	"+nomeTabela+" (";
						
						String plus = "";
						String plusParams = "";
						for(Field fd : fields){
							
							fd.setAccessible(true);
							
							
							if(!fd.isAnnotationPresent(DBID.class)){
								String nameField = fd.getName();
								
								if(!mi.containsKey(nameField)){
									
									Object oFd = fd.get(obj);
									
									if(oFd != null){
										plus += (","+nameField);
										plusParams += ",?";
										params.add(oFd);
									}
									
									
								}else{
									
									mi.remove(nameField);
									
								}
								
								
							}
							
							fd.setAccessible(false);
							
						}
						
						sql += plus.substring(1)+") VALUES("+plusParams.substring(1)+")";
						
						
						PreparedStatement ps = con.prepareStatement(sql);
						
						int i = 1;
						for(Object o : params){
							ps.setObject(i,o);
							i++;
						}
						
						int ct = ps.executeUpdate();
						
						ResultSet rs = ps.getGeneratedKeys();
						
						if (rs.next()) {
							
							/*
							long idCreated = Long.valueOf(rs.getLong(1));
							f.setAccessible(true);
							f.set(obj,idCreated);
							f.setAccessible(false);
							*/
							
							f.setAccessible(true);
							f.set(obj,rs.getObject(1));
							f.setAccessible(false);
							
			            }
						
						//System.out.println(ct);
						
					}else{
						//UPDATE
						sql += "UPDATE "+nomeTabela+" SET ";
						
						String plus = "";
						for(Field fd : fields){
							
							fd.setAccessible(true);
							
							
							if(!fd.isAnnotationPresent(DBID.class)){
								String nameField = fd.getName();
								
								if(!mi.containsKey(nameField)){
									
									Object oFd = fd.get(obj);
									
									if(oFd != null){
										
										plus += (","+nameField+" = ?");
										params.add(oFd);
										
									}
									
									
								}else{
									
									mi.remove(nameField);
									
								}
								
								
							}
							
							fd.setAccessible(false);
							
						}
						
						sql += plus.substring(1)+" WHERE "+idNome+" = "+idLong;
						
						PreparedStatement ps = con.prepareStatement(sql);
						
						int i = 1;
						for(Object o : params){
							ps.setObject(i,o);
							i++;
						}
						
						int ct = ps.executeUpdate();
						
						//System.out.println(ct);
						
					}
					
					
					
					
					bd.commite(con,true);
					
				}else{
					
					throw new Exception("Objecto n�o tem ID definido");
					
				}
				
				
				
			}else{
				throw new Exception("Objecto n�o � uma tabela do banco de dados");
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			bd.closeAndUnlock(con);
		}
		
		return obj;
	}
	
	
	//METODOS//
        
        //Put your code here to use as a singleton
	
        
}
