package tim.snyder;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		if(args.length>0) {
			BarrenField b = new BarrenField(args[0]);
			System.out.println(b.getPlotAreas());
		} else {
			String s = "";
			Scanner scan = new Scanner(System.in);
			while(!s.equals("0")) {
				System.out.println("Please enter the set of barren plots in the field or 0 to end.");
				s = scan.nextLine();
				if(!s.equals("0")) {
					BarrenField b = new BarrenField(s);
					System.out.println(b.getPlotAreas());
				}
			}
			scan.close();
		}
	}

}
