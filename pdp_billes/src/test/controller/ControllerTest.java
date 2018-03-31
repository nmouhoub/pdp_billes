package test.controller;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import view.DrawingPanel;

public class ControllerTest {
	Controller c;
	Circuit circuit;

	@Before
	public void setUp() throws Exception {
		c = Controller.getInstance();
		circuit = getControllerCircuit();
	}

	public Circuit getControllerCircuit() {
		try {
			Field fcircuit = Controller.class.getDeclaredField("_circuit");
			fcircuit.setAccessible(true);
			return (Circuit) fcircuit.get(c);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@After
	public void tearDown() throws Exception {
		c.clearCircuit();
		c = null;
		circuit = null;
	}

	@Test
	public void test_checkIfPointIsInBall() {
		Point p = new Point(10, 10);
		Ball b = new Ball(10, 10, 3, 1);
		c.addBall(b);

		// Le point est sur le centre de la bille
		assertEquals(b, c.checkIfPointIsInBall(p));

		// Le point est dans la bille, mais pas sur son centre
		p.setLocation(11, 10);
		assertEquals(b, c.checkIfPointIsInBall(p));

		// Le point n'est pas dans la bille
		p.setLocation(50, 50);
		assertEquals(null, c.checkIfPointIsInBall(p));
	}

	@Test
	public void test_removeLinesOutOfBounds() {
		Point depart = new Point(150, 150);
		Point arrivee = new Point(400, 150);
		ObstacleLine o = new ObstacleLine(depart, arrivee, 0.5);
		c.addLine(o);

		// L'obstacle est toujours dans le circuit
		circuit.set_width(450);
		circuit.set_height(500);
		c.removeLinesOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_lines().size(), 1, 0);

		// L'obstacle est hors du circuit
		circuit.set_width(300);
		c.removeLinesOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_lines().size(), 0, 0);
	}

	@Test
	public void test_removeBallsOutOfBounds() {
		Ball b = new Ball(400, 150, 10, 0);
		c.addBall(b);
		// La balle est toujours dans le circuit
		circuit.set_width(450);
		circuit.set_height(500);
		c.removeBallsOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_balls().size(), 1, 0);
		// La balle est hors du circuit
		circuit.set_width(402);
		c.removeBallsOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_balls().size(), 0, 0);
	}

	@Test
	public void test_checkIfBallIsOnExistingLine() {
		Point depart = new Point(150, 150);
		Point arrivee = new Point(400, 150);
		ObstacleLine o = new ObstacleLine(depart, arrivee, 0.5);
		c.addLine(o);
		// La balle n'est pas sur la ligne
		Ball b = new Ball(50, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), false);

		// La balle est sur la ligne en comptant son radius
		b = new Ball(200, 145, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), true);

		// La balle est exatement sur la ligne
		b = new Ball(200, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), true);
	}

	@Test
	public void test_checkIfBallIsOnExistingBall() {
		Ball b = new Ball(50, 150, 10, 0);
		c.addBall(b);
		// La balle est sur une autre balle
		Ball b2 = new Ball(52, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingBall(b2), true);

		Ball b3 = new Ball(71, 150, 10, 0);
		// La balle n'est pas sur une autre balle
		assertEquals(c.checkIfBallIsOnExistingBall(b3), false);

	}

	@Test
	public void test_checkIfLineIsOnExistingBall() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 0.5);

		// Existing ball
		Ball b = new Ball(10, 10, 2, 1);
		c.addBall(b);

		// La ligne traverse une bille en son centre
		assertEquals(true, c.checkIfLineIsOnExistingBall(o));

		// La ligne et la bille se touchent en renant en compte leur rayon et
		// epaisseur
		b.set_location(10, 8.1);
		assertEquals(true, c.checkIfLineIsOnExistingBall(o));

		// La ligne ne touche aucune bille
		b.set_location(50, 50);
		assertEquals(false, c.checkIfLineIsOnExistingBall(o));
	}

	@Test
	public void test_updateBall() {
		Ball b = new Ball(10, 10, 1, 1);

		c.addBall(b);
		assertEquals(1, b.get_mass(), 1E-10);
		assertEquals(1, b.get_radius());

		// *** Test update valide
		c.updateBall(b, 1, 2, 20, 20);
		testBallPosition(b);

		// *** Test update invalide
		// La modification ne peut pas se faire, la bille doit revenir dans
		// l'etat dans lequel elle etait avant l update

		// ** Test en updatant avec des coordonnees hors de l ecran

		c.updateBall(b, 1, 2, circuit.get_width() + 1, 20);
		testBallPosition(b);

		c.updateBall(b, 1, 2, 20, circuit.get_height() + 1);
		testBallPosition(b);

		// ** Test en updatant sur un autre objet (ball ou line)
		// * Conflit avec une bille
		Ball b2 = new Ball(30, 30, 2, 1);
		c.addBall(b2);
		// Centres identiques
		c.updateBall(b, 1, 2, 30, 30);

		testBallPosition(b);

		// Centres differents mais b et b2 se chevauchent
		c.updateBall(b, 1, 2, 29, 30);
		testBallPosition(b);

		// * Conflit avec un obstacle
		ObstacleLine o = new ObstacleLine(new Point(0, 30), new Point(60, 30), 0.5);
		c.addLine(o);

		// Centre de la bille sur la droite
		c.updateBall(b, 1, 2, 30, 30);
		testBallPosition(b);

		// Bille sur l'obstacle en prenant en compte son rayon
		c.updateBall(b, 1, 2, 30, 31);
		testBallPosition(b);
	}

	private void testBallPosition(Ball b) {
		assertEquals(2, b.get_mass(), 1E-10);
		assertEquals(1, b.get_radius());
		assertEquals(20, b.get_location().getX(), 1E-10);
		assertEquals(20, b.get_location().getY(), 1E-10);
	}

	@Test
	public void test_updateLine() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 0.5);
		c.addLine(o);

		// ** Update valide
		c.updateLine(o, 0, 20, 20, 20, 0.5);
		testLinePosition(o);

		// ** Update invalide
		// La modification ne peut pas se faire, l obstacle doit revenir dans
		// l'etat dans lequel elle etait avant l update

		// * Modification des positions hors du circuit

		c.updateLine(o, circuit.get_width() + 1, 20, 20, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, circuit.get_height() + 1, 20, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, 20, circuit.get_width() + 1, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, 20, 20, circuit.get_height() + 1, 0.5);
		testLinePosition(o);

		// * Conflit avec une bille
		Ball b = new Ball(30, 20, 2, 1);
		c.addBall(b);
		c.updateLine(o, 0, 20, 60, 20, 0.5);
		testLinePosition(o);
	}

	private void testLinePosition(ObstacleLine o) {
		assertEquals(new Point(0, 20), o.get_begin());
		assertEquals(new Point(20, 20), o.get_end());
	}

	@Test
	public void test_collisionSegment() {
		Controller controller = Controller.getInstance();
		Ball ball1 = new Ball(120, 189, 10, 1);
		Ball ball2 = new Ball(300, 400, 10, 1);
		Point p1 = new Point(80, 200);
		Point p2 = new Point(200, 200);
		ObstacleLine obstacle = new ObstacleLine(p1, p2, 0.5);
		boolean b1 = controller.collisionSegment(ball1, obstacle);
		boolean b2 = controller.collisionSegment(ball2, obstacle);
		// La balle est en collision avec l'obstacle
		assertEquals(true, b1);
		// La balle n'est pas en collision avec l'obstacle
		assertEquals(false, b2);
	}

	@Test
	public void test_collisionPointCercle() {
		Ball ball1 = new Ball(29, 26, 15, 1);
		Ball ball2 = new Ball(26, 13, 5, 1);
		Point2D.Double p1 = new Point2D.Double(33.0, 10);
		Point2D.Double p2 = new Point2D.Double(22.0, 10);
		boolean b1 = c.collisionPointCercle(p1, p2, ball1);
		boolean b2 = c.collisionPointCercle(p1, p2, ball2);
		// La balle est en collision avec le point
		assertEquals(true, b1);
		// La balle n'est pas en collision avec le point
		assertEquals(false, b2);
	}

	/*
	 * On place deux points a distance 11 l'un de l'autre. On verifie que la
	 * fonction distance pour ces 2 points renvoie 11.
	 */
	@Test
	public void test_distance() {
		Point2D.Double p1 = new Point2D.Double(33.0, 5);
		Point2D.Double p2 = new Point2D.Double(22.0, 5);
		double dist = c.distance(p1, p2);
		// Distance valide
		assertEquals(true, Math.abs(dist - 11) < 1E-10);
		// Distance invalide
		assertEquals(false, Math.abs(dist - 12) < 1E-10);
	}

	/*
	 * On cree deux billes qui se chevauchent et on verifie qu'elles sont bien
	 * en collision. On cree une autre bille plus loin et on verifie qu'elle
	 * n'est pas en collision avec les premieres billes. Pour finir, on place
	 * deux billes qui ont un seul point de contact et on verifie que la
	 * collision est bien detectee.
	 */
	@Test
	public void test_checkCollisionBallBall() {
		Ball ball1 = new Ball(120, 180, 10, 1);
		Ball ball2 = new Ball(110, 185, 6, 1);
		Ball ball3 = new Ball(200, 300, 8, 1);
		boolean b1 = c.checkCollisionBallBall(ball1, ball2);
		boolean b2 = c.checkCollisionBallBall(ball1, ball3);
		// Les balles entrent en collision
		assertEquals(true, b1);
		// Les balles n'entrent pas en collision
		assertEquals(false, b2);
		ball2.setAll(140, 180, 10, 1);
		b1 = c.checkCollisionBallBall(ball1, ball2);
		assertEquals(true, b1);
	}

	/*
	 * 
	 */
	@Test
	public void test_setDimensionsPlan() {
		DrawingPanel _dp = new DrawingPanel(new Dimension(500, 500), new JFrame());
		c.setDimensionsPlan(_dp, 300, 200);
		// Les dimensions du circuit et du panel sont correctement modifiees
		assertEquals(getControllerCircuit().get_width(), 300, 0);
		assertEquals(getControllerCircuit().get_height(), 200, 0);
		assertEquals(_dp.getWidth(), 300, 0);
		assertEquals(_dp.getHeight(), 200, 0);
	}

}
