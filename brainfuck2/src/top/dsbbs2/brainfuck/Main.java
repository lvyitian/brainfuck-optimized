
package top.dsbbs2.brainfuck;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
  public static void main(final String[] args) throws Throwable
  {
    if (args.length == 0) {
      final Scanner input = new Scanner(System.in);
      input.useDelimiter("\n");
      while (true) {
        final String source = input.next().trim();
        if ("exit".equals(source)) {
          break;
        }
        try {
          Main.eval(source, new ArrayList<>(4096), new int[] {0}, input);
        } catch (final Throwable e) {
          e.printStackTrace();
        }
      }
      input.close();
    } else {
      Main.eval(Main.readTextFile(args[0], "UTF8"), new ArrayList<>(4096), new int[] {0},
          new Scanner(System.in));
    }
  }

  public static String readTextFile(final String f, final String e) throws Throwable
  {
    try (FileInputStream inputStream = new FileInputStream(f)) {
      final byte[] buf = new byte[inputStream.available()];
      inputStream.read(buf);
      return new String(buf, e);
    }
  }

  public static String charArrToString(final char[] arr, final int start, final int end)
  {
    final StringBuilder builder = new StringBuilder();
    for (int i = start; i < end; i++) {
      builder.append(arr[i]);
    }
    return builder.toString();
  }

  public static void extendArray(final ArrayList<Character[]> ca, final int index)
  {
    while (index >= ca.size()) {
      ca.add(new Character[] {'\0'});
    }
  }

  public static ArrayList<CustomEntry> readLoops(final String source)
  {
    final ArrayList<CustomEntry> ret = new ArrayList<>();
    final char[] tmp = source.toCharArray();
    int count = 0;
    for (int i = 0; i < tmp.length; i++) {
      final char c = tmp[i];
      if (c=='[') {
        if (count == 0) {
          ret.add(new CustomEntry(i, -1));
        }
        count++;
      } else if (c==']') {
        count--;
        if (count == 0) {
          final CustomEntry tce = ret.parallelStream().filter(i2 -> i2.value== -1)
              .findFirst().orElse(null);
          if (tce == null) {
            throw new BrainfuckException(i, "unexpected null value");
          }
          tce.value=i;
        }
      }
    }
    return ret;
  }

  public static void eval(final String source, final ArrayList<Character[]> ca, final int[] index,
      final Scanner s)
  {
    Main.extendArray(ca, index[0]);
    final char[] arr = source.toCharArray();
    final ArrayList<CustomEntry> locs = Main.readLoops(source);
    for (int c = 0; c < arr.length; c++) {
      try {
        final char i = arr[c];
        if (i==' ' || i=='\n' || i=='\r') {
          continue;
        }
        final int c2 = c;
        final CustomEntry tce = locs.parallelStream().filter(i2 -> i2.key==c2)
            .findFirst().orElse(null);
        if (tce != null) {
          final String ws = Main.charArrToString(arr, c + 1, tce.value);
          while (ca.get(index[0])[0] != '\0') {
            Main.eval(ws, ca, index, s);
          }
          c = tce.value;
          continue;
        }

        if (i=='>') {
          index[0]++;
          Main.extendArray(ca, index[0]);
          continue;
        }
        if (i=='<') {
          index[0]--;
          continue;
        }
        if (i=='+') {
          ca.get(index[0])[0]++;
          continue;
        }
        if (i=='-') {
          ca.get(index[0])[0]--;
          continue;
        }
        if (i=='.') {
          System.out.print(ca.get(index[0])[0]);
          continue;
        }
        if (i==',') {
          ca.get(index[0])[0]=s.next().toCharArray()[0];
          continue;
        }
      } catch (final Throwable exc) {
        throw new BrainfuckException(c + 1, exc);
      }
    }
  }
}
