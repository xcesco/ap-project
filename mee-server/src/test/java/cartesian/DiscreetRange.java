package cartesian;

import java.util.stream.Stream;

public interface DiscreetRange<T> {
  Stream<T> getValuesStream();

  T get(long index);

  T getLowValue();

  T getHighValue();

  int getSize();
}
