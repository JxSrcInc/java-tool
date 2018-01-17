package jxsource.playground.regex;

import java.util.regex.*;

public class RegexWord {
	public static void main(String args[]) {
		String match = "baloney";
		// (\\n|.)* - any number of char and \n
		// (?i) - ignore case
		// (\\W+|^) - at least one \W or begin of line
		// (\\W+|$) - at least one \W or end of line
		Pattern p = Pattern.compile("(\\n|.)*(?i)(\\W+|^)"+match+"(\\W+|$).*");
		System.out.println(p.matcher("o&& baloney").matches());
		System.out.println(p.matcher("o&& BALoNEY ").matches());
		System.out.println(p.matcher("\nbaloney \"sldind  sidf").matches());
		System.out.println(p.matcher("o&&baLoney sldind  sidf").matches());
		System.out.println(p.matcher("o&&baLoney").matches());
		System.out.println(p.matcher("\n () uyt o&& baLoney sldind  sidf").matches());
		
		match = "baloney f";
		p = Pattern.compile("(\\n|.)*(?i)(\\W+|^)"+match+"(\\W+|$).*");
		System.out.println(p.matcher("o&& baloney f").matches());
		System.out.println(p.matcher("o&& baloney f dse").matches());
		System.out.println("false: "+p.matcher("o&& baloney fd").matches());

	}
}
