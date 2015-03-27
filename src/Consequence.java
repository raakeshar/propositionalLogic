
public class Consequence {
	
	public String first;
	public String second;
	public String predicateName;
	
	
	public Consequence(String first, String second)
	{
		this.first = first;
		this.second = second;
		
	}
	
	public Consequence() {
		// TODO Auto-generated constructor stub
	}

	public String getFirst()
	{
		return this.first;
	}
	
	public String getSecond()
	{
		return this.second;
	}
	
	public void setFirst(String first)
	{
		this.first = first;
	}
	
	public void setSecond(String second)
	{
		this.second = second;
	}
	
	public String getPredicateName()
	{
		return this.predicateName;
	}
	
	public void setPredicateName(String predicateName)
	{
		this.predicateName = predicateName;
	}

}
