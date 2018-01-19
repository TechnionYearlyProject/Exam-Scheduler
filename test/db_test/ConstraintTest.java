package db_test;

import db.Constraint;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ConstraintTest {
    private LocalDate c1, c2, c3;
    Constraint con1, con2,con3;
    @Before
    public void setUp() {
        c1 = LocalDate.of(2018,1,19);
        c2 = LocalDate.of(2018,3,1);
        c3 = LocalDate.of(2018,1,19);
        con1 = new Constraint(c1);
        con2 = new Constraint(c2);
        con3 = new Constraint(c3);
    }

    @Test
    public void sanityTest(){
        assertFalse(con1.forbidden);
        con1 = new Constraint(c1,true);
        assertTrue(con1.forbidden);
    }

    @Test
    public void compareTest(){
        assertEquals(-1, con1.compareTo(con2));
        assertEquals(1,con2.compareTo(con1));
        assertEquals(0,con1.compareTo(con3));
    }

    @Test
    public void constraintsWithSameDatesConsideredEquals(){
        assertTrue(con1.equals(con3));
        assertFalse(con1.equals(con2));
    }

    @Test
    public void constraintCopyCtor(){
        Constraint copy1 = new Constraint(con1);
        assertTrue(copy1.forbidden == con1.forbidden);
        assertTrue(copy1.date.equals(con1.date));
    }
}
