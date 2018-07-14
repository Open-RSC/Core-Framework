import java.io.*;
import java.lang.*;

public class  DecimalToHexadecimal{
	public static void main(String[] args) throws IOException{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the decimal value:");
		String hex = bf.readLine();
		int i = Integer.parseInt(hex);
		String hex1 = Integer.toHexString(i);
		System.out.println("Hexadecimal:="+ hex1);
	}
} 