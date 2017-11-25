import org.junit.Assert;
import org.junit.Test;

public class MainTest {

    @Test
    public void test() {
        Assert.assertTrue(Main.add(1, 1) == 2);
        System.out.println("test passed");
    }
}
