package cartesian;

import org.abubusoft.mee.server.model.compute.VariableValuesRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariableValuesRangeTest {
  @Test
  public void test() {
    VariableValuesRange interval = VariableValuesRange.Builder.create().setName("x0").setInterval(0, 0.1, 1).build();
    assertEquals(interval.getSize(), 11);
    interval.getValues().forEach(System.out::println);
  }

  @Test
  public void test1() {
    VariableValuesRange interval = VariableValuesRange.Builder.create().setName("x0").setInterval(0.1, 0.1, 1).build();
    assertEquals(interval.getSize(), 10);
    interval.getValues().forEach(System.out::println);
  }

//  @Test
//  public void testOne0() {
//    VariableValuesRange interval0 = VariableValuesRange.Builder.create().setInterval(1, 1, 10).setName("x0").build();
//
//    AtomicInteger counter = new AtomicInteger(1);
//    Stream<List<Double>> stream = DiscreetRangeCartesianProduct.productOf(Arrays.asList(interval0, interval0, interval0), new ArrayList<>(),
//            (tens, ones) ->
//            {
//              System.out.print(tens);
//              counter.getAndIncrement();
//              List<Double> temp = new ArrayList<>();
//              temp.addAll(tens);
//              temp.add(ones);
//
//              System.out.println(" -> " + temp);
//              return temp;
//            });
//
//    List<List<Double>> values = stream.collect(Collectors.toList());
//    System.out.println("i " + (counter.get()));
//    assertEquals(values.size(), 100);
//    values.forEach(System.out::println);
//  }

//  @Test
//  public void testOne() {
//    VariableValuesRange interval0 = VariableValuesRange.Builder.create().setInterval(1, 1, 10).setName("x0").build();
//    VariableValuesRange interval1 = VariableValuesRange.Builder.create().setInterval(11, 1, 20).setName("x1").build();
//    VariableValuesRange interval2 = VariableValuesRange.Builder.create().setInterval(21, 1, 30).setName("x2").build();
//
//    AtomicInteger counter = new AtomicInteger(1);
//    Stream<List<Double>> stream = DiscreetRangeCartesianProduct.productOf(Arrays.asList(interval0, interval1, interval2), new ArrayList<>(),
//            (tens, ones) ->
//            {
//              counter.getAndIncrement();
//              List<Double> temp = new ArrayList<>();
//              temp.addAll(tens);
//              temp.add(ones);
//              return temp;
//            });
//
//    List<List<Double>> values = stream.collect(Collectors.toList());
//    System.out.println("i " + (counter.get()));
//    assertEquals(values.size(), 1000);
//    values.forEach(System.out::println);
//  }

//  @Test
//  public void testTwo() {
//    VariableValuesRange interval0 = VariableValuesRange.Builder.create().setInterval(1, 1, 10).setName("x0").build();
//    VariableValuesRange interval1 = VariableValuesRange.Builder.create().setInterval(11, 1, 20).setName("x1").build();
//    VariableValuesRange interval2 = VariableValuesRange.Builder.create().setInterval(21, 1, 30).setName("x2").build();
//
//    AtomicInteger counter = new AtomicInteger(1);
//    Stream<List<Double>> stream = DiscreetRangeCartesianProduct.productOf(Arrays.asList(interval0, interval1, interval2), new ArrayList<>(),
//            (tens, ones) ->
//            {
//              counter.getAndIncrement();
//              List<Double> temp = new ArrayList<>();
//              temp.addAll(tens);
//              temp.add(ones);
//              return temp;
//            });
//  }


}
