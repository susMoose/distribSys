import java.util.Scanner;

public class R4 {

	public static void main(String[] args) {
		int A, B, C;
		double positiveRoot, negativeRoot;
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter A: ");
		A = sc.nextInt();
		System.out.print("Enter B: ");
		B = sc.nextInt();		
		System.out.print("Enter C: ");
		C = sc.nextInt();
		
		System.out.printf("Formula: %dx^2 + %dx + %d\n", A,B,C);
		
		positiveRoot = (-B + Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		negativeRoot = (-B - Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		
		System.out.printf("Positive root: %.1f\n", positiveRoot);
		
		System.out.printf("Negative root: %.1f\n", negativeRoot);
	  
	     
	     
	}

}
