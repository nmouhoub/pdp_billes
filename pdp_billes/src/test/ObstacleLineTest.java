package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.ObstacleLine;

public class ObstacleLineTest {

	private ObstacleLine o;
	private Point depart, arrivee, p;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		o = null;
		depart = null;
		arrivee = null;
		p = null;

	}

	@Test
	public void test_contains() {
		depart = new Point(50, 50);
		arrivee = new Point(50, 53);
		o = new ObstacleLine(depart, arrivee);
		assertEquals(o.contains(depart), true);
		depart.setLocation(0, 20);
		o.setPositions(depart, arrivee);
		p = new Point(-1, 25);
		assertEquals(o.contains(p), false);
	}

}
