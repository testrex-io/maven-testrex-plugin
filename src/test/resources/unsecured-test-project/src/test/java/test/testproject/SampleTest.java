package test.testproject;

import org.junit.Test;

import static junit.framework.TestCase.fail;

/**
 * Class representing sample tests.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class SampleTest {

    @Test
    public void successfulTest() {

    }

    @Test
    public void failingTest() {
        fail();
    }
}
