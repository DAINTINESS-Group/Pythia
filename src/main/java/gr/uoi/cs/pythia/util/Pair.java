package gr.uoi.cs.pythia.util;

import java.io.Serializable;
import java.util.Objects;

public class Pair<T> implements Serializable {
  private final T columnA, columnB;

  public Pair(T columnA, T columnB) {
    this.columnA = columnA;
    this.columnB = columnB;
  }

  public T getColumnA() {
    return columnA;
  }

  public T getColumnB() {
    return columnB;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof Pair)) return false;
    Pair<?> p = (Pair<?>) obj;
    return Objects.equals(this.columnA, p.columnA) && Objects.equals(this.columnB, p.columnB)
        || Objects.equals(this.columnA, p.columnB) && Objects.equals(this.columnB, p.columnA);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnA, columnB);
  }
}
