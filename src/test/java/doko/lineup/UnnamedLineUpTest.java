package doko.lineup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnnamedLineUpTest {

	UnnamedLineUp lineUp1 = new UnnamedLineUp("1, 3,   7, 5,		90");
	UnnamedLineUp lineUp2 = new UnnamedLineUp("1  ", " 3", "   7", " 5	", "		90");
	UnnamedLineUp lineUp3 = new UnnamedLineUp(new Long(1), new Long(3), new Long(7), new Long(5), new Long(90));
	UnnamedLineUp lineUp4 = new UnnamedLineUp("1,3,5,7");
	UnnamedLineUp lineUp5 = new UnnamedLineUp(new Long(1), new Long(7), new Long(5), new Long(3));

	UnnamedLineUp lineUp6 = new UnnamedLineUp("NaN, Infinity, -7, 99");
	UnnamedLineUp lineUp7 = new UnnamedLineUp("ab, 1, 2, 3");

	UnnamedLineUp lineUp8 = new UnnamedLineUp("1,2,3,NaN");
	UnnamedLineUp lineUp9 = new UnnamedLineUp("1,2,3,Infinity");
	UnnamedLineUp lineUp10 = new UnnamedLineUp("1,2,3,-4");
	UnnamedLineUp lineUp11 = new UnnamedLineUp("1,2,3,null");
	UnnamedLineUp lineUp12 = new UnnamedLineUp("1,2,3,1");

	Long[] longs = { null, new Long(3), new Long(7) };

	@Test
	public void testEquals() {
		assertTrue(lineUp1.equals(lineUp1));
		assertTrue(lineUp1.equals(lineUp2));
		assertTrue(lineUp1.equals(lineUp3));
		assertTrue(lineUp2.equals(lineUp1));
		assertTrue(lineUp2.equals(lineUp2));
		assertTrue(lineUp2.equals(lineUp3));
		assertTrue(lineUp3.equals(lineUp1));
		assertTrue(lineUp3.equals(lineUp2));
		assertTrue(lineUp3.equals(lineUp3));

		assertTrue(lineUp4.equals(lineUp4));
		assertTrue(lineUp4.equals(lineUp5));
		assertTrue(lineUp5.equals(lineUp4));
		assertTrue(lineUp5.equals(lineUp5));

		assertFalse(lineUp1.equals(lineUp4));
		assertFalse(lineUp1.equals(lineUp5));
		assertFalse(lineUp1.equals(lineUp6));
		assertFalse(lineUp1.equals(lineUp7));
		assertFalse(lineUp2.equals(lineUp4));
		assertFalse(lineUp2.equals(lineUp5));
		assertFalse(lineUp2.equals(lineUp6));
		assertFalse(lineUp2.equals(lineUp7));
		assertFalse(lineUp3.equals(lineUp4));
		assertFalse(lineUp3.equals(lineUp5));
		assertFalse(lineUp3.equals(lineUp6));
		assertFalse(lineUp3.equals(lineUp7));
		assertFalse(lineUp4.equals(lineUp6));
		assertFalse(lineUp4.equals(lineUp7));
		assertFalse(lineUp5.equals(lineUp6));
		assertFalse(lineUp5.equals(lineUp7));
	}

	@Test
	public void testValid() {
		assertFalse(lineUp1.isValid());
		assertFalse(lineUp2.isValid());
		assertFalse(lineUp3.isValid());
		assertFalse(lineUp6.isValid());
		assertFalse(lineUp7.isValid());
		assertFalse(lineUp8.isValid());
		assertFalse(lineUp9.isValid());
		assertFalse(lineUp10.isValid());
		assertFalse(lineUp11.isValid());
		assertFalse(lineUp12.isValid());

		assertTrue(lineUp4.isValid());
		assertTrue(lineUp5.isValid());
	}

	@Test
	public void testLineUpString() {
		assertTrue(lineUp1.getLineUpString().equals("1,3,5,7,90"));
		assertTrue(lineUp2.getLineUpString().equals("1,3,5,7,90"));
		assertTrue(lineUp3.getLineUpString().equals("1,3,5,7,90"));
		assertTrue(lineUp4.getLineUpString().equals("1,3,5,7"));
		assertTrue(lineUp5.getLineUpString().equals("1,3,5,7"));
		assertTrue(lineUp6.getLineUpString().equals("-1,-1,-7,99"));
		assertTrue(lineUp7.getLineUpString().equals("-1,1,2,3"));
		assertTrue(lineUp8.getLineUpString().equals("-1,1,2,3"));
		assertTrue(lineUp9.getLineUpString().equals("-1,1,2,3"));
		assertTrue(lineUp10.getLineUpString().equals("-4,1,2,3"));
		assertTrue(lineUp11.getLineUpString().equals("-1,1,2,3"));

		assertTrue(UnnamedLineUp.getLineUpString(longs).equals("-1,3,7"));
	}
}
