package tim.snyder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BarrenField {

	public static final int UNKNOWN = 0;
	public static final int BARREN = -1;
	public static final int SEEN_BARREN = -2;
	private final int[][] fieldColors = new int[400][600];
	private int[] areas = new int[1];

	/**
	 * This is an object to hold the data of barren zones and calculate the fertile zones
	 * @param barrenZones a single string of the input
	 */
	public BarrenField(String barrenZones) {
		// Remove all formatting characters and split on spaces
		String edittedInput = barrenZones.replaceAll("[\\{\\},\\\"]", "");
		String[] splitInput = edittedInput.split(" ");
		if(splitInput.length==1) {
			// Special case for no barren zones
			colorField();
			return;
		}
		
		// Create an array for the barren rectangles' corners
		int[][] coordinates = new int[splitInput.length/2][2];
		try {
			for(int i = 0; i < splitInput.length; i++) {
				if(i%2 == 0) { // This is the X value
					coordinates[i/2][0] = Integer.parseInt(splitInput[i]);
					if(i/2 % 2 == 1 && coordinates[i/2][0] < coordinates[i/2-1][0]) {
						// Swap X values if out of order
						int temp = coordinates[i/2][0];
						coordinates[i/2][0] = coordinates[i/2-1][0];
						coordinates[i/2-1][0] = temp;
					}
				} else { // This is the Y value
					coordinates[i/2][1] = Integer.parseInt(splitInput[i]);
					if(i/2 % 2 == 1 && coordinates[i/2][1] < coordinates[i/2-1][1]) {
						// Swap Y values if out of order
						int temp = coordinates[i/2][1];
						coordinates[i/2][1] = coordinates[i/2-1][1];
						coordinates[i/2-1][1] = temp;
					}
				}
			}
		} catch(NumberFormatException e) { // Throw an exception if we have unexpected punctuation or letters
			throw new BadInputException();
		}

		// For each point between a an even-odd indexed pair of coordinates is barren, so we label the field with those.
		for(int i = 0; i < coordinates.length; i+=2) {
			for(int x = Integer.max(coordinates[i][0], 0); x <= coordinates[i+1][0] && x < 400; x++) {
				for(int y = Integer.max(coordinates[i][1], 0); y <= coordinates[i+1][1] && y < 600; y++) {
					fieldColors[x][y] = BARREN;
				}
			}
		}

		// All untouched points are now fertile, but we have to figure out what the area of each contiguous zone is.
		colorField();
	}

	/**
	 * 	This method goes through the entire field and marks every 1x1 square with a number.
	 *  Negative numbers are barren (-1 is unchecked barren and -2 is checked barren to prevent duplicates)
	 *  Positive numbers indicate that the position fits in that contiguous set of squares
	 *  
	 *  It does this by starting in the bottom left, then searching through every 0 or -1 to mark them
	 *  with a positive number or -2.  If an adjacent square is 0 or 1, it adds it to the queue of 
	 *  squares to go through, otherwise, it gets ignored since it was checked already.
	 */
	private void colorField() {
		// Use two queues to go through positions in the field
		LinkedList<Coordinate> toColor = new LinkedList<>();
		LinkedList<Coordinate> barrenTouched = new LinkedList<>();
		
		// Start at (0,0) every time
		if(fieldColors[0][0]==BARREN) {
			barrenTouched.add(new Coordinate(0, 0));
		}else {
			toColor.add(new Coordinate(0, 0));
		}
		
		// plotNumber is which contiguous zone it's in.  Starts at 1 and increments after we can't find any more connected locations
		int plotNumber = 1;
		
		// coloredField keeps track of if we colored any of the field locations since the last time we changed something
		boolean coloredField = false;
		
		// Loop until both queues are empty of field locations
		while(!(toColor.isEmpty() && barrenTouched.isEmpty())) {
			if(toColor.isEmpty()) { // Check if there are any more connected uncolored spots
				
				// Increment the plotNumber if we colored since we ran out of contiguous points for that plot
				if(coloredField) {
					coloredField = false;
					plotNumber++;
				}
				
				// Grab the next barren point
				Coordinate c = barrenTouched.pop();
				int x = c.getX();
				int y = c.getY();
				if(fieldColors[x][y] == SEEN_BARREN) {
					// If we've seen it before, we can skip it
					continue;
				}
				// Mark it as seen
				fieldColors[x][y] = SEEN_BARREN;

				// Check to the right and above for new field locations to check
				if(x+1 < 400 && fieldColors[x+1][y] == BARREN) {
					barrenTouched.add(new Coordinate(x+1, y));
				} else if(x+1<400 && fieldColors[x+1][y] == UNKNOWN) {
					toColor.add(new Coordinate(x+1, y));
				}
				if(y+1 < 600 && fieldColors[x][y+1] == BARREN) {
					barrenTouched.add(new Coordinate(x, y+1));
				} else if(y+1 < 600 && fieldColors[x][y+1] == UNKNOWN) {
					toColor.add(new Coordinate(x, y+1));
				}
				
			} else {
				// We're coloring a field since the last time the queue was empty
				coloredField = true;
				Coordinate c = toColor.pop();
				int x = c.getX();
				int y = c.getY();
				if(fieldColors[x][y] != UNKNOWN) {
					// Skip if it's already been colored
					continue;
				}
				// Color the square
				fieldColors[x][y] = plotNumber;

				// Check spot to the right if it's barren or unknown
				if(x+1 < 400 && fieldColors[x+1][y] == BARREN) {
					barrenTouched.add(new Coordinate(x+1, y));
				} else if(x+1<400 && fieldColors[x+1][y] == UNKNOWN) {
					toColor.add(new Coordinate(x+1, y));
				}
				// Check spot above
				if(y+1 < 600 && fieldColors[x][y+1] == BARREN) {
					barrenTouched.add(new Coordinate(x, y+1));
				} else if(y+1 < 600 && fieldColors[x][y+1] == UNKNOWN) {
					toColor.add(new Coordinate(x, y+1));
				}

				// Check spot to the left and below (needed since barren land can create spirals)
				// We don't need to check barren though, since we will always hit them by checking
				// all spots above and to the right of the bottom left spot
				if(x-1 >= 0 && fieldColors[x-1][y] == UNKNOWN) {
					toColor.add(new Coordinate(x-1, y));
				}
				if(y-1 >= 0 && fieldColors[x][y-1] == UNKNOWN) {
					toColor.add(new Coordinate(x, y-1));
				}
			}
		}
		if(plotNumber>1) plotNumber--;  // No barren zones causes this to miscount, so we have a check
		areas = new int[plotNumber];
	}

	/**
	 * This will check if the different plots have had their areas calculated, if not it calculates them.
	 * Then, it will take the areas, sort them and combine them into a single string.
	 * 
	 * @return A string of the areas of contiguous plots in order of smallest to largest
	 */
	public String getPlotAreas() {
		// Check if we've counted already
		// Count up how many spots have each color
		if(areas[0]==0) {
			for(int x = 0; x < fieldColors.length; x++) {
				for(int y = 0; y < fieldColors[0].length; y++) {
					if(fieldColors[x][y]<=0) {
						continue;
					}
					areas[fieldColors[x][y]-1]++;
				}
			}
		}
		
		// Make and sort a list of the areas (area is equal to the number of spots of a color)
		List<Integer> areaList = new ArrayList<>();
		for(int i:areas) {
			areaList.add(i);
		}
		Collections.sort(areaList);
		
		// Create and return a string of the areas, in order
		StringBuilder s = new StringBuilder();
		for(int i = 0;i<areaList.size();i++) {
			s.append(areaList.get(i)+" ");
		}
		return s.toString().trim();
	}

	/**
	 * Creates and returns a copy of the field array for manual inspection
	 * 
	 * @return the field array cloned.
	 */
	public int[][] getFieldColors(){
		int[][] copy = new int[400][600];
		for(int x = 0; x < 400; x++) {
			for (int y = 0; y < 600; y++) {
				copy[x][y] = fieldColors[x][y];
			}
		}
		return copy;
	}

	public class BadInputException extends RuntimeException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String getMessage() {
			return "Input included non-integer value";
		}
	}
}
