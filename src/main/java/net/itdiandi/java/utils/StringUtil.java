package net.itdiandi.java.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static void main(String[] args) {
		System.out.println("----------------1------------------");
		String str = "a,b,[d,e,f]";
		String[] strArr = str.split("((?!\\[),(?<!\\]))");
//		(?<=(?<!\\\\)\\$\\{)(.*?)(?=(?<!\\\\)\\})
		for(String s: strArr){
			System.out.println(s);
		}
		System.out.println("---------------2------------------");
		str = "abc 123 def";
		strArr = str.split("(?<=\\s)\\d+(?=\\s)");
		for(String s: strArr){
			System.out.println(s);
		}
		
		System.out.println("---------------2------------------");
		str = "a,b,c[d,e,f],h,i,j[k,l,m],n";
		Matcher matcher = Pattern.compile("(?<!\\[)(,)(?<!\\])").matcher(str);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
		System.out.println("---------------3------------------");
//		(?<!\")(:)(?=\")：
		str = "timestamp:\"2018-03-01 12:34:12\"";
		System.out.println(Arrays.toString(str.split("(?<!\")(:)(?=\")"))); // [timestamp, "2018-03-01 12:34:12"]
		System.out.println("---------------4------------------");
		str = "/hosts/${ht.name}";
		matcher = Pattern.compile("(?<=(?<!\\\\)\\$\\{)(.*?)(?=(?<!\\\\)\\})").matcher(str);
		if (matcher.find()) {
			System.out.println(matcher.group());
		}
		System.out.println("---------------5------------------");
		
		/**我觉得： 
		 *  1. positive lookahead应该翻译成肯定性预查，而非正向预查。 
		 *  2. negative lookahead应该翻译成否定性预查，而非负向预查。 
		 *  3. positive lookbehind，negative lookbehind　同理。 
		 */
		
		/** 
	     *   (?=pattern) 正向预查，在任何匹配 pattern 的字符串开始处匹配查找字符串。 
	     * 这是一个非获取匹配，也就是说，该匹配不需要获取供以后使用或者说:不可通过 
	     * matcher.group(i)得到该组(i!=0). 
	     *   例如，'Windows (?=95|98|NT|2000)' 能匹配 "Windows 2000" 中的 "Windows" ， 
	     * 但不能匹配 "Windows 3.1" 中的 "Windows"。 
	     *   预查不消耗字符，也就是说，在一个匹配发生后，在最后一次匹配之后立即开始 
	     * 下一次匹配的搜索，而不是从包含预查的字符之后开始。  
	     */  
	    Pattern pat = Pattern.compile("win(?=98|nt)[x]");  
	    Matcher mat = pat.matcher("winntx");  
	    while (mat.find()) {  
	        //不匹配: 预查不消耗字符, 尚没有匹配到[x]处  
	        System.out.println("1:" + mat.group());  
	    }  
	    pat = Pattern.compile("win(?=98|nt)[n]");  
	    mat = pat.matcher("winntx");  
	    while (mat.find()) {  
	        System.out.println("2:" + mat.group());//匹配.  
	    }  
	    /** 
	     * (?:pattern), 这也是未捕获组，但看起来它是消耗性匹配。 
	     */  
	    pat = Pattern.compile("win(?:98|nt)[n]");  
	    mat = pat.matcher("winntx");  
	    while (mat.find()) {  
	        //不匹配: 看起来它是一个消耗性匹配  
	        System.out.println("3:" + mat.group());//不匹配,因为[n]未被匹配。  
	    }  
	    pat = Pattern.compile("win(?:98|nt)[n]");  
	    mat = pat.matcher("winntn");  
	    while (mat.find()) {  
	        System.out.println("4:" + mat.group());//匹配, [n]被匹配.  
	    }  
	    /** 
	     * 　(?!pattern) 负向预查，在任何不匹配 pattern 的字符串开始处匹配查找字符串。 
	     * 这是一个非获取匹配，也就是说，该匹配不需要获取供以后使用。 
	     * 　例如'Windows (?!95|98|NT|2000)' 能匹配 "Windows 3.1" 中的 "Windows"， 
	     * 但不能匹配 "Windows 2000" 中的 "Windows"。预查不消耗字符。 
	     *  
	     */  
	    pat = Pattern.compile("win(?!98|nt)[x]");  
	    mat = pat.matcher("win97x");  
	    while (mat.find()) {  
	        //不匹配: 预查不消耗字符, 尚没有匹配到[x]处  
	        System.out.println("11:" + mat.group());  
	    }  
	    pat = Pattern.compile("win(?!98|nt)[9]");  
	    mat = pat.matcher("win97x");  
	    while (mat.find()) {  
	        //匹配：虽未匹配至[9]处，但匹配到了9处。  
	        System.out.println("12:" + mat.group());  
	    }  
	    /** 
	        ?=  代表后面必须有 
	        ?!  代表后面不能有    
	        ?<= 代表前面必须有(positive lookbehind)    
	        ?<! 代表前面不能有(negative lookbehind) 
	     */  
	    pat = Pattern.compile("(?<=98)win");  
	    mat = pat.matcher("97win");  
	    while (mat.find()) {  
	        System.out.println("13:" + mat.group());//不匹配: win必须前邻98  
	    }  
	    pat = Pattern.compile("(?<!98)win");  
	    mat = pat.matcher("97win");  
	    while (mat.find()) {  
	        System.out.println("14:" + mat.group());//匹配, 因为win不前邻98  
	    } 
	}
}
