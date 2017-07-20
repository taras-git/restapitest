import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by ttymchyshyn on 20.07.17.
 */
public class ArithmethicTester {

    @Test
    public void testAdd() {
        MyNumbers my = new MyNumbers();
        Float i = 5.0f, j = 2.0f;

        assertTrue(my.add(i, j) == 7);
    }

    @Test
    public void testMult() {
        MyNumbers my = new MyNumbers();
        Float i = 5.0f, j = 2.0f;

        assertTrue(my.mult(i, j) == 10);
    }

    @Test
    public void testDiv() {
        MyNumbers my = new MyNumbers();
        Float i = 5.0f, j = 2.0f;

        assertTrue(my.div(i, j) == 2.5);
    }

    @Test(expected=java.lang.ArithmeticException.class)
    public void testDivByZero() {
        MyNumbers my = new MyNumbers();
        Float i = 5.0f, j = 0.0f;

        my.div(i, j);
    }

    static public abstract class GenericClass<E extends Number> {
        public abstract E add(E x, E y);
        public abstract E mult(E x, E y);
        public abstract E div(E x, E y);
    }

    static public class MyNumbers extends GenericClass<Float> {
        @Override
        public Float add(Float x, Float y) {
            return x + y;
        }

        @Override
        public Float mult(Float x, Float y) {
            return x * y;
        }

        @Override
        public Float div(Float x, Float y) {
            if(y == 0) {
                throw new ArithmeticException("Can not divide by ZERO!");
            }
            return x / y;
        }

    }
}
