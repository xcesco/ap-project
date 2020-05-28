package cartesian;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CartesianTest {
  @Test
  public void test() {
    Integer[][] dimensions = {{1, 2}, {3, 4}, {5, 6}};

    Stream<List<Integer>> permutations = Cartesian.productOf(
            dimensions,                  // array of dimensions
            new ArrayList<>(),                          // initial value
            (tens, ones) ->
            {
              List<Integer> temp = new ArrayList<>();
              temp.addAll(tens);
              temp.add(ones);
              return temp;
            }
            //     tens * 10 + ones
    );  // combiner function

    permutations.forEach(System.out::println);
  }
}
