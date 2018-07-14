import java.io.*;
import java.lang.*;

public class  HexadecimalToDecimal{
	public static void main(String[] args) throws IOException{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the Hexadecimal number:");
		String str= bf.readLine();
		int i= Integer.parseInt(str,16);
		System.out.println("Decimal:="+ i);
	}
} 