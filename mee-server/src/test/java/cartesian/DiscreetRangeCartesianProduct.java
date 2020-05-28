package cartesian;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Adapted version of
 * <p>
 * https://engineering.vena.io/2016/06/07/gotcha-cartesian-products-of-java-8-streams/
 *
 * @param <T>
 * @param <C>
 */
public class DiscreetRangeCartesianProduct<T, C> extends Spliterators.AbstractSpliterator<C> {
  final List<DiscreetRange<T>> dimensions;
  final int[] currentLocation;
  final BiFunction<C, T, C> combiner;
  final Deque<C> partialValues;

  protected DiscreetRangeCartesianProduct(List<DiscreetRange<T>> dimensions, C initialValue, BiFunction<C, T, C> combiner) {
    super(
            dimensions.stream().mapToLong(d -> d.getSize()).reduce(Math::multiplyExact).orElse(0),
            SIZED | DISTINCT | IMMUTABLE);
    this.combiner = combiner;
    this.dimensions = dimensions;
    this.currentLocation = new int[dimensions.size()];
    this.partialValues = new ArrayDeque<>(dimensions.size());
    this.partialValues.push(initialValue);
  }

  @Override
  public boolean tryAdvance(Consumer<? super C> action) {
    if (partialValues.isEmpty()) {
      return false;
    } else {
      // Populate any dimensions that need it
      //
      while (partialValues.size() <= dimensions.size()) {
        int dim = partialValues.size() - 1;
        partialValues.push(
                combiner.apply(
                        partialValues.peek(),
                        dimensions.get(dim).get(currentLocation[dim])));
      }

      // Top of the partialValues stack is actually the full value.
      //
      action.accept(partialValues.pop());

      // Advance the counters, starting with the least significant, popping
      // obsolete partialValues for any exhausted dimensions we encounter.
      //
      for (int dim = dimensions.size() - 1; dim >= 0; dim--) {
        if (++currentLocation[dim] < dimensions.get(dim).getSize()) {
          break;
        } else {
          partialValues.pop();
          currentLocation[dim] = 0;
        }
      }

      return true;
    }
  }

  public static <T, C> Stream<C> productOf(List<DiscreetRange<T>> dimensions, C initialValue, BiFunction<C, T, C> combiner) {
    return StreamSupport.stream(new DiscreetRangeCartesianProduct<>(dimensions, initialValue, combiner), false);
  }

}