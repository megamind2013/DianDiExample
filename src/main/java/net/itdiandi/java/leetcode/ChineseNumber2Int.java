package net.itdiandi.java.leetcode;

import java.util.ArrayList;
import java.util.List;

public class ChineseNumber2Int {
	public static void main(String[] args) {
		String str = "一千三百二十";
		
		List<String> numList = new ArrayList<>();
		numList.add("零");
		numList.add("一");
		numList.add("二");
		numList.add("三");
		numList.add("四");
		numList.add("五");
		numList.add("六");
		numList.add("七");
		numList.add("八");
		numList.add("九");
		
		List<String> unitList = new ArrayList<String>();
		unitList.add("个");// 10^0
		unitList.add("十");// 10^1
		unitList.add("百");// 10^2
		unitList.add("千");// 10^3
		unitList.add("万");// 10^4
				
		int result = 0;
		int temp=0;
		for(char c : str.toCharArray()){
			int i = numList.indexOf(String.valueOf(c));
			int j = unitList.indexOf(String.valueOf(c));
			if(i >=0){
				temp += i;
			}
			
			if(j >=0 ){
				result += temp * (int)Math.pow(10, j) ;
				temp=0;
			}
		}
		
		System.out.println("结果是"+result);
		
	}
}
