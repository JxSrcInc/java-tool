package jxsource.util.io.filter;

public class TextFilter extends FileContentFilter implements LineFilter
{
	String[] toMatch;
	boolean caseDepend = true;

	public TextFilter(String[] toMatch)
	{
		this(toMatch, true);
	}
	
	public TextFilter(String[] toMatch, boolean caseDepend)
	{
		this.caseDepend = caseDepend;
		this.toMatch = toMatch;
		if(!caseDepend)
		{
			for(int i=0; i<toMatch.length; i++)
			{
				this.toMatch[i] = toMatch[i].toLowerCase();
			}
		}
		contentFilter = this;
	}
	
	public boolean acceptLine(String data)
	{
		if(!caseDepend)
			data = data.toLowerCase();
		for(int i=0; i<toMatch.length; i++)
		{
			if(data.indexOf(toMatch[i]) > -1)
				return true;
		}
		return false;
	}
	
	public void finalize()
	{
		toMatch = null;
	}
	
}
