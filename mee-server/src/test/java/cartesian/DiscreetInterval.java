package cartesian;

import java.util.stream.Stream;

public interface DiscreetInterval<T> {
  Stream<T> stream();

  T get(long index);

  T getLowValue();

  T getHighValue();

  long getSize();
}
