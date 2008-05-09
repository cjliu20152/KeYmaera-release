/* This file has been generated by Stubmaker (de.uka.ilkd.stubmaker)
 * Date: Fri Mar 28 13:47:08 CET 2008
 */
package java.lang;

public final class String extends java.lang.Object implements java.io.Serializable, java.lang.Comparable
{
// public final static java.util.Comparator CASE_INSENSITIVE_ORDER;

   public String();
   public String(java.lang.String arg0);
   public String(char[] arg0);
   public String(char[] arg0, int arg1, int arg2);
   public String(int[] arg0, int arg1, int arg2);
   public String(byte[] arg0, int arg1, int arg2, int arg3);
   public String(byte[] arg0, int arg1);
// public String(byte[] arg0, int arg1, int arg2, java.lang.String arg3) throws java.io.UnsupportedEncodingException;
// public String(byte[] arg0, int arg1, int arg2, java.nio.charset.Charset arg3);
// public String(byte[] arg0, java.lang.String arg1) throws java.io.UnsupportedEncodingException;
// public String(byte[] arg0, java.nio.charset.Charset arg1);
   public String(byte[] arg0, int arg1, int arg2);
   public String(byte[] arg0);
// public String(java.lang.StringBuffer arg0);
// public String(java.lang.StringBuilder arg0);
   public int length();
   public boolean isEmpty();
   public char charAt(int arg0);
   public int codePointAt(int arg0);
   public int codePointBefore(int arg0);
   public int codePointCount(int arg0, int arg1);
   public int offsetByCodePoints(int arg0, int arg1);
   public void getChars(int arg0, int arg1, char[] arg2, int arg3);
   public void getBytes(int arg0, int arg1, byte[] arg2, int arg3);
// public byte[] getBytes(java.lang.String arg0) throws java.io.UnsupportedEncodingException;
// public byte[] getBytes(java.nio.charset.Charset arg0);
   public byte[] getBytes();
   public boolean equals(java.lang.Object arg0);
// public boolean contentEquals(java.lang.StringBuffer arg0);
// public boolean contentEquals(java.lang.CharSequence arg0);
   public boolean equalsIgnoreCase(java.lang.String arg0);
   public int compareTo(java.lang.String arg0);
   public int compareToIgnoreCase(java.lang.String arg0);
   public boolean regionMatches(int arg0, java.lang.String arg1, int arg2, int arg3);
   public boolean regionMatches(boolean arg0, int arg1, java.lang.String arg2, int arg3, int arg4);
   public boolean startsWith(java.lang.String arg0, int arg1);
   public boolean startsWith(java.lang.String arg0);
   public boolean endsWith(java.lang.String arg0);
   public int hashCode();
   public int indexOf(int arg0);
   public int indexOf(int arg0, int arg1);
   public int lastIndexOf(int arg0);
   public int lastIndexOf(int arg0, int arg1);
   public int indexOf(java.lang.String arg0);
   public int indexOf(java.lang.String arg0, int arg1);
   public int lastIndexOf(java.lang.String arg0);
   public int lastIndexOf(java.lang.String arg0, int arg1);
   public java.lang.String substring(int arg0);
   public java.lang.String substring(int arg0, int arg1);
// public java.lang.CharSequence subSequence(int arg0, int arg1);
   public java.lang.String concat(java.lang.String arg0);
   public java.lang.String replace(char arg0, char arg1);
   public boolean matches(java.lang.String arg0);
// public boolean contains(java.lang.CharSequence arg0);
   public java.lang.String replaceFirst(java.lang.String arg0, java.lang.String arg1);
   public java.lang.String replaceAll(java.lang.String arg0, java.lang.String arg1);
// public java.lang.String replace(java.lang.CharSequence arg0, java.lang.CharSequence arg1);
   public java.lang.String[] split(java.lang.String arg0, int arg1);
   public java.lang.String[] split(java.lang.String arg0);
// public java.lang.String toLowerCase(java.util.Locale arg0);
   public java.lang.String toLowerCase();
// public java.lang.String toUpperCase(java.util.Locale arg0);
   public java.lang.String toUpperCase();
   public java.lang.String trim();
   public java.lang.String toString();
   public char[] toCharArray();
   public static java.lang.String format(java.lang.String arg0, java.lang.Object[] arg1);
// public static java.lang.String format(java.util.Locale arg0, java.lang.String arg1, java.lang.Object[] arg2);
   public static java.lang.String valueOf(java.lang.Object arg0);
   public static java.lang.String valueOf(char[] arg0);
   public static java.lang.String valueOf(char[] arg0, int arg1, int arg2);
   public static java.lang.String copyValueOf(char[] arg0, int arg1, int arg2);
   public static java.lang.String copyValueOf(char[] arg0);
   public static java.lang.String valueOf(boolean arg0);
   public static java.lang.String valueOf(char arg0);
   public static java.lang.String valueOf(int arg0);
   public static java.lang.String valueOf(long arg0);
   public static java.lang.String valueOf(float arg0);
   public static java.lang.String valueOf(double arg0);
   public java.lang.String intern();
   public int compareTo(java.lang.Object arg0);
}
