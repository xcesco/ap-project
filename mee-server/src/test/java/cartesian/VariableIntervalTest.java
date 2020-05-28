package cartesian;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariableIntervalTest {
  @Test
  public void test() {
    VariableInterval interval = VariableInterval.Builder.create().setName("x0").setInterval(0, 0.1, 1).build();
    assertEquals(interval.getSize(), 11);
    interval.stream().forEach(System.out::println);
  }

  @Test
  public void test1() {
    VariableInterval interval = VariableInterval.Builder.create().setName("x0").setInterval(0.1, 0.1, 1).build();
    assertEquals(interval.getSize(), 10);
    interval.stream().forEach(System.out::println);
  }

  @Test
  public void testBig() {
    //VariableInterval interval = VariableInterval.Builder.create().setName("x0").setInterval(1, 1, 100000000000000000000000000000000000000000000000000.0).build();
    //interval.stream().forEach(value -> System.out.println(value));
    //System.out.println(interval.getSize());
  }

  @Test
  public void testOne() {
    VariableInterval interval0 = VariableInterval.Builder.create().setInterval(1, 1, 10).setName("x0").build();
    VariableInterval interval1 = VariableInterval.Builder.create().setInterval(11, 1, 20).setName("x1").build();
    VariableInterval interval2 = VariableInterval.Builder.create().setInterval(21, 1, 30).setName("x2").build();

    AtomicInteger counter = new AtomicInteger(1);
    Stream<List<Double>> stream = DiscreetIntervalCartesianProduct.productOf(Arrays.asList(interval0, interval1, interval2), new ArrayList<>(),
            (tens, ones) ->
            {
              counter.getAndIncrement();
              List<Double> temp = new ArrayList<>();
              temp.addAll(tens);
              temp.add(ones);
              return temp;
            });

    List<List<Double>> values = stream.collect(Collectors.toList());
    System.out.println("i " + (counter.get()));
    assertEquals(values.size(), 1000);
    values.forEach(System.out::println);
    //VariableInterval interval = VariableInterval.Builder.create().setName("x0").setInterval(1, 1, 100000000000000000000000000000000000000000000000000.0).build();
    //interval.stream().forEach(value -> System.out.println(value));
    //System.out.println(interval.getSize());
  }

  @Test
  public void testTwo() {
    VariableInterval interval0 = VariableInterval.Builder.create().setInterval(1, 1, 10).setName("x0").build();
    VariableInterval interval1 = VariableInterval.Builder.create().setInterval(11, 1, 20).setName("x1").build();
    VariableInterval interval2 = VariableInterval.Builder.create().setInterval(21, 1, 30).setName("x2").build();

    AtomicInteger counter = new AtomicInteger(1);
    Stream<List<Double>> stream = DiscreetIntervalCartesianProduct.productOf(Arrays.asList(interval0, interval1, interval2), new ArrayList<>(),
            (tens, ones) ->
            {
              counter.getAndIncrement();
              List<Double> temp = new ArrayList<>();
              temp.addAll(tens);
              temp.add(ones);
              return temp;
            });

    //List<List<Double>> values = stream.


   // System.out.println("i " + (counter.get()));
    //assertEquals(values.size(), 1000);
    //values.forEach(System.out::println);
    //VariableInterval interval = VariableInterval.Builder.create().setName("x0").setInterval(1, 1, 100000000000000000000000000000000000000000000000000.0).build();
    //interval.stream().forEach(value -> System.out.println(value));
    //System.out.println(interval.getSize());
  }


}
