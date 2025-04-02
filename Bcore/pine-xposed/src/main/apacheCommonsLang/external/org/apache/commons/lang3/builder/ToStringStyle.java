/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package external.org.apache.commons.lang3.builder;

import external.org.apache.commons.lang3.ClassUtils;
import external.org.apache.commons.lang3.ObjectUtils;
import external.org.apache.commons.lang3.SystemUtils;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Controls <code>String</code> formatting for {@link ToStringBuilder}. The main public interface is
 * always via <code>ToStringBuilder</code>.
 *
 * <p>These classes are intended to be used as <code>Singletons</code>. There is no need to
 * instantiate a new style each time. A program will generally use one of the predefined constants
 * on this class. Alternatively, the {@link StandardToStringStyle} class can be used to set the
 * individual settings. Thus most styles can be achieved without subclassing.
 *
 * <p>If required, a subclass can override as many or as few of the methods as it requires. Each
 * object type (from <code>boolean</code> to <code>long</code> to <code>Object</code> to <code>int[]
 * </code>) has its own methods to output it. Most have two versions, detail and summary.
 *
 * <p>For example, the detail version of the array based methods will output the whole array,
 * whereas the summary method will just output the array length.
 *
 * <p>If you want to format the output of certain objects, such as dates, you must create a subclass
 * and override a method.
 *
 * <pre>
 * public class MyStyle extends ToStringStyle {
 *   protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
 *     if (value instanceof Date) {
 *       value = new SimpleDateFormat("yyyy-MM-dd").format(value);
 *     }
 *     buffer.append(value);
 *   }
 * }
 * </pre>
 *
 * @since 1.0
 * @version $Id: ToStringStyle.java 1091066 2011-04-11 13:30:11Z mbenson $
 */
public abstract class ToStringStyle implements Serializable {

  /** Serialization version ID. */
  private static final long serialVersionUID = -2587890625525655916L;

  /**
   * The default toString style. Using the Using the <code>Person</code> example from {@link
   * ToStringBuilder}, the output would look like this:
   *
   * <pre>
   * Person@182f0db[name=John Doe,age=33,smoker=false]
   * </pre>
   */
  public static final ToStringStyle DEFAULT_STYLE = new DefaultToStringStyle();

  /**
   * The multi line toString style. Using the Using the <code>Person</code> example from {@link
   * ToStringBuilder}, the output would look like this:
   *
   * <pre>
   * Person@182f0db[
   *   name=John Doe
   *   age=33
   *   smoker=false
   * ]
   * </pre>
   */
  public static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();

  /**
   * The no field names toString style. Using the Using the <code>Person</code> example from {@link
   * ToStringBuilder}, the output would look like this:
   *
   * <pre>
   * Person@182f0db[John Doe,33,false]
   * </pre>
   */
  public static final ToStringStyle NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();

  /**
   * The short prefix toString style. Using the <code>Person</code> example from {@link
   * ToStringBuilder}, the output would look like this:
   *
   * <pre>
   * Person[name=John Doe,age=33,smoker=false]
   * </pre>
   *
   * @since 2.1
   */
  public static final ToStringStyle SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();

  /**
   * The simple toString style. Using the Using the <code>Person</code> example from {@link
   * ToStringBuilder}, the output would look like this:
   *
   * <pre>
   * John Doe,33,false
   * </pre>
   */
  public static final ToStringStyle SIMPLE_STYLE = new SimpleToStringStyle();

  /**
   * A registry of objects used by <code>reflectionToString</code> methods to detect cyclical object
   * references and avoid infinite loops.
   */
  private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY =
      new ThreadLocal<WeakHashMap<Object, Object>>();

  /**
   * Returns the registry of objects being traversed by the <code>reflectionToString</code> methods
   * in the current thread.
   *
   * @return Set the registry of objects being traversed
   */
  static Map<Object, Object> getRegistry() {
    return REGISTRY.get();
  }

  /**
   * Returns <code>true</code> if the registry contains the given object. Used by the reflection
   * methods to avoid infinite loops.
   *
   * @param value The object to lookup in the registry.
   * @return boolean <code>true</code> if the registry contains the given object.
   */
  static boolean isRegistered(Object value) {
    Map<Object, Object> m = getRegistry();
    return m != null && m.containsKey(value);
  }

  /**
   * Registers the given object. Used by the reflection methods to avoid infinite loops.
   *
   * @param value The object to register.
   */
  static void register(Object value) {
    if (value != null) {
      Map<Object, Object> m = getRegistry();
      if (m == null) {
        REGISTRY.set(new WeakHashMap<Object, Object>());
      }
      getRegistry().put(value, null);
    }
  }

  /**
   * Unregisters the given object.
   *
   * <p>Used by the reflection methods to avoid infinite loops.
   *
   * @param value The object to unregister.
   */
  static void unregister(Object value) {
    if (value != null) {
      Map<Object, Object> m = getRegistry();
      if (m != null) {
        m.remove(value);
        if (m.isEmpty()) {
          REGISTRY.remove();
        }
      }
    }
  }

  /** Whether to use the field names, the default is <code>true</code>. */
  private boolean useFieldNames = true;

  /** Whether to use the class name, the default is <code>true</code>. */
  private boolean useClassName = true;

  /** Whether to use short class names, the default is <code>false</code>. */
  private boolean useShortClassName = false;

  /** Whether to use the identity hash code, the default is <code>true</code>. */
  private boolean useIdentityHashCode = true;

  /** The content start <code>'['</code>. */
  private String contentStart = "[";

  /** The content end <code>']'</code>. */
  private String contentEnd = "]";

  /** The field name value separator <code>'='</code>. */
  private String fieldNameValueSeparator = "=";

  /** Whether the field separator should be added before any other fields. */
  private boolean fieldSeparatorAtStart = false;

  /** Whether the field separator should be added after any other fields. */
  private boolean fieldSeparatorAtEnd = false;

  /** The field separator <code>','</code>. */
  private String fieldSeparator = ",";

  /** The array start <code>'{'</code>. */
  private String arrayStart = "{";

  /** The array separator <code>','</code>. */
  private String arraySeparator = ",";

  /** The detail for array content. */
  private boolean arrayContentDetail = true;

  /** The array end <code>'}'</code>. */
  private String arrayEnd = "}";

  /**
   * The value to use when fullDetail is <code>null</code>, the default value is <code>true</code>.
   */
  private boolean defaultFullDetail = true;

  /** The <code>null</code> text <code>'&lt;null&gt;'</code>. */
  private String nullText = "<null>";

  /** The summary size text start <code>'<size'</code>. */
  private String sizeStartText = "<size=";

  /** The summary size text start <code>'&gt;'</code>. */
  private String sizeEndText = ">";

  /** The summary object text start <code>'&lt;'</code>. */
  private String summaryObjectStartText = "<";

  /** The summary object text start <code>'&gt;'</code>. */
  private String summaryObjectEndText = ">";

  // ----------------------------------------------------------------------------

  /** Constructor. */
  protected ToStringStyle() {
    super();
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> the superclass toString.
   *
   * <p>NOTE: It assumes that the toString has been created from the same ToStringStyle.
   *
   * <p>A <code>null</code> <code>superToString</code> is ignored.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param superToString the <code>super.toString()</code>
   * @since 2.0
   */
  public void appendSuper(StringBuffer buffer, String superToString) {
    appendToString(buffer, superToString);
  }

  /**
   * Append to the <code>toString</code> another toString.
   *
   * <p>NOTE: It assumes that the toString has been created from the same ToStringStyle.
   *
   * <p>A <code>null</code> <code>toString</code> is ignored.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param toString the additional <code>toString</code>
   * @since 2.0
   */
  public void appendToString(StringBuffer buffer, String toString) {
    if (toString != null) {
      int pos1 = toString.indexOf(contentStart) + contentStart.length();
      int pos2 = toString.lastIndexOf(contentEnd);
      if (pos1 != pos2 && pos1 >= 0 && pos2 >= 0) {
        String data = toString.substring(pos1, pos2);
        if (fieldSeparatorAtStart) {
          removeLastFieldSeparator(buffer);
        }
        buffer.append(data);
        appendFieldSeparator(buffer);
      }
    }
  }

  /**
   * Append to the <code>toString</code> the start of data indicator.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param object the <code>Object</code> to build a <code>toString</code> for
   */
  public void appendStart(StringBuffer buffer, Object object) {
    if (object != null) {
      appendClassName(buffer, object);
      appendIdentityHashCode(buffer, object);
      appendContentStart(buffer);
      if (fieldSeparatorAtStart) {
        appendFieldSeparator(buffer);
      }
    }
  }

  /**
   * Append to the <code>toString</code> the end of data indicator.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param object the <code>Object</code> to build a <code>toString</code> for.
   */
  public void appendEnd(StringBuffer buffer, Object object) {
    if (this.fieldSeparatorAtEnd == false) {
      removeLastFieldSeparator(buffer);
    }
    appendContentEnd(buffer);
    unregister(object);
  }

  /**
   * Remove the last field separator from the buffer.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @since 2.0
   */
  protected void removeLastFieldSeparator(StringBuffer buffer) {
    int len = buffer.length();
    int sepLen = fieldSeparator.length();
    if (len > 0 && sepLen > 0 && len >= sepLen) {
      boolean match = true;
      for (int i = 0; i < sepLen; i++) {
        if (buffer.charAt(len - 1 - i) != fieldSeparator.charAt(sepLen - 1 - i)) {
          match = false;
          break;
        }
      }
      if (match) {
        buffer.setLength(len - sepLen);
      }
    }
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>Object</code> value, printing the full <code>
   * toString</code> of the <code>Object</code> passed in.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (value == null) {
      appendNullText(buffer, fieldName);

    } else {
      appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code>, correctly interpreting its type.
   *
   * <p>This method performs the main lookup by Class type to correctly route arrays, <code>
   * Collections</code>, <code>Maps</code> and <code>Objects</code> to the appropriate method.
   *
   * <p>Either detail or summary views can be specified.
   *
   * <p>If a cycle is detected, an object will be appended with the <code>Object.toString()</code>
   * format.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>, not <code>null</code>
   * @param detail output detail or not
   */
  protected void appendInternal(
      StringBuffer buffer, String fieldName, Object value, boolean detail) {
    if (isRegistered(value)
        && !(value instanceof Number || value instanceof Boolean || value instanceof Character)) {
      appendCyclicObject(buffer, fieldName, value);
      return;
    }

    register(value);

    try {
      if (value instanceof Collection<?>) {
        if (detail) {
          appendDetail(buffer, fieldName, (Collection<?>) value);
        } else {
          appendSummarySize(buffer, fieldName, ((Collection<?>) value).size());
        }

      } else if (value instanceof Map<?, ?>) {
        if (detail) {
          appendDetail(buffer, fieldName, (Map<?, ?>) value);
        } else {
          appendSummarySize(buffer, fieldName, ((Map<?, ?>) value).size());
        }

      } else if (value instanceof long[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (long[]) value);
        } else {
          appendSummary(buffer, fieldName, (long[]) value);
        }

      } else if (value instanceof int[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (int[]) value);
        } else {
          appendSummary(buffer, fieldName, (int[]) value);
        }

      } else if (value instanceof short[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (short[]) value);
        } else {
          appendSummary(buffer, fieldName, (short[]) value);
        }

      } else if (value instanceof byte[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (byte[]) value);
        } else {
          appendSummary(buffer, fieldName, (byte[]) value);
        }

      } else if (value instanceof char[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (char[]) value);
        } else {
          appendSummary(buffer, fieldName, (char[]) value);
        }

      } else if (value instanceof double[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (double[]) value);
        } else {
          appendSummary(buffer, fieldName, (double[]) value);
        }

      } else if (value instanceof float[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (float[]) value);
        } else {
          appendSummary(buffer, fieldName, (float[]) value);
        }

      } else if (value instanceof boolean[]) {
        if (detail) {
          appendDetail(buffer, fieldName, (boolean[]) value);
        } else {
          appendSummary(buffer, fieldName, (boolean[]) value);
        }

      } else if (value.getClass().isArray()) {
        if (detail) {
          appendDetail(buffer, fieldName, (Object[]) value);
        } else {
          appendSummary(buffer, fieldName, (Object[]) value);
        }

      } else {
        if (detail) {
          appendDetail(buffer, fieldName, value);
        } else {
          appendSummary(buffer, fieldName, value);
        }
      }
    } finally {
      unregister(value);
    }
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> value that has been detected to
   * participate in a cycle. This implementation will print the standard string value of the value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>, not <code>null</code>
   * @since 2.2
   */
  protected void appendCyclicObject(StringBuffer buffer, String fieldName, Object value) {
    ObjectUtils.identityToString(buffer, value);
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> value, printing the full detail of
   * the <code>Object</code>.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
    buffer.append(value);
  }

  /**
   * Append to the <code>toString</code> a <code>Collection</code>.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param coll the <code>Collection</code> to add to the <code>toString</code>, not <code>null
   *     </code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, Collection<?> coll) {
    buffer.append(coll);
  }

  /**
   * <p>Append to the <code>toString</code> a <code>Map<code>.</p>
   *
   * @param buffer  the <code>StringBuffer</code> to populate
   * @param fieldName  the field name, typically not used as already appended
   * @param map  the <code>Map</code> to add to the <code>toString</code>,
   *  not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, Map<?, ?> map) {
    buffer.append(map);
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> value, printing a summary of the
   * <code>Object</code>.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, Object value) {
    buffer.append(summaryObjectStartText);
    buffer.append(getShortClassName(value.getClass()));
    buffer.append(summaryObjectEndText);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>long</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, long value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>long</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, long value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>int</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, int value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> an <code>int</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, int value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>short</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, short value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>short</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, short value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>byte</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, byte value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>byte</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, byte value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>char</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, char value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>char</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, char value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>double</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, double value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>double</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, double value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>float</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, float value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>float</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, float value) {
    buffer.append(value);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>boolean</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   */
  public void append(StringBuffer buffer, String fieldName, boolean value) {
    appendFieldStart(buffer, fieldName);
    appendDetail(buffer, fieldName, value);
    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> a <code>boolean</code> value.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param value the value to add to the <code>toString</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, boolean value) {
    buffer.append(value);
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the toString
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> the detail of an <code>Object</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      Object item = array[i];
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      if (item == null) {
        appendNullText(buffer, fieldName);

      } else {
        appendInternal(buffer, fieldName, item, arrayContentDetail);
      }
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> the detail of an array type.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   * @since 2.0
   */
  protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
    buffer.append(arrayStart);
    int length = Array.getLength(array);
    for (int i = 0; i < length; i++) {
      Object item = Array.get(array, i);
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      if (item == null) {
        appendNullText(buffer, fieldName);

      } else {
        appendInternal(buffer, fieldName, item, arrayContentDetail);
      }
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of an <code>Object</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, Object[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>long</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>long</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>long</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, long[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>int</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of an <code>int</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of an <code>int</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, int[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>short</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>short</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>short</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, short[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>byte</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>byte</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>byte</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, byte[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>char</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>char</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>char</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, char[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>double</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the toString
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>double</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>double</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, double[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>float</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the toString
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>float</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>float</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, float[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>boolean</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   * @param array the array to add to the toString
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info, <code>null
   *     </code> for style decides
   */
  public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {
    appendFieldStart(buffer, fieldName);

    if (array == null) {
      appendNullText(buffer, fieldName);

    } else if (isFullDetail(fullDetail)) {
      appendDetail(buffer, fieldName, array);

    } else {
      appendSummary(buffer, fieldName, array);
    }

    appendFieldEnd(buffer, fieldName);
  }

  /**
   * Append to the <code>toString</code> the detail of a <code>boolean</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
    buffer.append(arrayStart);
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        buffer.append(arraySeparator);
      }
      appendDetail(buffer, fieldName, array[i]);
    }
    buffer.append(arrayEnd);
  }

  /**
   * Append to the <code>toString</code> a summary of a <code>boolean</code> array.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   * @param array the array to add to the <code>toString</code>, not <code>null</code>
   */
  protected void appendSummary(StringBuffer buffer, String fieldName, boolean[] array) {
    appendSummarySize(buffer, fieldName, array.length);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> the class name.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param object the <code>Object</code> whose name to output
   */
  protected void appendClassName(StringBuffer buffer, Object object) {
    if (useClassName && object != null) {
      register(object);
      if (useShortClassName) {
        buffer.append(getShortClassName(object.getClass()));
      } else {
        buffer.append(object.getClass().getName());
      }
    }
  }

  /**
   * Append the {@link System#identityHashCode(java.lang.Object)}.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param object the <code>Object</code> whose id to output
   */
  protected void appendIdentityHashCode(StringBuffer buffer, Object object) {
    if (this.isUseIdentityHashCode() && object != null) {
      register(object);
      buffer.append('@');
      buffer.append(Integer.toHexString(System.identityHashCode(object)));
    }
  }

  /**
   * Append to the <code>toString</code> the content start.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   */
  protected void appendContentStart(StringBuffer buffer) {
    buffer.append(contentStart);
  }

  /**
   * Append to the <code>toString</code> the content end.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   */
  protected void appendContentEnd(StringBuffer buffer) {
    buffer.append(contentEnd);
  }

  /**
   * Append to the <code>toString</code> an indicator for <code>null</code>.
   *
   * <p>The default indicator is <code>'&lt;null&gt;'</code>.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name, typically not used as already appended
   */
  protected void appendNullText(StringBuffer buffer, String fieldName) {
    buffer.append(nullText);
  }

  /**
   * Append to the <code>toString</code> the field separator.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   */
  protected void appendFieldSeparator(StringBuffer buffer) {
    buffer.append(fieldSeparator);
  }

  /**
   * Append to the <code>toString</code> the field start.
   *
   * @param buffer the <code>StringBuffer</code> to populate
   * @param fieldName the field name
   */
  protected void appendFieldStart(StringBuffer buffer, String fieldName) {
    if (useFieldNames && fieldName != null) {
      buffer.append(fieldName);
      buffer.append(fieldNameValueSeparator);
    }
  }

  /**
   * <p>Append to the <code>toString<code> the field end.</p>
   *
   * @param buffer  the <code>StringBuffer</code> to populate
   * @param fieldName  the field name, typically not used as already appended
   */
  protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
    appendFieldSeparator(buffer);
  }

  /**
   * <p>Append to the <code>toString</code> a size summary.</p>
   *
   * <p>The size summary is used to summarize the contents of
   * <code>Collections</code>, <code>Maps</code> and arrays.</p>
   *
   * <p>The output consists of a prefix, the passed in size
   * and a suffix.</p>
   *
   * <p>The default format is <code>'&lt;size=n&gt;'<code>.</p>
   *
   * @param buffer  the <code>StringBuffer</code> to populate
   * @param fieldName  the field name, typically not used as already appended
   * @param size  the size to append
   */
  protected void appendSummarySize(StringBuffer buffer, String fieldName, int size) {
    buffer.append(sizeStartText);
    buffer.append(size);
    buffer.append(sizeEndText);
  }

  /**
   * Is this field to be output in full detail.
   *
   * <p>This method converts a detail request into a detail level. The calling code may request full
   * detail (<code>true</code>), but a subclass might ignore that and always return <code>false
   * </code>. The calling code may pass in <code>null</code> indicating that it doesn't care about
   * the detail level. In this case the default detail level is used.
   *
   * @param fullDetailRequest the detail level requested
   * @return whether full detail is to be shown
   */
  protected boolean isFullDetail(Boolean fullDetailRequest) {
    if (fullDetailRequest == null) {
      return defaultFullDetail;
    }
    return fullDetailRequest.booleanValue();
  }

  /**
   * Gets the short class name for a class.
   *
   * <p>The short class name is the classname excluding the package name.
   *
   * @param cls the <code>Class</code> to get the short name of
   * @return the short name
   */
  protected String getShortClassName(Class<?> cls) {
    return ClassUtils.getShortClassName(cls);
  }

  // Setters and getters for the customizable parts of the style
  // These methods are not expected to be overridden, except to make public
  // (They are not public so that immutable subclasses can be written)
  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the class name.
   *
   * @return the current useClassName flag
   */
  protected boolean isUseClassName() {
    return useClassName;
  }

  /**
   * Sets whether to use the class name.
   *
   * @param useClassName the new useClassName flag
   */
  protected void setUseClassName(boolean useClassName) {
    this.useClassName = useClassName;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to output short or long class names.
   *
   * @return the current useShortClassName flag
   * @since 2.0
   */
  protected boolean isUseShortClassName() {
    return useShortClassName;
  }

  /**
   * Sets whether to output short or long class names.
   *
   * @param useShortClassName the new useShortClassName flag
   * @since 2.0
   */
  protected void setUseShortClassName(boolean useShortClassName) {
    this.useShortClassName = useShortClassName;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the identity hash code.
   *
   * @return the current useIdentityHashCode flag
   */
  protected boolean isUseIdentityHashCode() {
    return useIdentityHashCode;
  }

  /**
   * Sets whether to use the identity hash code.
   *
   * @param useIdentityHashCode the new useIdentityHashCode flag
   */
  protected void setUseIdentityHashCode(boolean useIdentityHashCode) {
    this.useIdentityHashCode = useIdentityHashCode;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the field names passed in.
   *
   * @return the current useFieldNames flag
   */
  protected boolean isUseFieldNames() {
    return useFieldNames;
  }

  /**
   * Sets whether to use the field names passed in.
   *
   * @param useFieldNames the new useFieldNames flag
   */
  protected void setUseFieldNames(boolean useFieldNames) {
    this.useFieldNames = useFieldNames;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use full detail when the caller doesn't specify.
   *
   * @return the current defaultFullDetail flag
   */
  protected boolean isDefaultFullDetail() {
    return defaultFullDetail;
  }

  /**
   * Sets whether to use full detail when the caller doesn't specify.
   *
   * @param defaultFullDetail the new defaultFullDetail flag
   */
  protected void setDefaultFullDetail(boolean defaultFullDetail) {
    this.defaultFullDetail = defaultFullDetail;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to output array content detail.
   *
   * @return the current array content detail setting
   */
  protected boolean isArrayContentDetail() {
    return arrayContentDetail;
  }

  /**
   * Sets whether to output array content detail.
   *
   * @param arrayContentDetail the new arrayContentDetail flag
   */
  protected void setArrayContentDetail(boolean arrayContentDetail) {
    this.arrayContentDetail = arrayContentDetail;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array start text.
   *
   * @return the current array start text
   */
  protected String getArrayStart() {
    return arrayStart;
  }

  /**
   * Sets the array start text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arrayStart the new array start text
   */
  protected void setArrayStart(String arrayStart) {
    if (arrayStart == null) {
      arrayStart = "";
    }
    this.arrayStart = arrayStart;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array end text.
   *
   * @return the current array end text
   */
  protected String getArrayEnd() {
    return arrayEnd;
  }

  /**
   * Sets the array end text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arrayEnd the new array end text
   */
  protected void setArrayEnd(String arrayEnd) {
    if (arrayEnd == null) {
      arrayEnd = "";
    }
    this.arrayEnd = arrayEnd;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array separator text.
   *
   * @return the current array separator text
   */
  protected String getArraySeparator() {
    return arraySeparator;
  }

  /**
   * Sets the array separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arraySeparator the new array separator text
   */
  protected void setArraySeparator(String arraySeparator) {
    if (arraySeparator == null) {
      arraySeparator = "";
    }
    this.arraySeparator = arraySeparator;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the content start text.
   *
   * @return the current content start text
   */
  protected String getContentStart() {
    return contentStart;
  }

  /**
   * Sets the content start text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param contentStart the new content start text
   */
  protected void setContentStart(String contentStart) {
    if (contentStart == null) {
      contentStart = "";
    }
    this.contentStart = contentStart;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the content end text.
   *
   * @return the current content end text
   */
  protected String getContentEnd() {
    return contentEnd;
  }

  /**
   * Sets the content end text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param contentEnd the new content end text
   */
  protected void setContentEnd(String contentEnd) {
    if (contentEnd == null) {
      contentEnd = "";
    }
    this.contentEnd = contentEnd;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the field name value separator text.
   *
   * @return the current field name value separator text
   */
  protected String getFieldNameValueSeparator() {
    return fieldNameValueSeparator;
  }

  /**
   * Sets the field name value separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param fieldNameValueSeparator the new field name value separator text
   */
  protected void setFieldNameValueSeparator(String fieldNameValueSeparator) {
    if (fieldNameValueSeparator == null) {
      fieldNameValueSeparator = "";
    }
    this.fieldNameValueSeparator = fieldNameValueSeparator;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the field separator text.
   *
   * @return the current field separator text
   */
  protected String getFieldSeparator() {
    return fieldSeparator;
  }

  /**
   * Sets the field separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param fieldSeparator the new field separator text
   */
  protected void setFieldSeparator(String fieldSeparator) {
    if (fieldSeparator == null) {
      fieldSeparator = "";
    }
    this.fieldSeparator = fieldSeparator;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether the field separator should be added at the start of each buffer.
   *
   * @return the fieldSeparatorAtStart flag
   * @since 2.0
   */
  protected boolean isFieldSeparatorAtStart() {
    return fieldSeparatorAtStart;
  }

  /**
   * Sets whether the field separator should be added at the start of each buffer.
   *
   * @param fieldSeparatorAtStart the fieldSeparatorAtStart flag
   * @since 2.0
   */
  protected void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart) {
    this.fieldSeparatorAtStart = fieldSeparatorAtStart;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether the field separator should be added at the end of each buffer.
   *
   * @return fieldSeparatorAtEnd flag
   * @since 2.0
   */
  protected boolean isFieldSeparatorAtEnd() {
    return fieldSeparatorAtEnd;
  }

  /**
   * Sets whether the field separator should be added at the end of each buffer.
   *
   * @param fieldSeparatorAtEnd the fieldSeparatorAtEnd flag
   * @since 2.0
   */
  protected void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd) {
    this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the text to output when <code>null</code> found.
   *
   * @return the current text to output when null found
   */
  protected String getNullText() {
    return nullText;
  }

  /**
   * Sets the text to output when <code>null</code> found.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param nullText the new text to output when null found
   */
  protected void setNullText(String nullText) {
    if (nullText == null) {
      nullText = "";
    }
    this.nullText = nullText;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the start text to output when a <code>Collection</code>, <code>Map</code> or array size is
   * output.
   *
   * <p>This is output before the size value.
   *
   * @return the current start of size text
   */
  protected String getSizeStartText() {
    return sizeStartText;
  }

  /**
   * Sets the start text to output when a <code>Collection</code>, <code>Map</code> or array size is
   * output.
   *
   * <p>This is output before the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param sizeStartText the new start of size text
   */
  protected void setSizeStartText(String sizeStartText) {
    if (sizeStartText == null) {
      sizeStartText = "";
    }
    this.sizeStartText = sizeStartText;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the end text to output when a <code>Collection</code>, <code>Map</code> or array size is
   * output.
   *
   * <p>This is output after the size value.
   *
   * @return the current end of size text
   */
  protected String getSizeEndText() {
    return sizeEndText;
  }

  /**
   * Sets the end text to output when a <code>Collection</code>, <code>Map</code> or array size is
   * output.
   *
   * <p>This is output after the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param sizeEndText the new end of size text
   */
  protected void setSizeEndText(String sizeEndText) {
    if (sizeEndText == null) {
      sizeEndText = "";
    }
    this.sizeEndText = sizeEndText;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the start text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output before the size value.
   *
   * @return the current start of summary text
   */
  protected String getSummaryObjectStartText() {
    return summaryObjectStartText;
  }

  /**
   * Sets the start text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output before the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param summaryObjectStartText the new start of summary text
   */
  protected void setSummaryObjectStartText(String summaryObjectStartText) {
    if (summaryObjectStartText == null) {
      summaryObjectStartText = "";
    }
    this.summaryObjectStartText = summaryObjectStartText;
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the end text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output after the size value.
   *
   * @return the current end of summary text
   */
  protected String getSummaryObjectEndText() {
    return summaryObjectEndText;
  }

  /**
   * Sets the end text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output after the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param summaryObjectEndText the new end of summary text
   */
  protected void setSummaryObjectEndText(String summaryObjectEndText) {
    if (summaryObjectEndText == null) {
      summaryObjectEndText = "";
    }
    this.summaryObjectEndText = summaryObjectEndText;
  }

  // ----------------------------------------------------------------------------

  /**
   * Default <code>ToStringStyle</code>.
   *
   * <p>This is an inner class rather than using <code>StandardToStringStyle</code> to ensure its
   * immutability.
   */
  private static final class DefaultToStringStyle extends ToStringStyle {

    /**
     * Required for serialization support.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * <p>Use the static constant rather than instantiating.
     */
    DefaultToStringStyle() {
      super();
    }

    /**
     * Ensure <code>Singleton</code> after serialization.
     *
     * @return the singleton
     */
    private Object readResolve() {
      return ToStringStyle.DEFAULT_STYLE;
    }
  }

  // ----------------------------------------------------------------------------

  /**
   * <code>ToStringStyle</code> that does not print out the field names.
   *
   * <p>This is an inner class rather than using <code>StandardToStringStyle</code> to ensure its
   * immutability.
   */
  private static final class NoFieldNameToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * <p>Use the static constant rather than instantiating.
     */
    NoFieldNameToStringStyle() {
      super();
      this.setUseFieldNames(false);
    }

    /**
     * Ensure <code>Singleton</code> after serialization.
     *
     * @return the singleton
     */
    private Object readResolve() {
      return ToStringStyle.NO_FIELD_NAMES_STYLE;
    }
  }

  // ----------------------------------------------------------------------------

  /**
   * <code>ToStringStyle</code> that prints out the short class name and no identity hashcode.
   *
   * <p>This is an inner class rather than using <code>StandardToStringStyle</code> to ensure its
   * immutability.
   */
  private static final class ShortPrefixToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * <p>Use the static constant rather than instantiating.
     */
    ShortPrefixToStringStyle() {
      super();
      this.setUseShortClassName(true);
      this.setUseIdentityHashCode(false);
    }

    /**
     * <p>Ensure <code>Singleton</ode> after serialization.</p>
     * @return the singleton
     */
    private Object readResolve() {
      return ToStringStyle.SHORT_PREFIX_STYLE;
    }
  }

  /**
   * <code>ToStringStyle</code> that does not print out the classname, identity hashcode, content
   * start or field name.
   *
   * <p>This is an inner class rather than using <code>StandardToStringStyle</code> to ensure its
   * immutability.
   */
  private static final class SimpleToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * <p>Use the static constant rather than instantiating.
     */
    SimpleToStringStyle() {
      super();
      this.setUseClassName(false);
      this.setUseIdentityHashCode(false);
      this.setUseFieldNames(false);
      this.setContentStart("");
      this.setContentEnd("");
    }

    /**
     * <p>Ensure <code>Singleton</ode> after serialization.</p>
     * @return the singleton
     */
    private Object readResolve() {
      return ToStringStyle.SIMPLE_STYLE;
    }
  }

  // ----------------------------------------------------------------------------

  /**
   * <code>ToStringStyle</code> that outputs on multiple lines.
   *
   * <p>This is an inner class rather than using <code>StandardToStringStyle</code> to ensure its
   * immutability.
   */
  private static final class MultiLineToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * <p>Use the static constant rather than instantiating.
     */
    MultiLineToStringStyle() {
      super();
      this.setContentStart("[");
      this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
      this.setFieldSeparatorAtStart(true);
      this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
    }

    /**
     * Ensure <code>Singleton</code> after serialization.
     *
     * @return the singleton
     */
    private Object readResolve() {
      return ToStringStyle.MULTI_LINE_STYLE;
    }
  }
}
