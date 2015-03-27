
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class agent {
	/**
	 * @param args
	 */
	static ArrayList<Rules> knowledgeBase = new ArrayList<Rules>();
	static ArrayList<Rules> clauses = new ArrayList<Rules>();
	static ArrayList<Rules> replacedClauses = new ArrayList<Rules>();
	static ArrayList<String> factsOne = new ArrayList<String>();
	static ArrayList<String> factsTwo = new ArrayList<String>();
	static LinkedList<String> query=new LinkedList<String>();
	static LinkedList<String> testQuery=new LinkedList<String>();
	static ArrayList<String> fact=new ArrayList<String>();
	static Set<Consequence> consequences= new HashSet<Consequence>();
	static Consequence consequence = new Consequence();
	static Query queries = new Query();
	static String queryfile = null;
	static String variable;
	static ArrayList<String> variabes = new ArrayList<String>();
	static PrintWriter writerLog;
	static PrintWriter writerOutput;
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		//String kbfile=args[3];
	    
	    String outputentail=null;
	    String outputlog=null;
	    int count=0;
	    ArrayList<String> sbclauses = new ArrayList<String>();
	   // writerLog = new PrintWriter(outputlog, "UTF-8");
	   // writerOutput= new PrintWriter(outputentail, "UTF-8");
	    StringBuilder sb = new StringBuilder();
	    BufferedReader br;
	    br= new BufferedReader( new FileReader("input.txt"));
	    
		String line;
		try {
			queryfile = br.readLine();
			
			count = Integer.parseInt(br.readLine());
			
			while ((line = br.readLine()) != null || count> 0) {
				if(line != null)
				sbclauses.add(line);
				count--;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		processquery(queryfile);
	    processKBfile(sbclauses);
	    
	    backwardchain();
	    
	}
	
	static void backwardchain()
	{
		
		boolean result = false;
		if(query.size() == 0)
			result = false;
		for(int i=0;i< variabes.size();i++)
		{
			if(!replacedClauses.isEmpty())
				replacedClauses.clear();
			if(testQuery.isEmpty())
				testQuery.addFirst(query.get(0));
			else
			{
				testQuery.clear();
				testQuery.addFirst(query.get(0));
			}
			ClauseReplace(variabes.get(i));
			result = backchain(testQuery);
			if(result == true)
			{
				break;
			}
		}
		
		
			 //result = backchain(query);
			if(result == true)
		    {
				String output = "output.txt";
				try {
					writerLog = new PrintWriter(output);
					writerLog.println("TRUE");
					writerLog.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//writerOutput.println("YES");
		    }
		    else if(result == false)
		    {
		    	String output = "output.txt";
				try {
					writerLog = new PrintWriter(output);
					writerLog.println("FALSE");
					writerLog.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    }
			
	}
	static boolean backchain(LinkedList<String> goals)
	{
		if(goals.getFirst() == null)
			return true;
		else
		{
			String topOfQueue=goals.removeFirst();
			
	    	 if(fact.contains(topOfQueue))
			 {
	    		 
	    		 return true;
			 }
	    	 else
	    	 {   int flag2=0;
	    		 for (Iterator<Rules> iterator = replacedClauses.iterator(); iterator.hasNext(); ) {
						Rules entry=iterator.next();
						if(entry.consequent.equals(topOfQueue))
						{
							flag2=1;
							String prec=entry.antecedent;
							
							
							if(prec.contains("&"))
							{
								String first = prec.split("&")[0];
								first = ReplaceX(first);
								String second = prec.split("&")[1];
								second = ReplaceX(second);
								
								goals.addFirst(first);
								goals.addFirst(second);
							}
							else
							{
								prec = ReplaceX(prec);
								goals.addFirst(prec);
							}
								
							
							
							
							boolean result = false;
							while(!(goals.isEmpty()))
							{
								result = backchain(goals);
								if(result == false)
									break;
							}
							
			    			if(result == true)
			    			   {
			    				
			    				   return true;
			    				
			    			   }
			    			else
			    			  {
			    				return false;
			    				//goals.clear();
			    			  }  
							//return result;
						}
						
						
	    	 }
	    		 if(flag2==0)
	    		 {
	    			 //writerLog.println(topOfQueue+" # N/A # N/A");
					return false;
	    		 }
	    		 return false;
		     }
		}
	}
	
	
	
	static void processquery(String path)
	{
		int endIndex;
		int beginIndex;
		BufferedReader br;
		try{
			
			String line = path;
			
				 endIndex = line.indexOf("(");
				queries.predicateName = line.substring(0,endIndex);
				beginIndex = line.indexOf("(");
				endIndex = line.indexOf(",");
				queries.first = line.substring(beginIndex+1, endIndex);
				beginIndex = line.indexOf(",");
				endIndex = line.indexOf(")");
				queries.second = line.substring(beginIndex+1, endIndex);
			
				
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	static String ReplaceX(String second)
	{
		if(second.contains("(x,"))
    	{
			second.replace("(x", "("+variable);
    	}
		else if(second.contains(",x)"))
		{
			second.replace(",x)", ","+variable+")");
		}
		else if(second.contains("(x)"))
		{
			second.replace("(x)", "("+variable+")");
		}
		return second;
	}
	
	static void ClauseReplace(String variable)
	{
		for(int i=0;i<clauses.size();i++)
        {
        	String rep = null;
        	Rules clause = clauses.get(i);
        	if(clause.consequent.contains("(x,")||clause.consequent.contains(",x)")|| clause.consequent.contains("(x)"))
        	{
        		rep = clause.consequent.replace("x", variable);
        		clause = new Rules(clause.antecedent,rep);
        		//clauses.remove(i);
	        	//clauses.add(i, clause);
        		
        		replacedClauses.add(i,clause);
        		continue;
        	}
        	replacedClauses.add(i,clause);
        	
        	
        }
        
        for(int i=0;i<replacedClauses.size();i++)
        {
        	String rep = null;
        	Rules clause = replacedClauses.get(i);
        	if(clause.antecedent.contains("(x,")||clause.antecedent.contains(",x)")|| clause.antecedent.contains("(x)"))
        	{
        		rep = clause.antecedent.replace("x", variable);
        		clause = new Rules(rep, clause.consequent);
        		//replacedClauses.remove(i);
	        	//clauses.add(i, clause);
        		if(replacedClauses.get(i) != null)
        		{
        			replacedClauses.remove(i);
        		}
        		replacedClauses.add(i,clause);
        		continue;
        	}
        	
        	replacedClauses.add(i,clause);
        	
        }
	}
	static void processKBfile(ArrayList<String> sbclauses)
	{
		int endIndex;
		int beginIndex;
		variable = "";
		BufferedReader br;
		try{
			
	        
	        for (Iterator<String> iterator = sbclauses.iterator(); iterator.hasNext(); )
	        {
	        	String facts = iterator.next();
	        	if(facts.contains("=>"))
	        	{
	        		Rules rule = new Rules(facts.split("=>")[0],facts.split("=>")[1]);
	        		clauses.add(rule);
	        	}
	        	else //facts
	        	{
	        		
	        		fact.add(facts);
	        	}	
	        }
	        
	        for(int i=0;i<clauses.size();i++)
	        {
	        	Rules clause = clauses.get(i);
	        	
	        	Consequence cons = new Consequence();
	        	endIndex = clause.consequent.indexOf("(");
	        	cons.setPredicateName(clause.consequent.substring(0,endIndex)) ;
				beginIndex = clause.consequent.indexOf("(");
				if(clause.consequent.contains(","))
					endIndex = clause.consequent.indexOf(",");
				else
				{
					endIndex = clause.consequent.indexOf(")");
				}
					
				
				cons.setFirst(clause.consequent.substring(beginIndex+1, endIndex));
				if(clause.consequent.contains(","))
				{
					beginIndex = clause.consequent.indexOf(",");
					endIndex = clause.consequent.indexOf(")");
				}
				
				cons.setSecond (clause.consequent.substring(beginIndex+1, endIndex));
	        	/*if(clause.contains("(x,")||clause.contains(",x)")|| clause.contains("(x)"))
	        	{
	        		clause.replace("x", variable);
	        	}
	        	clauses.remove(i);
	        	clauses.add(i, clause);*/
				
				if(cons.getPredicateName().equals(queries.predicateName))
				{
					String conseq = null,res = queryfile;
					/*if(clause.consequent.contains("(x,")||clause.consequent.contains(",x)")|| clause.consequent.contains("(x)"))
		        	{
						 conseq = clause.consequent;
						if(conseq.contains("("+queries.first+",")|| conseq.contains(","+queries.first+")")||conseq.contains("("+queries.first+")"))
						{
							res = conseq.replace("x", queries.second);
							//variable = queries.second;
						}
							
						else if(conseq.contains("("+queries.second+",")|| conseq.contains(","+queries.second+")")||conseq.contains("("+queries.second+")"))
						{
							res = conseq.replace("x", queries.first);
							//variable = queries.first;
						}
							
		        	}*/
					
					query.add(res);
				}
				consequences.add(cons);
	        }
	        
	        for(int i=0;i<fact.size();i++)
	        {
	        	String rep = null;
	        	String facts = fact.get(i);
	        	if(facts.contains(","))
	        	{
	        		int begin = facts.indexOf("(");
	        		int end = facts.indexOf(",");
	        		factsOne.add(facts.substring(begin+1, end)) ;
	        		begin = facts.indexOf(")");
	        		factsTwo.add(facts.substring(end+1, begin));
	        	}
	        	else
	        	{
	        		int begin = facts.indexOf("(");
	        		int end = facts.indexOf(")");
	        		factsOne.add(facts.substring(begin+1, end)) ;
	        		
	        	}
	        }
	        if(!factsOne.isEmpty())
	        	for(int i=0;i<factsOne.size();i++)
	        	{
	        		variabes.add(factsOne.get(i));
	        		break;
	        	}
	        
	        
	        
	        for(int i=1;i<factsOne.size();i++)
	        {
	        	if(factsOne.size() > 1){
	        		String first = factsOne.get(i-1);
	        		String second = factsOne.get(i);
	        		if(!first.equals(second))
		        	{
	        			variabes.add(factsOne.get(i));
		        		
		        	}
		        		
		        	
	        		
	        	}
	        	
	        		
	        }
	      
	        
		}catch (Exception e)
		{
			System.out.println(e);
		}
	}

}