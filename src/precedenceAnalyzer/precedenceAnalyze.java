package precedenceAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

public class precedenceAnalyze {
													//每行依次为+,-,*,/,(,),i,#
	private final static char[][] precedence = new char[][]{{'>','>','<','<','<','>','<','>'},
													{'>','>','<','<','<','>','<','>'},
													{'>','>','>','>','<','>','<','>'},
													{'>','>','>','>','<','>','<','>'},
													{'<','<','<','<','<','=','<',' '},
													{'>','>','>','>',' ','>',' ','>'},
													{'>','>','>','>',' ','>',' ','>'},
													{'<','<','<','<','<',' ','<','='}};
	public static int[][] func = new int[8][2];
	
	public static void main(String[] args) {
		if (!madePrecedence(precedence)) {
			System.out.println("构造优先函数失败。退出程序");
			System.exit(0);
		} else {
			System.out.println("优先函数构造成功。");
			for(int i = 0; i < 2; i++) {
				for (int j = 0; j < 8; j++) {
					System.out.print(func[j][i]+" ");					
				}
				System.out.println();
			}
		}
		//构造对照表
		HashMap<String, Integer> keyvalue = new HashMap<>();
		keyvalue = readFileByLines("keyword.txt");
		//判断表达式
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("请输入一个表达式:");
		while(input.hasNext())
		{
			String[] innerNum = fenxi(input.nextLine(), keyvalue).concat("#").split(" ");
			ArrayDeque<String> exp = new ArrayDeque<>();
			for(int i = innerNum.length-1;i >= 0;i--)
			{
				exp.push(innerNum[i]);
			}
			if (useFuncAnalyze(exp)) {
				System.out.println("使用优先分析函数分析成功。");
			}
		}
	}
	
	/*
	 * 用优先函数分析表达式
	 * 
	 */
	public static boolean useFuncAnalyze(ArrayDeque<String> exp)
	{
		boolean isPush = true;
		String sym = null;
		System.out.println(exp);
		ArrayDeque<String> pushDownStack = new ArrayDeque<>();//下推栈
		pushDownStack.push("#");
		ArrayDeque<Integer> opnd = new ArrayDeque<>();//算量栈
		boolean flag = true;//循环条件
		while(flag)
		{
			//System.out.println(1);
			sym = exp.getFirst();					
			if (!exp.isEmpty()) {
				if (pushDownStack.getFirst().equals("#") && sym.equals("#")) {
					//匹配成功
					flag = false;
					System.out.println("匹配成功");
				} else if (pushDownStack.getFirst().equals("19") && sym.equals("20")) {
					pushDownStack.pop();//将（出栈
					isPush = true; //相当于将）入栈
				} else if (sym.equals("9") || sym.equals("10")) {
					opnd.push(Integer.parseInt(sym));
					isPush = true;
				} else if (precedence[getIndex(pushDownStack.getFirst())][getIndex(sym)] == '<') {
					pushDownStack.push(sym);
					isPush = true;
				} else if (precedence[getIndex(pushDownStack.getFirst())][getIndex(sym)] == '>') {
					int a = opnd.pop();
					int b = opnd.pop();
					String op = pushDownStack.pop();
					opnd.push(calc(a, b, op));
					isPush = false;
				} else {
					System.out.println("发生错误，在分析过程中。退出程序");
					System.exit(0);
				}
				if (isPush) {
					exp.pop();
				}
//				if (pushDownStack.getFirst().equals("#") && sym.equals("#")) {
//					//匹配成功
//					flag = false;
//				}
//				else if (pushDownStack.getFirst().equals("19") && sym.equals("20")) {
//					pushDownStack.pop();
//				}
//				else if (sym.equals("9") || sym.equals("10")) {
//					opnd.push(Integer.parseInt(sym));
//				}
//				else if (func[getIndex(pushDownStack.getFirst())][0] < func[getIndex(sym)][1]) {
//					//移进
//					pushDownStack.push(sym);
//				}
//				else if (func[getIndex(pushDownStack.getFirst())][0] > func[getIndex(sym)][1]) {
//					//归约
//					int a = opnd.pop();
//					int b = opnd.pop();
//					String op = pushDownStack.pop();
//					opnd.push(calc(a, b, op));
//				} else {
//					System.out.println("发生错误。在直观算符优先分析法中。");
//					System.exit(0);
//				}
//			} else {
//				System.out.println("发生未知错误。在表达式输入完成时，没有分析完成。");
//				System.exit(0);
//			}
			} else {
				System.out.println("输入带为空。退出程序");
				System.exit(0);
			}
		}
		System.out.println("记值分析结果为:"+opnd.pop());
		return true;
	}	
	public static int calc(int a, int b, String op)
	{
		if (op.equals("11")) {
			return b+a;
		}
		else if (op.equals("12")) {
			return b-a;
		}
		else if (op.equals("13")) {
			return b*a;
		}
		else if (op.equals("14")) {
			if (a == 0) {
				System.out.println("除以0错误！");
				System.exit(0);
				return -1;
			}
			else {
				return b/a;
			}
		}
		else {
			System.out.println("运算符错误:"+change(op));
			System.exit(0);
			return -1;
		}
	}
	
	/*
	 * 将类号转换为字符
	 * 
	 */
	public static String change(String aString)
	{
		HashMap<String, Integer> keyvalue = new HashMap<>();
		keyvalue = readFileByLines("keyword.txt");
		Set<Entry<String, Integer>> set = keyvalue.entrySet();
		Iterator<Entry<String, Integer>> iterator = set.iterator();
		while (iterator.hasNext()) {
			HashMap.Entry<String, Integer> entry = (HashMap.Entry<String, Integer>)iterator.next();
			if (entry.getValue().equals(Integer.parseInt(aString))) {
				return (String)entry.getKey();
			}
		}
		return "未找到此类号代表的关键字。";
	}
	
	/*
	 * 获得栈的第二个元素
	 * 
	 */
	public static String getSecString(ArrayDeque<String> stack)
	{
		String aString = stack.pop();
		if (!aString.equals("")) {
			String bString = stack.getFirst();
			if (!bString.equals("")) {
				stack.push(aString);
			}
			return bString;
		}
		return "";
	}
	
	/*
	 * 跟据运算符的值计算其在优先函数中的下标
	 * 
	 */
	public static int getIndex(String op)
	{
		//每行依次为+,-,*,/,(,),i,#
		if (op.equals("11")) {
			return 0;
		}
		else if (op.equals("12")) {
			return 1;
		}
		else if (op.equals("13")) {
			return 2;
		}
		else if (op.equals("14")) {
			return 3;
		}
		else if (op.equals("19")) {
			return 4;
		}
		else if (op.equals("20")) {
			return 5;
		}
		else if (op.equals("9") || op.equals("10")) {
			return 6;
		}
		else if (op.equals("#")) {
			return 7;
		}
		else {
			System.out.println("下标转换错误。");
			return -1;
		}
	}
	
	/*
	 * 判断func[][]的最大值
	 * 
	 */
	public static int maxf(int[][] func)
	{
		int a = 0;
		for(int i = 0;i < 8;i++)
		{
			for(int j = 0;j < 2;j++)
			{
				if (a < func[i][j]) {
					a = func[i][j];
				}
			}
		}
		return a;
	}
	
	/*
	 * 判断两个数大小
	 * 
	 */
	public static int max(int a, int b)
	{
		return a > b?a:b;
	}
	
	/*
	 * 构造优先分析函数
	 * 
	 */
	public static boolean madePrecedence(char[][] precedence)
	{
		boolean judge = true;
		int m = 0;
		for(int i = 0;i < 8;i++)
		{
			for(int j = 0;j < 2;j++)
			{
				func[i][j] = 1;
			}
		}
		while (judge) {
			m = 0;
			for(int i = 0;i < 8;i++)
			{
				for(int j = 0;j < 8;j++)
				{
					if ((precedence[i][j] == '>') && (func[i][0] <= func[j][1])) {
						func[i][0] = func[j][1]+1;
						m = 1;
					}
					else if ((precedence[i][j] == '<') && (func[i][0] >= func[j][1])) {
						func[j][1] = func[i][0]+1;
						m = 1;
					}
					else if ((precedence[i][j] == '=') && (func[i][0] != func[j][1])) {
						func[i][0] = max(func[i][0],func[j][1]);
						func[j][1] = func[i][0];
						m = 1;
					}
					else if (precedence[i][j] == ' ') {
					}
					else {
						//judge = false;
					}
				}
			}
			if (maxf(func) >= 17) {
				System.out.println("优先函数不存在。");
				for(int i = 0;i < 8;i++)
				{
					for(int j = 0;j < 2;j++)
					{
						System.out.print(func[i][j]+" ");						
					}
					System.out.println();
				}
				return false;
			}
			if (m != 1) {
				judge = false;;
			}
		}
		return true;
	}
	
	/*
	 * 以行为单位读取文件
	 * 读取类号的文件操作
	 * 
	 */
 	public static HashMap<String, Integer> readFileByLines(String filename)
	{
		File file = new File(filename);
		BufferedReader reader = null;
		HashMap<String, Integer> keyvalue = new HashMap<String, Integer>();
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while((tempString = reader.readLine()) != null)
			{
				String[] line = tempString.split(" ");
				try
				{
					int a = Integer.parseInt(line[0]);
					keyvalue.put(line[1], a);
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return keyvalue;
	}
	
	/*
	 * 分析过程
	 * 
	 */
	public static String fenxi(String string, HashMap<String, Integer> keyvalue)
	{
		String returnString = new String();
		int a = 0;
		boolean judge = false;
		
		String str = new String();
		for(int i = 0;i < string.length();i++)
		{
			if(isLetter(string.charAt(i)))
			{
				str = str.concat(String.valueOf(string.charAt(i)));
			}
			else if (isNumber(string.charAt(i))) {
				int n = i+1;
				for(;n < string.length();i++)
				{
					if (!isNumber(string.charAt(n))) {
						break;
					}
				}
				if (n == i+1) {
					returnString = returnString.concat(String.valueOf(10)+' ');
				}
				else {
					//String number = string.substring(i, n);
					returnString = returnString.concat(String.valueOf(10)+' ');
					i = n-1;
				}
			}
			else if (string.charAt(i) == ' ') {
				if (!str.equals("") && (a = searchHashMap(str, keyvalue)) != 0) {
					returnString = returnString.concat(String.valueOf(a)+' ');
				}
				else if (isVariable(str)) {
					returnString = returnString.concat(String.valueOf(9)+' ');
				}
				else {
					for(int i1 = 0;i1 < str.length();i1++)
					{
						if (isNumber(str.charAt(i1))) {
							judge = true;
						}
						else {
							judge = false;
							System.out.println("不能识别:"+str);
						}
					}
					if (judge) {
						judge = false;
						returnString = returnString.concat(String.valueOf(10)+' ');
					}
					str = "";
				}
			}
			else if (isOperator1(string.charAt(i))) {
				if (!str.equals("") && (a = searchHashMap(str, keyvalue)) != 0) {
					returnString = returnString.concat(String.valueOf(a)+' ');
				}
				else if (isVariable(str)) {
					returnString = returnString.concat(String.valueOf(9)+' ');
				}
				else {
					for(int i3 = 0;i3 < str.length();i++){
						if (isNumber(str.charAt(i3))) {
							judge = true;
						}
						else {
							judge = false;
							System.out.println("不能识别:"+str);
						}
					}
					if (judge) {
						judge = false;
						returnString = returnString.concat(String.valueOf(10)+' ');
					}
				}
				if (i != string.length()-1 && isOperator2(string.charAt(i+1))) {
					String string2 = string.substring(i, i+2);
					if ((a = searchHashMap(string2, keyvalue)) != 0) {
						returnString = returnString.concat(String.valueOf(a)+' ');
					}
					else {
						System.out.println("发生错误。");
					}
				}
				returnString = returnString.concat(String.valueOf(searchHashMap(String.valueOf(string.charAt(i)), keyvalue))+' ');
				str = "";
			}
		}
		return returnString;
	}
	
	/*
	 * searchHashMap
	 * 搜索HashMap中的value
	 * 
	 */
	public static int searchHashMap(String string, HashMap<String, Integer> keyvalue)
	{
		Object object = null;
		int a = 0;
		if((object = keyvalue.get(string)) != null)
		{
			a = (int)object;
		}
		return a;
	}
	
	/*
	 * 判断是否是变量名
	 * 
	 */
	public static boolean isVariable(String string)
	{
		if (string.equals("")) {
			return false;
		}
		if (isLetter(string.charAt(0)) || string.charAt(0) == '_') {
			for(int i = 1;i < string.length();i++)
			{
				if (!(isLetter(string.charAt(i)) || isNumber(string.charAt(i)) || string.charAt(i) == '_')) {
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * isLetter
	 * 判断是否为字母
	 * 
	 */
	public static boolean isLetter(char ch)
	{
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * isNumber
	 * 判断是否是数字
	 * 
	 */
	public static boolean isNumber(char ch)
	{
		if (ch >= '0' && ch <= '9') {
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * isOperator1
	 * 判断是否为运算符
	 * 
	 */
	public static boolean isOperator1(char ch)
	{
		if (ch == '=' || ch == '<' || ch == '>') {
			return true;
		}
		else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
			return true;
		}
		else if (ch == '!' || ch == '|' || ch == '%' || ch == '&') {
			return true;
		}
		else if (ch == '(' || ch == ')' || ch == '{' || ch == '}') {
			return true;
		}
		else if (ch == ';') {
			return true;
		}
		return false;
	}
	
	/*
	 * isOperator2
	 * 判断第二个字符是否是运算符
	 * 
	 */
	public static boolean isOperator2(char ch)
	{
		if (ch == '=' || ch == '<' || ch == '>') {
			return true;
		}
		else if (ch == '+' || ch == '-') {
			return true;
		}
		else if (ch == '|' || ch == '&') {
			return true;
		}
		else if (ch == ';') {
			return true;
		}
		return false;
	}
}