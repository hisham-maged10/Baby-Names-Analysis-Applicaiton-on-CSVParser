/**
 * @author ${hisham_maged10}
 *https://github.com/hisham-maged10
 * ${DesktopApps}
 */
import java.util.ArrayList;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static java.lang.System.out;
import static java.lang.System.err;
//new method in csvparser needs testing
public class TestBabyNames
{
	public static void main(String[] args)
	{
		testing();
	}
	public static BigInteger getTotalNumberOfGenderBirths(CSVParser csvParser,Choice choice)
	{
		MutableInteger startIndex=new MutableInteger();
		MutableInteger endIndex=new MutableInteger();
		String[][] data=getData(extractYearFromFileName(csvParser),startIndex,endIndex,choice);
		BigInteger total=BigInteger.ZERO;
		for(int i=startIndex.getIntegerValue(),n=endIndex.getIntegerValue();i<n;i++)
		{
			total=total.add(new BigInteger(data[i][2]));
		}
	
		return total;
	}
	public static BigInteger getTotalNumberOfGenderBirths(CSVParser csvParser,String name,Choice choice)
	{
		MutableInteger startIndex=new MutableInteger();
		MutableInteger endIndex=new MutableInteger();
		int year=extractYearFromFileName(csvParser);
		String[][] data=getData(year,startIndex,endIndex,choice);
		int rank=getRank(name,year,choice);
		int temp;
		BigInteger total=BigInteger.ZERO;	
		for(int i=startIndex.getIntegerValue(),n=endIndex.getIntegerValue();i<n && rank!=-1;i++)
		{
			if((rank>(temp=getRank(data[i][0],year,choice))) && temp!=-1)
			{
				total=total.add(new BigInteger(data[i][2]));
			}
			else if(rank==temp) break;
		}
		return (rank!=-1)?total:new BigInteger("-1");
	}
	public static int getFirstMaleIndex(CSVParser csvParser)
	{
		int index=-1;
		for(String[] e:csvParser.getCsvInfoArray())
		{
			if(index==-1)index++;
			if(e[1].equalsIgnoreCase("m"))
			return index;
			index++;	
		}	
		return index;
	}
	public static int countNames(CSVParser csvParser,Choice choice)
	{
		int count=0;
		switch(choice)
		{
			
			case MALE:count=csvParser.getCsvInfoArray().length-getFirstMaleIndex(csvParser);break;
			case FEMALE:count=getFirstMaleIndex(csvParser);break;
		}
		return count;
	}
	public static BigInteger getTotalNumberOfBirths(CSVParser csvParser)
	{
		return getTotalNumberOfGenderBirths(csvParser,Choice.MALE).add(getTotalNumberOfGenderBirths(csvParser,Choice.FEMALE));
	}
	public static int getRank(String name,int year,Choice choice)
	{
		MutableInteger startIndex=new MutableInteger();
		MutableInteger endIndex=new MutableInteger();
		String[][] data=getData(year,startIndex,endIndex,choice);
		for(int i=startIndex.getIntegerValue(),n=endIndex.getIntegerValue();i<n;i++)
		{
			if(name.equalsIgnoreCase(data[i][0]))
			return ((i-startIndex.getIntegerValue())+1);
		}
		return -1;
	}
	public static String getName(int rank,int year,Choice choice)
	{		
		rank--;
		MutableInteger startIndex=new MutableInteger();
		MutableInteger endIndex=new MutableInteger();
		String[][] data=getData(year,startIndex,endIndex,choice);	
		return validateRank(rank,endIndex.getIntegerValue())?data[startIndex.getIntegerValue()+rank][0]:"";
	}
	public static String getNameInYear(String name,int year,int newYear,Choice choice)
	{
		int neededRank=getRank(name,year,choice);
		String nameGivenRank=getName(neededRank,newYear,choice);
		return nameGivenRank;
	}
	
	public static int getYearOfHighestRank(String name,Choice choice)
	{
		ArrayList<CSVParser> csvArr=CSVParser.makeArrayList(false);
		int j=0;
		int highestRank=-1;
		do{	highestRank=getRank(name,extractYearFromFileName(csvArr.get(j++)),choice); } while(highestRank==-1);
		int temp;
		int neededYear=extractYearFromFileName(csvArr.get(j));
		String[][] data;
		for(int i=j,n=csvArr.size();i<n;i++)
		{
			System.out.println("Checked "+i+" files");
			data=csvArr.get(i).getCsvInfoArray();
			if((highestRank>(temp=getRank(name,extractYearFromFileName(csvArr.get(i)),choice)))&& temp!=-1)
			{
				highestRank=temp;
				neededYear=extractYearFromFileName(csvArr.get(i));
			}
		}
		return neededYear;
	}
	public static double getAverageOfNameYears(String name,Choice choice)
	{
		ArrayList<CSVParser> csvArr=CSVParser.makeArrayList(false);
		double sumRank=0.0;
		int count=0;
		String[][] data;
		for(int i=0,n=csvArr.size();i<n;i++)
		{	
			System.out.println("Checked "+(i+1)+" files");
			data=csvArr.get(i).getCsvInfoArray();
			sumRank+=getRank(name,extractYearFromFileName(csvArr.get(i)),choice);
			count++;
		}
		return (count>0)?(sumRank/count):-1.0;	
	}
	public static int extractYearFromFileName(CSVParser csvParser)
	{
		try{
		return Integer.parseInt(csvParser.getCsvFileName().substring(csvParser.getCsvFileName().indexOf("b")+1,csvParser.getCsvFileName().lastIndexOf(".")));	
		}	
		catch(NumberFormatException ex)
		{
			return Integer.parseInt(csvParser.getCsvFileName().substring(csvParser.getCsvFileName().indexOf("b")+1,csvParser.getCsvFileName().lastIndexOf("s.")));		
		}
	}
	public static String[][] getData(int year,MutableInteger startIndex,MutableInteger endIndex,Choice choice)
	{
		CSVParser csvParser;
		try
		{csvParser= new CSVParser("us_babynames_by_year/yob"+Integer.toString(year)+".csv",false);}
		catch(Exception ex){csvParser= new CSVParser("us_babynames_by_decade/yob"+Integer.toString(year)+".csv",false);}
		String[][] data=csvParser.getCsvInfoArray();
		switch(choice)
		{
			case MALE:startIndex.setIntegerValue(getFirstMaleIndex(csvParser));endIndex.setIntegerValue(data.length);break;
			case FEMALE:startIndex.setIntegerValue(0);endIndex.setIntegerValue(getFirstMaleIndex(csvParser));break;
		}
		return data;
	}
	public static boolean validateRank(int rank,int endIndex)
	{
		return (rank>=0 && rank<endIndex)?true:false;
	}
	
	public static void testing()
	{
		CSVParser csvParser=new CSVParser(false);
		try{
		guiController(csvParser);
		}catch(NoSuchElementException ex){System.err.println("Termination by another souce, Exiting");System.exit(0);}
	}
	public static void guiController(CSVParser csvParser)
	{	
		boolean execute=true;
		do
		{
			gui();
			execute=doOperation(csvParser);
			for(int i=0;i<15;i++) out.print("-"); out.println();
		}while(execute);
	}
	public static boolean doOperation(CSVParser csvParser)
	{
		Scanner input=new Scanner(System.in);
		int choice=0;
		try{choice=Integer.parseInt(input.nextLine());}catch(NumberFormatException ex){out.println("IncorrectResponse");guiController(csvParser);}	
		switch(choice)
		{
			case 1:printGenderNamesCount(csvParser);break;
			case 2:printTotalNoOfBirths(csvParser);break;
			case 3:printNoOfGenderBirths(csvParser);break;
			case 4:printNoOfGenderBirthsRankedHigher(csvParser);break;
			case 5:printRank();break;
			case 6:printRankName();break;
			case 7:printNameInYear();break;
			case 8:printHighestYearForName();break;
			case 9:printAverageRankForName();break;
			case 10:return false;
			default:out.println("Incorrect input!");
		}
		return continueOperation();
	}
	private static boolean continueOperation()
	{
		out.print("Do you want to Reuse the program? (y/n)");
		switch(new Scanner(System.in).nextLine().toLowerCase().charAt(0))
		{
			case 'y':
			case '1':return true;
			case 'n':
			case '0':return false;
			default:out.println("Incorrect response"); return continueOperation();
		}
		
	}
	public static void gui()
	{
		out.print("\nBaby Names Analysis (Application on CSVParser) Version 1.0\n-----------------------------------------------------------\n"+
		"1.Print total number of names\n"+
		"2.Print Total number of Births\n"+
		"3.Print total number of Specific Gender Births\n"+
		"4.Print the Number of births that have higher rank than the name specified (will specify name)\n"+
		"5.Find a rank of a name in certain year (will specify name, year needed)\n"+
		"6.Print Name of a certain Rank (will specify rank, year needed)\n"+
		"7.What would your name be in a different year? (According to Rank) (will specify name, year of birth, year needed)\n"+
		"8.Which year(of many) a certain name had it's own highest rank? (will specify name)\n"+
		"9.Get average the rank of a Name in many years (will specify name)\n"+
		"10.Exit Program\n"+
		"Enter your Choice: ");
	}	
	public static Choice getChoice()
	{
		System.out.println("1.Male\n2.Female");
		
		switch(new Scanner(System.in).nextLine().toLowerCase().charAt(0))
		{
			case '1':
			case 'm': return Choice.MALE;
			case '2':
			case 'f': return Choice.FEMALE;
			default:System.err.println("Incorrect Response"); return getChoice();
		}
	}
	public static String getName()
	{
		String name="";
		System.out.print("Enter a name: ");
		try{ name=new Scanner(System.in).nextLine();}catch(NoSuchElementException ex){System.err.println("Termination by another souce, Exiting");System.exit(0);}
		return name;
	}
	public static int getYear()
	{
		int year=-1;
		System.out.print("Enter a Year (1880-2014): ");
		try{ year=Integer.parseInt(new Scanner(System.in).nextLine());
			if(year>2014 || year<1880)
			{out.println("Incorrect response"); return getYear();}
	
			}catch(NumberFormatException ex){System.out.println("Incorrect Response"); return getYear();}
		catch(NoSuchElementException ex){System.err.println("Termination by another souce, Exiting");System.exit(0);}
		return year;
	}
	public static int getRank()
	{
		int rank=-1;
		System.out.print("Enter a Rank: ");
		try{ rank=Integer.parseInt(new Scanner(System.in).nextLine());
			if(rank<1 )
			{out.println("Incorrect response"); return getRank();}

		}catch(NumberFormatException ex){System.out.println("Incorrect Response"); return getYear();}
		catch(NoSuchElementException ex){System.err.println("Termination by another souce, Exiting");System.exit(0);}
		return rank;	
	}
	public static void printTotalNoOfBirths(CSVParser csvParser)
	{
		System.out.println("Total Number: "+getTotalNumberOfBirths(csvParser));
	}
	public static void printNoOfGenderBirths(CSVParser csvParser)
	{
		Choice choice=getChoice();
		System.out.println(choice.toString().toLowerCase()+": "+getTotalNumberOfGenderBirths(csvParser,choice));
	}
	public static void printNoOfGenderBirthsRankedHigher(CSVParser csvParser)
	{
		Choice choice=getChoice();
		System.out.println(choice.toString().toLowerCase()+": "+getTotalNumberOfGenderBirths(csvParser,getName(),choice));
	}
	public static void printRank()
	{
		Choice choice=getChoice();
		String name=getName();
		int year=getYear();
		int rank=getRank(name,year,choice);
		if(rank!=-1)
		System.out.println("Rank: "+rank);
		else
		System.out.println("Name Doesn't exist in records!");	
	}
	public static void printRankName()
	{
		Choice choice=getChoice();
		int year=getYear();
		int rank=getRank();
		String name=getName(rank,year,choice);
		if(!name.isEmpty())
		System.out.println("Name: "+name);
		else
		System.out.println("rank doesn't exist in records");
	}
	public static void printGenderNamesCount(CSVParser csvParser)
	{
		Choice choice=getChoice();
		System.out.println(choice.toString().toLowerCase()+":"+countNames(csvParser,choice));
	}
	public static void printNameInYear()
	{
		Choice choice=getChoice();
		String name=getName();
		int year=getYear();
		int newYear=getYear();
		System.out.println(name+" born in "+year+" would be "+getNameInYear(name,year,newYear,getChoice())+" if she was born in "+newYear+".");		
	}
	public static void printHighestYearForName()
	{
		Choice choice=getChoice();
		String name=getName();
		System.out.println("Name: "+name+" had it's own highest rank at "+getYearOfHighestRank(name,choice));
	}
	public static void printAverageRankForName()
	{
		Choice choice=getChoice();
		String name=getName();
		System.out.println("Name: "+name+" had an Average rank of: "+getAverageOfNameYears(name,choice));
	}

}