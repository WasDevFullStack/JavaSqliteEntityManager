package bd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtil {
	
	
	public static void clonarObj(Object from){
		Object to = null;
		try{
			to = from.getClass().newInstance();
			
			if(from != null && to != null){
				
				Class<?> csFrom = from.getClass();
				Class<?> csTo = to.getClass();
				
				Field[] fds = csFrom.getDeclaredFields();
				if(fds != null && fds.length > 0){
					for(Field fdFrom : fds){
						boolean travar = !fdFrom.isAccessible();
						
						try{	
							if(travar){
								fdFrom.setAccessible(true);
							}
							
							
							Field fdTo = null;
							try{
								fdTo = csTo.getDeclaredField(fdFrom.getName());
							}
							catch(NoSuchFieldException nsfe){}
							
							if(fdTo != null){
								boolean travarTo = !fdTo.isAccessible();
								if(travarTo){
									fdTo.setAccessible(true);
								}
								
								try{
									
									if(fdTo.getType().equals(fdFrom.getType())){
										Object valor = fdFrom.get(from);
										fdTo.set(to,valor);
									}
									
								}catch(Exception ex){
									ex.printStackTrace();
								}
								
								
								if(travarTo){
									fdTo.setAccessible(false);
								}
							}
							
						}
						catch(Exception ex){
							ex.printStackTrace();
						}finally{
							if(travar){
								fdFrom.setAccessible(false);
							}
						}
					}
					
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void clonarObj(Object from, Object to){
		try{
			if(from != null && to != null){
				
				Class<?> csFrom = from.getClass();
				Class<?> csTo = to.getClass();
				
				Field[] fds = csFrom.getDeclaredFields();
				if(fds != null && fds.length > 0){
					for(Field fdFrom : fds){
						boolean travar = !fdFrom.isAccessible();
						
						try{	
							if(travar){
								fdFrom.setAccessible(true);
							}
							
							
							Field fdTo = null;
							try{
								fdTo = csTo.getDeclaredField(fdFrom.getName());
							}
							catch(NoSuchFieldException nsfe){}
							
							if(fdTo != null){
								boolean travarTo = !fdTo.isAccessible();
								if(travarTo){
									fdTo.setAccessible(true);
								}
								
								try{
									
									if(fdTo.getType().equals(fdFrom.getType())){
										Object valor = fdFrom.get(from);
										fdTo.set(to,valor);
									}
									
								}catch(Exception ex){
									ex.printStackTrace();
								}
								
								
								if(travarTo){
									fdTo.setAccessible(false);
								}
							}
							
						}
						catch(Exception ex){
							ex.printStackTrace();
						}finally{
							if(travar){
								fdFrom.setAccessible(false);
							}
						}
					}
					
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void callMethod(String method,Object o,Object[] argValues){
		try{
			Class<?> cls = o.getClass();
			Class<?>[] args = null;
			int lenArgs = 0;
			Method m = null;
			
			if(argValues != null && (lenArgs = argValues.length) > 0){
				args = new Class<?>[lenArgs];
				for(int i = 0 ; i < lenArgs ; i++){
					args[i] = argValues[i].getClass();
				}
				m = cls.getDeclaredMethod(method,args);
			}else{
				m = cls.getDeclaredMethod(method);
			}
			 
			if(m != null){
				boolean travar = !m.isAccessible();
				if(travar){
					m.setAccessible(true);
				}
				
				if(lenArgs > 0){
					m.invoke(o,argValues);
				}else{
					m.invoke(o);
				}
				
				if(travar){
					m.setAccessible(false);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static boolean setField(String nameField,Object valueField,Object o){
		boolean ret = false;
		
		try{
			Class<?> cls = o.getClass();
			Field f = null;
			
			f = cls.getDeclaredField(nameField);
			 
			if(f != null){
				boolean travar = !f.isAccessible();
				if(travar){
					f.setAccessible(true);
				}
				
				f.set(o,valueField);
				
				if(travar){
					f.setAccessible(false);
				}
				
				ret = true;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	public static Object getFieldValue(String nameField,Object o){
		Object ret = null;
		
		try{
			Class<?> cls = o.getClass();
			Field f = null;
			
			f = cls.getDeclaredField(nameField);
			 
			if(f != null){
				boolean travar = !f.isAccessible();
				if(travar){
					f.setAccessible(true);
				}
				
				ret = f.get(o);
				
				if(travar){
					f.setAccessible(false);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	public static <T> T getFieldValue(String nameField,Object o,Class<T> typeReturn){
		T ret = null;
		
		try{
			Class<?> cls = o.getClass();
			Field f = null;
			
			f = cls.getDeclaredField(nameField);
			 
			if(f != null){
				boolean travar = !f.isAccessible();
				if(travar){
					f.setAccessible(true);
				}
				
				ret = (T)f.get(o);
				
				if(travar){
					f.setAccessible(false);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	public static <T> List<T> fillDatabaseListOfObject(Class<T> objectClass,ResultSet rs,String... ignoredFields){
		List<T> ret = new ArrayList<T>();
		
		try{
			
			while(rs.next()){
				ret.add(fillDatabaseObject(objectClass,rs,ignoredFields));
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
		return ret;
		
	}

	
	public static <T> T fillDatabaseObject(Class<T> objectClass,ResultSet rs,String... ignoredFields){
		T ret = null;
		
		try{
			
			Map<String,Boolean> mi = new HashMap<String,Boolean>();
			if(ignoredFields != null && ignoredFields.length > 0){
				for(String c : ignoredFields){
					mi.put(c,false);
				}
			}
			
			ret = objectClass.newInstance();
			
			Field[] fields = objectClass.getDeclaredFields();
			
			if(fields != null && fields.length > 0){
				
				for(Field f : fields){
					
					Class<?> typeFiled = f.getType();
					String columnName = f.getName();
					boolean campoValido = true;
					
					if(mi.containsKey(columnName)){
						campoValido = mi.get(f.getName());
						mi.remove(columnName);
					}
					
					if(campoValido){
						
						boolean travar = !f.isAccessible();
						if(travar){
							f.setAccessible(true);
						}
						
						
						int index = -1;
						try{
							
							index = rs.findColumn(columnName);
							
							if(index >= 0){
                                                            
                                                            if(typeFiled.equals(Boolean.class)){
                                                                
                                                                Object o = rs.getObject(index);
                                                                if(o instanceof Integer){
                                                                    f.set(ret,(int)o > 0);
                                                                }else if(o instanceof Long){
                                                                    f.set(ret,(long)o > 0);
                                                                }else if(o instanceof Short){
                                                                    f.set(ret,(short)o > 0);
                                                                }else{
                                                                    f.set(ret,o);
                                                                }
                                                                
                                                            }else if(typeFiled.equals(boolean.class)){
                                                                
                                                                Object o = rs.getObject(index);
                                                                if(o instanceof Integer){
                                                                    f.set(ret,(int)o > 0);
                                                                }else if(o instanceof Long){
                                                                    f.set(ret,(long)o > 0);
                                                                }else if(o instanceof Short){
                                                                    f.set(ret,(short)o > 0);
                                                                }else{
                                                                    f.set(ret,o);
                                                                }
                                                                
                                                            }else{
                                                                Object o = rs.getObject(index);
                                                                f.set(ret,o);
                                                            }
                                                            
							}
							
						}catch(Exception ex){}
						
						if(travar){
							f.setAccessible(false);
						}
						
					}
					
				}
				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
		return ret;
		
	}
	
	public static Field findFieldByAnnotation(Class target,Class annotation){
		
		try{
		
			Field[] fds = target.getDeclaredFields();
			
			if(fds != null && fds.length > 0){
				
				for(Field f : fds){
					
					if(f.isAnnotationPresent(annotation)){
						return f;
					}
					
				}
				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}

}
