package tim.snyder.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import tim.snyder.BarrenField;
import tim.snyder.Coordinate;

class BarrenTest {

	@Test
	void newFieldTest() {
		BarrenField b = new BarrenField("{,}");
		assertNotNull(b.getFieldColors());
		assertEquals(400, b.getFieldColors().length);
		assertEquals(600, b.getFieldColors()[0].length);
		assertEquals("240000", b.getPlotAreas());
	}

	@Test
	void newFieldArrayTest() {
		BarrenField b = new BarrenField("{,}");
		int[][] fieldColors = b.getFieldColors();
		assertNotNull(fieldColors);
		assertEquals(1, fieldColors[0][0]);
		fieldColors[0][0]=0;
		assertEquals(1, b.getFieldColors()[0][0]);
		assertEquals("240000", b.getPlotAreas());
	}
	
	@Test
	void coordinateTest() {
		Coordinate c = new Coordinate(1, 2);
		assertEquals(1, c.getX());
		assertEquals(2, c.getY());
		assertEquals("1 2", c.toString());
	}

	@Test
	void barrenTest() {
		BarrenField b = new BarrenField("{\"0 0 10 10\"}");
		assertEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[0][0]);
		assertEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[10][10]);
		assertEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[5][5]);
		assertNotEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[100][100]);
		assertNotEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[10][11]);
		assertNotEquals(BarrenField.SEEN_BARREN, b.getFieldColors()[11][10]);
		assertEquals("239879", b.getPlotAreas());
	}

	@Test
	void givenTest1() {
		BarrenField b = new BarrenField("{\"0 292 399 307\"} ");
		assertEquals("116800 116800", b.getPlotAreas());
	}

	@Test
	void givenTest2() {
		BarrenField b = new BarrenField("{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}");
		assertEquals("22816 192608", b.getPlotAreas());
	}
	
	@Test
	void badInputTest() {
		assertThrows(BarrenField.BadInputException.class, () -> {
			new BarrenField("{\"0 red 1 1\"}");
		});
	}
	
	@Test
	void badCoordsTest() {
		BarrenField b = new BarrenField("{\"0 307 399 292\"} ");
		assertEquals("116800 116800", b.getPlotAreas());
	}

	@Test
	void outOfBoundsTest() {
		BarrenField b = new BarrenField("{\"-1 292 600 307\"} ");
		assertEquals("116800 116800", b.getPlotAreas());
	}
	
	@Test
	void spiralTest() {
		BarrenField b = new BarrenField("{\"10 0 10 589\", \"10 589 389 589\", \"389 10 389 589\", "+
										 "\"20 10 389 10\", \"20 10 20 569\", \"20 569 359 569\"}");
		assertEquals("237185", b.getPlotAreas());
	}
}
