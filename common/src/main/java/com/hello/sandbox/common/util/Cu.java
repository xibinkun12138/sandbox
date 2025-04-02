package com.hello.sandbox.common.util;

import android.graphics.Point;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import com.hello.sandbox.common.util.collections.Quadruple;
import com.hello.sandbox.common.util.collections.TripleElementSet;
import com.hello.sandbox.common.util.collections.TwoElementSet;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * CollectionUtils
 *
 * <p>include operations of java collections and primitive types
 */
public class Cu {

  public static final Charset UTF_8 = Charset.forName("UTF-8");

  public static <T extends Enum> T ordinal(int i, T[] values) {
    if (i >= 0 && i < values.length) return values[i];
    else return null;
  }

  public static <T, V> V option(T t, Func1<T, V> map, V def) {
    if (t == null) {
      return def;
    } else {
      return map.call(t);
    }
  }

  public static <T> List<T> option(T t) {
    if (t == null) return Collections.emptyList();
    else return seq(t);
  }

  public static <T> T takeOne(List<T> items) {
    if (isEmpty(items)) return null;
    else return items.get(0);
  }

  public static <T> List<T> take(List<T> items, int count) {
    if (items == null) return null;
    else return items.subList(0, Math.min(count, items.size()));
  }

  public static String take(String items, int count) {
    return items.substring(0, Math.min(items.length(), count));
  }

  public static <T> List<T> takeRight(List<T> items, int count) {
    return items.subList(Math.max(0, items.size() - count), items.size());
  }

  public static String takeRight(String items, int count) {
    return items.substring(Math.max(0, items.length() - count), items.length());
  }

  public static HashMap<String, String> asMap(String... vs) {
    HashMap<String, String> map = new HashMap<>();
    for (int i = 0; i < vs.length / 2; i += 2) {
      map.put(vs[i], vs[i + 1]);
    }
    return map;
  }

  public static <K, V, E> HashMap<K, V> asMap(Collection<E> list, Func1<E, K> k, Func1<E, V> v) {
    final HashMap<K, V> map = new HashMap<>();
    for (E e : list) {
      map.put(k.call(e), v.call(e));
    }
    return map;
  }

  public static CharSequence[] stringListToCharSequenceArray(List<String> list) {
    return list.toArray(new CharSequence[list.size()]);
  }

  public static <T, E> Pair<T, E> pair(T t, E e) {
    return new Pair<T, E>(t, e);
  }

  public static <A, B, C, D> Quadruple<A, B, C, D> quad(A a, B b, C c, D d) {
    return new Quadruple<A, B, C, D>(a, b, c, d);
  }

  public static <T> int indexOf(List<T> l, Func1<T, Boolean> pred) {
    if (l == null) return -1;
    for (int i = 0; i < l.size(); i++) {
      if (pred.call(l.get(i))) {
        return i;
      }
    }
    return -1;
  }

  public static <T> int lastIndexOf(List<T> l, Func1<T, Boolean> pred) {
    if (l == null) return -1;
    for (int i = l.size() - 1; i >= 0; i--) {
      if (pred.call(l.get(i))) {
        return i;
      }
    }
    return -1;
  }

  public static <T> T find(Collection<T> l, Func1<T, Boolean> pred) {
    if (l == null) return null;
    Iterator<T> iterator = l.iterator();
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (pred.call(t)) {
        return t;
      }
    }
    return null;
  }

  public static <T> T findWithMaxCount(List<T> list, Func1<T, Boolean> pred, int maxCount) {
    if (list == null) return null;
    for (int index = 0; index < list.size(); index++) {
      if (index > maxCount) {
        return null;
      } else {
        T t = list.get(index);
        if (pred.call(t)) {
          return t;
        }
      }
    }
    return null;
  }

  public static <T> T find(T[] l, Func1<T, Boolean> pred) {
    if (l == null) return null;
    for (int i = 0; i < l.length; i++) if (pred.call(l[i])) return l[i];
    return null;
  }

  public static <T> int removeAll(List<T> l, Func1<T, Boolean> pred) {
    int count = 0;
    Iterator<T> iterator = l.iterator();
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (pred.call(t)) {
        iterator.remove();
        count += 1;
      }
    }
    return count;
  }

  public static <T> boolean removeFirst(List<T> l, Func1<T, Boolean> pred) {
    Iterator<T> iterator = l.iterator();
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (pred.call(t)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public static <T, F> List<T> hashMapKeysToArrayList(Map<T, F> map) {
    ArrayList<T> arr = new ArrayList<>();
    for (T t : map.keySet()) {
      arr.add(t);
    }
    return arr;
  }

  public static <T> Set<T> toSet(List<T> ll) {
    if (ll.size() == 0) {
      return Collections.emptySet();
    } else if (ll.size() == 1) {
      return Collections.singleton(ll.get(0));
    } else if (ll.size() == 2) {
      return new TwoElementSet<>(ll.get(0), ll.get(1));
    } else if (ll.size() == 3) {
      return new TripleElementSet<>(ll.get(0), ll.get(1), ll.get(2));
    } else {
      return new HashSet<>(ll);
    }
  }

  public static <T> ArrayList<T> toArray(Set<T> set) {
    ArrayList<T> list = new ArrayList<>(set.size());
    for (T value : set) {
      list.add(value);
    }
    return list;
  }

  public static <T> ArrayList<T> seq(T... items) {
    ArrayList<T> a = new ArrayList<>(items.length);
    Collections.addAll(a, items);
    return a;
  }

  public static <T> ArrayList<T> just(T t) {
    ArrayList<T> a = new ArrayList<>(1);
    a.add(t);
    return a;
  }

  public static <T> T headOrNull(List<T> list) {
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    } else {
      return null;
    }
  }

  public static <T> List<T> headOption(List<T> list) {
    if (list != null && list.size() >= 1) {
      return just(list.get(0));
    } else {
      return Collections.emptyList();
    }
  }

  public static <T, E> ArrayList<E> map(T[] col, Func1<T, E> m) {
    if (col == null) return null;
    ArrayList<E> arr = new ArrayList<>();
    for (T c : col) {
      arr.add(m.call(c));
    }
    return arr;
  }

  public static <T, E> List<E> mapView(List<T> col, Func1<T, E> m) {
    return new AbstractList<E>() {
      @Override
      public E get(int location) {
        return m.call(col.get(location));
      }

      @Override
      public int size() {
        return col.size();
      }
    };
  }

  public static <T, E> ArrayList<E> map(Collection<T> col, Func1<T, E> m) {
    if (col == null) return null;
    ArrayList<E> arr = new ArrayList<>();
    for (T c : col) {
      arr.add(m.call(c));
    }
    return arr;
  }

  public static <T> boolean exists(Collection<T> col, Func1<T, Boolean> m) {
    if (col == null) return false;
    for (T c : col) {
      if (m.call(c)) return true;
    }
    return false;
  }

  public static <T> boolean equals(
      Collection<T> source, Collection<T> newSource, Func2<T, T, Boolean> function2) {
    if (Cu.isEmpty(source) && Cu.isEmpty(newSource)) {
      return true;
    }
    if (newSource.size() != source.size()) {
      return false;
    }
    for (T t : newSource) {
      if (!exists(source, t1 -> function2.call(t, t1))) {
        return false;
      }
    }
    return true;
  }

  public static <T> ArrayList<T> filter(Collection<T> col, Func1<T, Boolean> m) {
    ArrayList<T> arr = new ArrayList<>();
    if (col == null) return arr;
    for (T c : col) {
      if (m.call(c)) arr.add(c);
    }
    return arr;
  }

  public static <T> int count(List<T> list, Func1<T, Boolean> pred) {
    return filter(list, pred).size();
  }

  public static <T> long sum(Iterable<T> list, Func1<T, Long> evaluator) {
    long sum = 0;
    for (T t : list) {
      sum += evaluator.call(t);
    }
    return sum;
  }

  public static <T> long sum(T[] list, Func1<T, Long> evaluator) {
    long sum = 0;
    for (T t : list) {
      sum += evaluator.call(t);
    }
    return sum;
  }

  public static <T> void foreach(Collection<T> col, Action1<T> m) {
    for (T c : col) {
      m.call(c);
    }
  }

  public static <T> void foreach(T[] col, Action1<T> m) {
    for (T c : col) {
      m.call(c);
    }
  }

  public static <T> boolean forall(Collection<T> col, Func1<T, Boolean> m) {
    for (T c : col) {
      if (!m.call(c)) return false;
    }
    return true;
  }

  public static <T> List<Pair<T, Integer>> zipWithIndex(Collection<T> col) {
    ArrayList<Pair<T, Integer>> l = new ArrayList<>(col.size());
    int i = 0;
    for (T t : col) {
      l.add(pair(t, i++));
    }
    return l;
  }

  public static final int longStringCompare(String lid, String rid) {
    int dl = lid.length() - rid.length();
    if (dl != 0) {
      return dl;
    } else if (lid.length() > 0) {
      for (int i = 0; i < lid.length(); i++) {
        int dp = lid.charAt(i) - rid.charAt(i);
        if (dp != 0) {
          return dp;
        }
      }
      return 0;
    } else {
      return 0;
    }
  }

  public static <T> void insertionSort(List<T> list, Comparator<? super T> comp) {
    for (int i = 1; i < list.size(); i++) {
      T temp = list.get(i);
      int j;
      for (j = i - 1; j >= 0 && comp.compare(temp, list.get(j)) < 0; j--) {
        list.set(j + 1, list.get(j));
      }
      list.set(j + 1, temp);
    }
  }

  public static <T> void singleInsertionSort(List<T> list, T single, Comparator<? super T> comp) {
    int pos = list.size();
    for (int i = 0; i < list.size(); i++) {
      if (comp.compare(list.get(i), single) < 0) {
        pos = i;
        break;
      }
    }
    list.add(pos, single);
  }

  public static <T> String mkString(Collection<T> os, String s) {
    if (os == null) return null;
    StringBuilder sb = new StringBuilder();
    for (Object o : os) {
      if (o != null) {
        sb.append(o.toString());
        sb.append(s);
      }
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - s.length());
    }
    return sb.toString();
  }

  public static String mkString(Object[] os, String s) {
    StringBuilder sb = new StringBuilder();
    for (Object o : os) {
      sb.append(o.toString());
      sb.append(s);
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - s.length());
    }
    return sb.toString();
  }

  public static String nullIfEmpty(String s) {
    if (TextUtils.isEmpty(s)) {
      return null;
    } else {
      return s;
    }
  }

  public static ArrayList<Point> regexMatchPositions(Matcher matcher) {
    ArrayList<Point> matches = new ArrayList<>();
    while (matcher.find()) {
      matches.add(new Point(matcher.start(), matcher.end()));
    }
    return matches;
  }

  public static String emptyIfNullOrSpaces(String s) {
    if (isEmptyOrSpaces(s)) {
      return "";
    } else {
      return s;
    }
  }

  public static boolean isEmpty(Collection c) {
    return c == null || c.isEmpty();
  }

  public static <T> boolean isEmpty(T[] c) {
    return c == null || c.length == 0;
  }

  public static boolean isEmptyOrSpaces(String s) {
    return s == null || s.trim().isEmpty();
  }

  public static String filterNumbers(String org) {
    Pattern pattern = Pattern.compile("[^0-9]");
    return pattern.matcher(org).replaceAll("").toString();
  }

  public static <T> int countSame(List<T> au, List<T> ame) {
    int c = 0;
    for (T l : au) {
      for (T b : ame) {
        if (l.equals(b)) {
          c++;
        }
      }
    }
    return c;
  }

  public static <T> List<T> take(List<T> list, Func1<T, Boolean> pred) {
    int i = 0;
    for (; i < list.size(); i++) {
      if (!pred.call(list.get(i))) {
        break;
      }
    }
    return list.subList(0, i);
  }

  public static <T> List<T> takeUntil(List<T> list, Func1<T, Boolean> pred) {
    int i = 0;
    for (; i < list.size(); i++) {
      if (pred.call(list.get(i))) {
        break;
      }
    }
    return list.subList(0, i);
  }

  public static <T> HashMap<String, ArrayList<T>> groupByString(
      List<T> data, Func1<T, String> func) {
    HashMap<String, ArrayList<T>> hash = new HashMap<>();
    for (T t : data) {
      String key = func.call(t);
      ArrayList<T> list = hash.get(key);
      if (list == null) {
        list = new ArrayList<>();
        hash.put(key, list);
      }
      list.add(t);
    }
    return hash;
  }

  public static <T> ArrayList<List<T>> grouped(List<T> data, int i) {
    ArrayList<List<T>> gs = new ArrayList<>();
    while (gs.size() * i < data.size()) {
      gs.add(
          new ArrayList<T>(
              data.subList(gs.size() * i, Math.min(gs.size() * (i + 1), data.size()))));
    }
    return gs;
  }

  public static <T, V> ArrayList<V> flatMap(List<T> list, Func1<T, List<V>> map) {
    ArrayList<V> res = new ArrayList<>();
    for (T l : list) {
      res.addAll(map.call(l));
    }
    return res;
  }

  public static <T> List<T> flatten(List<List<T>> lists) {
    ArrayList<T> res = new ArrayList<>();
    for (List<T> l : lists) {
      res.addAll(l);
    }
    return res;
  }

  public static <T> List<T> flatten(List<T>... lists) {
    ArrayList<T> res = new ArrayList<>();
    for (List<T> l : lists) {
      res.addAll(l);
    }
    return res;
  }

  public static CharSequence uncaps(CharSequence region) {
    if (TextUtils.isEmpty(region)) {
      return region;
    } else {
      return "" + Character.toLowerCase(region.charAt(0)) + region.subSequence(1, region.length());
    }
  }

  public static List<Integer> range(int count) {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      list.add(i);
    }
    return list;
  }

  public static <T> void use(T a, Action1<T> map) {
    map.call(a);
  }

  public static String dropRight(String str, int i) {
    return str.substring(0, Math.max(0, str.length() - i));
  }

  public static String drop(String str, int i) {
    return str.substring(Math.min(i, str.length()), str.length());
  }

  public static String drop(String str, String i) {
    if (str.startsWith(i)) {
      return str.substring(i.length(), str.length());
    } else {
      return str;
    }
  }

  public static <T> T last(List<T> value) {
    return value.get(value.size() - 1);
  }

  public static <T> T lastOrNull(List<T> value) {
    return value.isEmpty() ? null : value.get(value.size() - 1);
  }

  public static <T> List<T> filterAdd(List<T> base, T add) {
    if (!base.contains(add)) {
      base.add(add);
    }
    return base;
  }

  public static <T> List<T> filterAddAll(List<T> base, List<T> add) {
    for (T t : add) {
      if (!base.contains(t)) {
        base.add(t);
      }
    }
    return base;
  }

  public static <T> List<T> filterAddTop(List<T> base, List<T> add) {
    for (int i = add.size() - 1; i >= 0; i--) {
      if (!base.contains(add.get(i))) {
        base.add(0, add.get(i));
      }
    }
    return base;
  }

  public static <T> List<T> remove(List<T> ids, T s) {
    ArrayList<T> l = new ArrayList<>(ids);
    l.remove(s);
    return l;
  }

  public static <T> List<T> add(List<T> ids, T s) {
    ArrayList<T> l = new ArrayList<>(ids);
    l.add(s);
    return l;
  }

  public static <T> Set<T> add(Set<T> set, T... ts) {
    Set<T> result = new HashSet<>(set.size() + ts.length);
    result.addAll(set);
    result.addAll(Arrays.asList(ts));
    return result;
  }

  @SafeVarargs
  public static <T> List<T> concat(List<T> ids, List<T>... map) {
    ArrayList<T> l = new ArrayList<>(ids);
    for (List<T> m : map) {
      l.addAll(m);
    }
    return l;
  }

  public static int indexOfArray(Object[] a, Object o) {
    for (int i = 0; i < a.length; i++) {
      if (a[i].equals(o)) return i;
    }
    return 0;
  }

  public static int sum(List<Integer> map) {
    int total = 0;
    for (Integer i : map) total += i;
    return total;
  }

  public static <A, T> T getOrPut(HashMap<A, T> cs, A cid, Func0<T> query) {
    T res = cs.get(cid);
    if (res != null) {
      return res;
    } else {
      res = query.call();
      cs.put(cid, res);
      return res;
    }
  }

  public static <T> List<T> intersection(List<T> l1, List<T> l2) {
    List<T> r = new ArrayList<>();
    for (T t1 : l1) {
      for (T t2 : l2) {
        if (t1.equals(t2)) r.add(t1);
      }
    }
    return r;
  }

  public static byte[] concat(byte[]... bss) {
    int l = 0;
    for (int i = 0; i < bss.length; i++) l += bss[i].length;
    byte[] bs = new byte[l];
    int k = 0;
    for (int i = 0; i < bss.length; i++) {
      for (int j = 0; j < bss[i].length; j++) {
        bs[k++] = bss[i][j];
      }
    }
    return bs;
  }

  public static byte[] compact(boolean... bs) {
    byte[] res = new byte[(bs.length + 7) / 8];
    for (int i = 0; i < bs.length; i++) {
      res[i / 8] = (byte) (res[i / 8] | ((bs[i] ? 1 : 0) << (7 - i % 8)));
    }
    return res;
  }

  public static <E> List<E> sub(Collection<E> left, Collection<E> right) {
    ArrayList<E> list = new ArrayList<>();
    for (E e : left) {
      if (!right.contains(e)) {
        list.add(e);
      }
    }
    return list;
  }

  public static <T, K, R> ArrayList<R> mapReduce(
      List<? extends T> list, Func1<? super T, K> mapper, Func2<K, List<T>, R> reducer) {
    final int size = list.size();
    final HashMap<K, List<T>> groups = new HashMap<>(size);
    for (T t : list) {
      final K k = mapper.call(t);
      List<T> g = groups.get(k);
      if (g == null) {
        g = new ArrayList<>(list.size());
        groups.put(k, g);
      }
      g.add(t);
    }

    final ArrayList<R> result = new ArrayList<>(groups.size());
    for (Map.Entry<K, List<T>> e : groups.entrySet()) {
      final R r = reducer.call(e.getKey(), e.getValue());
      if (r != null) {
        result.add(r);
      }
    }
    return result;
  }

  public static <E> String toString(Collection<E> list, Func1<E, String> stringer, String joiner) {
    if (list == null) return null;
    if (list.isEmpty()) return "";
    if (joiner == null) joiner = "";
    final StringBuilder builder = new StringBuilder();
    for (E e : list) {
      builder.append(stringer.call(e)).append(joiner);
    }
    return builder.substring(0, builder.length() - joiner.length());
  }

  public static <E> Set<E> toDistinctSet(List<E> sourceList) {
    Set<E> set = new LinkedHashSet<>();
    set.addAll(sourceList);
    return set;
  }

  public static <E, T> List<E> distinctBy(List<E> sourceList, Func1<E, T> fun1) {
    LinkedHashMap<T, E> map = new LinkedHashMap<>();
    Cu.foreach(sourceList, item -> map.put(fun1.call(item), item));
    return new ArrayList<>(map.values());
  }

  public static int findArrayMax(@NonNull int[] array) {
    int max = array[0];
    for (int value : array) {
      if (value > max) {
        max = value;
      }
    }
    return max;
  }

  public static int findArrayMin(@NonNull int[] array) {
    int min = array[0];
    for (int value : array) {
      if (value < min) {
        min = value;
      }
    }
    return min;
  }
}
