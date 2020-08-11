
package top.dsbbs2.brainfuck;

public class CustomEntry
{
  public final int key;
  public volatile int value;

  // private final Object lock=new Object();
  public CustomEntry(final int key, final int value)
  {
    this.key = /* Objects.requireNonNull( */key/* ) */;
    this.value = value;
  }

}
