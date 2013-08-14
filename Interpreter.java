/**
This program is an Interpreter for a scripting language assignment from CS 3320 at 
Georgia State University, Summer 3013. The assignment is design the outline of a 
scripting language with some basic commands and syntax. This program will take a text 
file and read it and implement commands for the language. This file will interpret
commands based on Commodore_BASIC. An outline is documented in a seperate file
*/

import java.util.*;
import java.io.*;

public class Interpreter
{
	public static void main(String[] args)throws IOException
	{
		FileReader freader = new FileReader (args[0]);
		BufferedReader inputFile = new BufferedReader (freader);
		Scanner keyboard = new Scanner(System.in);
		String line = inputFile.readLine();								//reading first line from script file
		StringTokenizer strToken = new StringTokenizer(line);    
		String token;															//will hold command line #, first Token
		String token2 = "";													//will hold script command, 2nd Token
		boolean status = false;												//assists in control of IF/ELSE statements
		
		Map<String, String> variables = new HashMap<String, String>();  //Where variables from script are stored
		//inputFile.mark(3);													//For the GOTO script command
		
		//Start of reading script file line by line
		do
		{
			token2 = "";														//cleanup token2 variable, so following 'if' is not impacted
			if (strToken.hasMoreTokens())									//check line to see if at the end
			{
				token = strToken.nextToken();								//skipping command line #
				if (strToken.hasMoreTokens())								//check line to see if at the end
				{
					token2 = strToken.nextToken();						//assign script command to a String for ability to compare
				}
			}
			if (token2.equals("PRINT"))									//checking for script command
			{
				print(line, variables);										//call to print, sending command line and variable list to print method
			}
			else if (token2.equals("INPUT"))								//checking for script command
			{
				String var = input(line);									//retrieve script's variable name assignment with input method
				String input = keyboard.nextLine();						//store user's input
				variables.put(var, input);									//assign key and value based on script's variable assignment
			}
			else if (token2.equals("MATH"))								//checking for script command
			{
				String result = math(line, variables);					//math method will return  result of postfix math expression
				String var = input(line);									//the script's designated variable 
				variables.put(var, result);								//assign key and value based on script's variable assignment	
			}
			else if (token2.equals("SORT"))								//checking for script command
			{
				String result = sort(line, variables);					//sort method will return result of  call to sort 
				String var = input(line);									//the script's designated variable
				variables.put(var, result);								//assign key and value based on script's variable assignment
			}
			else if (token2.equals("IF"))									//checking for script command
			{
				status = ifFunc(line, variables);						//checking if statement, TRUE or FALSE
				while (!status)												//if statement false, while loop passes over code
				{																	//associated to if statement being true
					line = inputFile.readLine();
					if (line != null)
					{
						strToken = new StringTokenizer(line);			
					}
					strToken.nextToken();									//looking for ELSE statement so to skip over items associated with IF
					if (strToken.nextToken().equals("ELSE"))
					{
						status = true;
					}
				}
			
			}
			else if (token2.equals("ELSE"))								//checking for script command
			{
				if (!status)													//if IF command  was false, do nothing but fall through rest of code
				{
					
				}
				else																//if IF was true this will skip over steps associated with ELSE
				{
					while (status)
				{
					line = inputFile.readLine();
					inputFile.mark(3);
					if (line != null)
					{
						strToken = new StringTokenizer(line);
					}
					strToken.nextToken();
					if (strToken.nextToken().equals("END"))
					{
						status = false;
					}
				}

				}
			}
			else if (token2.equals("KITTY"))									//checking for script command
			{
				String input = keyboard.nextLine();
				String fileName = strToken.nextToken();
				if (variables.containsKey(fileName.substring(0,fileName.length()-1)))		//looking for variable entered by user
				{
					fileName = variables.get(fileName.substring(0, fileName.length()-1));
				}
				else
				{
					fileName = "file.txt";										//sets fileName if users does not provide one.
				}
				
				while (!input.equals("-1"))									
				{
					kitty(input,fileName, variables);
					input = keyboard.nextLine();
				}
			}
	  
			line = inputFile.readLine();										//read next line of file for next run through do loop, if needed
			if (line != null)														//checking to see if at EOF
			{
				strToken = new StringTokenizer (line);						//sets next line so it can be Tokenized
			}
		}while (strToken.hasMoreTokens() || line != null);			
		
	}	
	/**
		The print method will remove line # and command text and out put scripts output
		@param s The line of code that contains the PRINT command
		@param variables The Map of variables associated with running script
	*/
	static void print(String s, Map<String,String> variables)
	{
		StringTokenizer inputStr = new StringTokenizer(s);
		String output= "";													//String to be outputted, will be concatenated 
		inputStr.nextToken(); 												//skipping command line number
		inputStr.nextToken();												//skipping PRINT command text
		while (inputStr.hasMoreTokens())
		{
			String temp = inputStr.nextToken();							//set temp to next "word" on line
			if (temp.charAt(temp.length()-1) == '$')					//checking to see if Token is a variable
			{
				temp = variables.get(temp.substring(0, temp.length()-1));		//retrieve value of variable
			}
			output = output + temp + " ";									//concatenate output string
		}
		System.out.println(output);										
		
	}
	/**
		The input method finds the variable name assigned by script being read
		@param s The line of code containing a variable
		@return The variable name assigned by scirpt		
	*/
	static String input(String s)
	{
		StringTokenizer inputStr = new StringTokenizer(s);
		String var = "";
		while (inputStr.hasMoreTokens())
		{
			String temp = inputStr.nextToken();
			if (temp.charAt(0) == '$')
			{
				var = temp.substring(1);
			}
		}
		return var;
			
	}
	/**
		The math method concatenates a string in postfix mode using script input and values of varialbes
		and sends the String to the postfix method
		@param s The line of script code to read
		@param variables The Map of variables associated with running script
		@return value as String of postfix equation
	*/
	static String math(String s, Map<String, String> variables)
	{
		String postfixEqu="";												//String built to send to postfix method
		StringTokenizer inputStr = new StringTokenizer(s);
		inputStr.nextToken(); 												//skipping command line number
		inputStr.nextToken();												//skipping MATH command text
		
		
		while (inputStr.hasMoreTokens())
		{
			
			String temp = inputStr.nextToken();
			if (temp.charAt(temp.length()-1) == '$')					//check to see if looking at a variable
			{
				String x = variables.get(temp.substring(0, temp.length()-1)); 		//retrieve variable value
				postfixEqu = postfixEqu + x + " ";						//add variable to a String
			}
			else if (temp.equals("+") || temp.equals("-") || temp.equals("*") || temp.equals("/") || temp.equals("^")) //check for arithmetic operators
			{
				postfixEqu = postfixEqu + temp + " ";
			}
			else if (isNumber(temp))										//check to see if looking at a number, and not a variable or operator
			{
				postfixEqu = postfixEqu + temp + " ";			
			}
		}
		return postfix(postfixEqu); 										//return the result of postfix equation from postfix method
	}
	/**
		The postfix method solves a postfix equation
		@param s A String in the form of a postfix equation
		@return A String that is the result of given postfix equation 
	*/
	static String postfix(String s)
	{
		StringTokenizer inputStr = new StringTokenizer(s);
		Stack<String> integers = new Stack<String>();
		String total = "";
		
		while (inputStr.hasMoreTokens())
		{
			String temp = inputStr.nextToken();
			if (isNumber(temp))
			{
				integers.push(temp);
			}
			else if(temp.equals("+"))
			{
				total = Integer.toString(Integer.parseInt(integers.pop()) + Integer.parseInt(integers.pop()));
				integers.push(total);
			}
			else if(temp.equals("-"))
			{
				total = Integer.toString(Integer.parseInt(integers.pop()) - Integer.parseInt(integers.pop()));
				integers.push(total);
			}
			else if(temp.equals("*"))
			{
				total = Integer.toString(Integer.parseInt(integers.pop()) * Integer.parseInt(integers.pop()));
				integers.push(total);
			}
			else if(temp.equals("/"))
			{
				total = Integer.toString(Integer.parseInt(integers.pop()) / Integer.parseInt(integers.pop()));
				integers.push(total);
			}
			else if (temp.equals("^"))
			{
				int j = Integer.parseInt(integers.pop());
				int k = Integer.parseInt(integers.pop());
				int num = k;
				for (int i = 1; i < j; i++)
				{
					num = num * k;
				}
				total = Integer.toString(num);
				integers.push(total);
			}
			
		}
		return total;
	
	}
	/**
		The sort method sorts an array of numbers
		@param s A line of code
		@param variables The Map of variables associated with running script
		@return The result of a postfix equation, in String form
	*/
	
	static String sort(String s, Map<String, String> variables)
	{
		String result = "";
		StringTokenizer inputStr = new StringTokenizer(s);
		
		inputStr.nextToken(); 			//skipping command line number
		inputStr.nextToken();			//skipping MATH command text
		String numString ="";
		
		while (inputStr.hasMoreTokens())
		{
			
			String temp = inputStr.nextToken();
			if (temp.charAt(temp.length()-1) == '$')
			{
				String x = variables.get(temp.substring(0, temp.length()-1));
				numString = numString + x + " ";
			}
		}
		inputStr = new StringTokenizer(numString);
		List<Integer> numbers = new ArrayList<Integer>();
		int i = inputStr.countTokens();
		//int[] numbers = new int[i];
		while (inputStr.hasMoreTokens())
		{
			
			numbers.add(Integer.parseInt(inputStr.nextToken()));
			
		
		}
		Collections.sort(numbers);
		for (int j = 0; j < i ; j++)
		{
			result = result + numbers.get(j) + " ";
		
		}
		return result;
	}
	/**
		The ifFunc determines determines truth or falsity of statement from script
		@param s Line of script code
		@param variables Map of variables associated with running script
		@return A boolean result of if an equality statement is true or false
	*/
	static boolean ifFunc(String s, Map<String,String> variables)
	{
		StringTokenizer inputStr = new StringTokenizer(s);
		
		inputStr.nextToken(); 			//skipping command line number
		inputStr.nextToken();			//skipping IF command text
		String A = inputStr.nextToken();
		String operator = inputStr.nextToken();
		String B = inputStr.nextToken();
		
		if (A.charAt(A.length()-1) == '$' && variables.containsKey(A.substring(0,A.length()-1)))
		{
			A = variables.get(A.substring(0,A.length()-1));
		}
		if (B.charAt(B.length()-1) == '$' && variables.containsKey(B.substring(0,B.length()-1)))
		{
			B = variables.get(B.substring(0,B.length()-1));
		}
		int result = A.compareTo(B);
		if (operator.equals(">"))
		{
			if (result > 0)
			{
				return true;
			}
		}
		if (operator.equals("<"))
		{
			if (result < 0)
			{
				return true;
			}
		}
		if (operator.equals("="))
		{
			if (result == 0)
			{
				return true;
			}
		}
		return false;

	}
	/**
		kitty allows a user to enter the name of txt files and will concatonate the files
		The user can enter their own name for a file, if it exists, data will be appended
		If file does not exist, it will be created.  If no file name is entered the new file
		will be saved as file.txt
		@param input User's existing file that will be added to new file
		@param fileName Name of file data will be save d to.
		@param variables Map of variables associated with running script
	*/
	static void kitty(String input,String fileName, Map<String, String> variables) throws IOException
	{
		FileWriter fwriter = new FileWriter(fileName, true);
		PrintWriter outputFile = new PrintWriter(fwriter);
		FileReader fileReader = new FileReader(input);
		BufferedReader inputText = new BufferedReader(fileReader);
		String fileLine = inputText.readLine();
		
		while (fileLine != null)
		{
			outputFile.println(fileLine);
			fileLine = inputText.readLine();
		
		} 	
		outputFile.close();
	}
	/**
		isNumber checks a String to see if it is a number
		@param str A String that is a Token from a line of code from associated script.
		@return A boolean of whether a String is a number or not.
	*/
	public static boolean isNumber(String str)  
	{  
  		try  
  		{  
    		double d = Double.parseDouble(str);  
  		}  
  		catch(NumberFormatException nfe)  
 		{  
    		return false;  
  		}  
  		return true;  
	}
	
		

}