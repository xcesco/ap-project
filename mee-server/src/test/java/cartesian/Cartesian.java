package cartesian;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// https://engineering.vena.io/2016/06/07/gotcha-cartesian-products-of-java-8-streams/
public class Cartesian<T, C> extends Spliterators.AbstractSpliterator<C> {
  final T[][] dimensions;
  final int[] currentLocation;
  final BiFunction<C, T, C> combiner;
  final Deque<C> partialValues;

  protected Cartesian(T[][] dimensions, C initialValue, BiFunction<C, T, C> combiner) {
    super(
            Stream.of(dimensions).mapToLong(d -> d.length).reduce(Math::multiplyExact).orElse(0),
            SIZED | DISTINCT | IMMUTABLE);
    this.dimensions = dimensions;
    this.currentLocation = new int[dimensions.length];
    this.partialValues = new ArrayDeque<>(dimensions.length);
    this.combiner = combiner;
    this.partialValues.push(initialValue);
  }

  @Override
  public boolean tryAdvance(Consumer<? super C> action) {
    if (partialValues.isEmpty()) {
      return false;
    } else {
      // Populate any dimensions that need it
      //
      while (partialValues.size() <= dimensions.length) {
        int dim = partialValues.size() - 1;
        partialValues.push(
                combiner.apply(
                        partialValues.peek(),
                        dimensions[dim][currentLocation[dim]]));
      }

      // Top of the partialValues stack is actually the full value.
      //
      action.accept(partialValues.pop());

      // Advance the counters, starting with the least significant, popping
      // obsolete partialValues for any exhausted dimensions we encounter.
      //
      for (int dim = dimensions.length - 1; dim >= 0; dim--) {
        if (++currentLocation[dim] < dimensions[dim].length) {
          break;
        } else {
          partialValues.pop();
          currentLocation[dim] = 0;
        }
      }

      return true;
    }
  }

  public static <T, C> Stream<C> productOf(T[][] dimensions, C initialValue, BiFunction<C, T, C> combiner) {
    return StreamSupport.stream(new Cartesian<>(dimensions, initialValue, combiner), false);
  }

}