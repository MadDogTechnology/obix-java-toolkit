/*
 * This code licensed to public domain
 */
package obix;

import java.text.*;
import java.util.Date;
import java.util.*;

/**
 * Abstime models an absolute point in time modeled as millis since
 * the epoch 1 Jan 1970.  It also provides access to time of day
 * components relative to a specified time zone: year, month, day,
 * hour, min, and seconds.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Abstime
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Abstime with specified value using default time zone.
   */
  public Abstime(String name, long millis) 
  { 
    super(name); 
    set(millis, defaultTimeZone);
  }                 

  /**
   * Construct named Abstime with value of 0 using default time zone.
   */
  public Abstime(String name) 
  { 
    super(name); 
    set(0, defaultTimeZone);
  }                 
  
  /**
   * Construct unnamed Abstime with specified value and time zone.
   */
  public Abstime(long millis, TimeZone timeZone) 
  {
    set(millis, timeZone);
  }

  /**
   * Construct unnamed Abstime with same millis, but different time zone.
   */
  public Abstime(Abstime t, TimeZone timeZone) 
  {
    set(t.millis, timeZone);
  }

  /**
   * Construct unnamed Abstime with specified value using default time zone.
   */
  public Abstime(long millis) 
  {           
    this(millis, defaultTimeZone);
  }

  /**
   * Construct unnamed Abstime with components relative to specified time zone.
   */
  public Abstime(int year, int month, int day, int hour, int min, int sec, int millis, TimeZone timeZone) 
  {
    set(toMillis(year, month, day, hour, min, sec, millis, timeZone), timeZone);
  }

  /**
   * Construct unnamed Abstime with components relative to default time zone.
   */
  public Abstime(int year, int month, int day, int hour, int min, int sec, int millis) 
  {   
    this(year, month, day, hour, min, sec, millis, defaultTimeZone);
  }

  /**
   * Construct unnamed Abstime with components relative to default time zone.
   */
  public Abstime(int year, int month, int day, int hour, int min, int sec) 
  {   
    this(year, month, day, hour, min, sec, 0, defaultTimeZone);
  }

  /**
   * Construct unnamed Abstime with components relative to default time zone.
   */
  public Abstime(int year, int month, int day, int hour, int min) 
  {   
    this(year, month, day, hour, min, 0, 0, defaultTimeZone);
  }

  /**
   * Construct unnamed Abstime with components relative to default time zone.
   */
  public Abstime(int year, int month, int day) 
  {   
    this(year, month, day, 0, 0, 0, 0, defaultTimeZone);
  }
  
  /**
   * Construct unnamed Abstime with value of 0 using default time zone.
   */
  public Abstime() 
  { 
    set(0, defaultTimeZone);
  }
  
////////////////////////////////////////////////////////////////
// Get Functions
////////////////////////////////////////////////////////////////

  /**
   * @return millis since the Java epoch relative to UTC.  This
   *    result is independent of this AbsTime's time zone.
   */
  public long getMillis()
  {
    return millis;
  }

  /**
   * @return millis since the 1 Jan 2000 UTC epoch.  This
   *    result is independent of this AbsTime's time zone.
   */
  public long getMillis2000()
  {                             
    // subtract Java 1970 epoch to get oBIX 2000 epoch
    return millis - JAVA_2000;
  }

  /**
   * The year as a four digit integer (ie 2001).
   */
  public final int getYear()
  {
    if (bits0 == 0) millisToFields();
    return (bits0 >> 16) & 0xFFFF;
  }

  /**
   * The month: 1-12
   */
  public final int getMonth()
  {
    if (bits0 == 0) millisToFields();
    return (bits1 >> 25) & 0x0F;
  }

  /**
   * The day: 1-31.
   */
  public final int getDay()
  {
    if (bits0 == 0) millisToFields();
    return (bits1 >> 20) & 0x1F;
  }

  /**
   * The hour: 0-23.
   */
  public final int getHour()
  {
    if (bits0 == 0) millisToFields();
    return (bits1 >> 15) & 0x1F;
  }

  /**
   * The minute: 0-59.
   */
  public final int getMinute()
  {
    if (bits0 == 0) millisToFields();
    return (bits1 >> 9) & 0x3F;
  }

  /**
   * The seconds: 0-59.
   */
  public final int getSecond()
  {
    if (bits0 == 0) millisToFields();
    return (bits1 >> 3) & 0x3F;
  }

  /**
   * The milliseconds: 0-999.
   */
  public final int getMillisecond()
  {
    if (bits0 == 0) millisToFields();
    return bits0 & 0xFFFF;
  }

  /**
   * The weekday: 0-6
   */
  public final int getWeekday()
  {
    if (bits0 == 0) millisToFields();
    return bits1 & 0x07;
  }           
  
  /**
   * Get the number of milliseconds into the day
   * for this Abstime.  An example is that 1:00 AM
   * would return 3600000.
   */
  public final long getTimeOfDayMillis()
  {
    return getHour()   * 60*60*1000L +
           getMinute() * 60*1000L +
           getSecond() * 1000L +
           getMillisecond();
  }              
  
  /**
   * Return a nice human formatted String.
   */
  public String format()
  {                            
    if (isNull() || millis == 0) return "null";           
    return format.format(new Date(millis));
  }  
  private DateFormat format = new SimpleDateFormat("HH:mm:ss dd-MMM-yy z");

////////////////////////////////////////////////////////////////
// TimeZone
////////////////////////////////////////////////////////////////

  /**
   * Get timezone used to compute relative fields such 
   * as year, month, day, hour, and minutes.  The time zone
   * never has any bearing on getMillis().
   */
  public final TimeZone getTimeZone()
  {                                       
    return timeZone;
  }

  /**
   * Return the offset in millis from GMT taking daylight
   * savings time into account if appropriate.
   */
  public int getTimeZoneOffset()
  {                 
    if (!inDaylightTime())
      return timeZone.getRawOffset();
    GregorianCalendar cal = new GregorianCalendar(timeZone);
    cal.setTime(new Date(millis));
    return cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
  }

  /**
   * Does this time fall in daylight savings time
   * based on the current TimeZone.
   */
  public boolean inDaylightTime()
  {
    if (bits0 == 0) millisToFields();
    return ((bits1 >> 29) & 0x01) != 0;
  }

  /**
   * Convert this instance to an equivalent instance in the
   * current VM's local time zone.
   */
  public Abstime toLocalTime()
  {
    if (timeZone.equals(defaultTimeZone))
      return this;
    else
      return new Abstime(this, defaultTimeZone);
  }

  /**
   * Convert this instance to an equivalent instance in UTC.
   */
  public Abstime toUtcTime()
  {
    if (timeZone.equals(utcTimeZone))
      return this;
    else
      return new Abstime(this, utcTimeZone);
  }

////////////////////////////////////////////////////////////////
// Comparsion
////////////////////////////////////////////////////////////////

  /**
   * Compare to another Abstime.
   * @return a negative integer, zero, or a
   *    positive integer as this object is less
   *    than, equal to, or greater than the
   *    specified object.
   */
  public int compareTo(Object that)
  {
    Abstime t = (Abstime)that;
    if (this.millis < t.millis) return -1;
    else if (this.millis == t.millis) return 0;
    else return 1;
  }

  /**
   * Return true if the specified time is before this time.
   */
  public boolean isBefore(Abstime x)
  {
    return compareTo(x) < 0;
  }

  /**
   * Return true if the specified time is after this time.
   */
  public boolean isAfter(Abstime x)
  {
    return compareTo(x) > 0;
  }

  /**
   * Abstime hash code is based on the
   * the absolute time in millis.
   */
  public int hashCode()
  {
   return (int)(millis ^ (millis >> 32));
  }

  /**
   * Return if millis are equal regardless of timezone.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Abstime)
      return ((Abstime)that).millis == millis;
    return false;
  }

  /**
   * Is the date of the specified instance equal to the date of this instance?
   */
  public boolean dateEquals(Abstime that)
  {
    return (that.getYear() == getYear()) &&
           (that.getMonth() == getMonth()) &&
           (that.getDay() == getDay());
  }

  /**
   * Is the time of the specified instance equal to the date of this instance?
   */
  public boolean timeEquals(Abstime that)
  {
    return that.getTimeOfDayMillis() == getTimeOfDayMillis();
  }

////////////////////////////////////////////////////////////////
// Algebra
////////////////////////////////////////////////////////////////

  /**
   * Add a relative time to this time and return
   * the new instant in time.
   */
  public Abstime add(Reltime relTime)
  {
    return new Abstime(millis+relTime.getMillis(), timeZone);
  }

  /**
   * Subtract a relative time from this time and
   * return the new instant in time.
   */
  public Abstime subtract(Reltime relTime)
  {
    return new Abstime(millis-relTime.getMillis(), timeZone);
  }

  /**
   * Compute the time difference between this time and the specified time.  If
   * t2 is after this time, the result will be positive.  If t2 is before
   * this time, the result will be negative.
   *
   * @param t2 The time to compare against.
   */
  public Reltime delta(Abstime t2)
  {
    return new Reltime(t2.millis - millis);
  }

  /**
   * Create a new instance on the same date as this instance
   * but with a different time.
   */
  public Abstime timeOfDay(int hour, int min, int sec, int millis)
  {
    return new Abstime(getYear(), getMonth(), getDay(), hour, min, sec, millis, timeZone);
  }

  /**
   * The same time on the next day.
   */
  public Abstime nextDay()
  {
    int year  = getYear();
    int month = getMonth();
    int day   = getDay();

    if (day == getDaysInMonth(year,month))
    {
      day = 1;
      if (month == 12)
      {
        month = 1;
        year++;
      }
      else
      {
        month++;
      }
    }
    else
    {
      day++;
    }
    return new Abstime(year, month, day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }

  /**
   * The same time on the previous day.
   */
  public Abstime prevDay()
  {
    int year  = getYear();
    int month = getMonth();
    int day   = getDay();
    
    if (day == 1)
    {
      if (month == 1)
      {
        month = 12;
        year--;
      }
      else
      {
        month--;
      }
      day = getDaysInMonth(year,month);
    }
    else
    {
      day--;
    }
    return new Abstime(year, month, day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }

  /**
   * The same day and time in the next month.  If
   * this day is greater than the last day in the
   * next month, then cap the day to the next month's
   * last day.  If this time's day is the last day
   * in this month, then we automatically set the
   * month to the next month's last day.
   */
  public Abstime nextMonth()
  {
    int year  = getYear();
    int month = getMonth();
    int day   = getDay();
    
    if (month == 12)
    {
      // no need to worry about day capping 
      // because both Dec and Jan have 31 days
      month = 1;  
      year++;
    }
    else
    {
      if (day == getDaysInMonth(year, month))
      {
        month++;
        day = getDaysInMonth(year, month);
      }
      else
      {
        month++;
        if (day > getDaysInMonth(year, month))
          day = getDaysInMonth(year, month);
      }
    }                   
    
    return new Abstime(year, month, day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }

  /**
   * The same time and day in previous month. If
   * this day is greater than the last day in the
   * prev month, then cap the day to the prev month's
   * last day.  If this time's day is the last day
   * in this month, then we automatically set the
   * month to the prev month's last day.
   */
  public Abstime prevMonth()
  {
    int year  = getYear();
    int month = getMonth();
    int day   = getDay();
    
    if (month == 1)
    {
      // no need to worry about day capping 
      // because both Dec and Jan have 31 days
      month = 12;  
      year--;
    }
    else
    {
      if (day == getDaysInMonth(year, month))
      {
        month--;
        day = getDaysInMonth(year, month);
      }
      else
      {
        month--;
        if (day > getDaysInMonth(year, month))
          day = getDaysInMonth(year, month);
      }
    }  
    
    return new Abstime(year, month, day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }

  /**
   * Get the same time and day in next year.  If today
   * is a leap day, then return next year Feb 28.
   */
  public Abstime nextYear()
  {
    int day = getDay();
    if (isLeapDay()) day = 28;
    return new Abstime(getYear()+1, getMonth(), day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }

  /**
   * Get the same time and day in prev year.  If today
   * is a leap day, then return prev year Feb 28.
   */
  public Abstime prevYear()
  {
    int day = getDay();
    if (isLeapDay()) day = 28;
    return new Abstime(getYear()-1, getMonth(), day, getHour(), getMinute(), getSecond(), getMillisecond(), timeZone);
  }
  
  /**
   * Get the next day of the specified weekday. If
   * today is the specified weekday, then return one
   * week from now.
   */
  public Abstime nextWeekday(int weekday)
  {
    Abstime t = nextDay();
    while(t.getWeekday() != weekday)
      t = t.nextDay();
    return t;
  }

  /**
   * Get the prev day of the specified weekday. If
   * today is the specified weekday, then return one
   * week before now.
   */
  public Abstime prevWeekday(int weekday)
  {
    Abstime t = prevDay();
    while(t.getWeekday() != weekday)
      t = t.prevDay();
    return t;
  }

////////////////////////////////////////////////////////////////
// Leap Years
////////////////////////////////////////////////////////////////

  /**
   * Return if today is Feb 29.
   */
  public boolean isLeapDay()
  {
    return (getMonth() == 2) && (getDay() == 29);
  }

  /**
   * Return if the specified year (as a four digit
   * number) is a leap year.
   */
  public static boolean isLeapYear(int year)
  {
    if (year >= 1582)
    {
      // Gregorian
      return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
    }
    else
    {
      // Julian
      return (year % 4 == 0);
    }
  }

  /**
   * Given a year and month (1-12), return the number of days
   * in that month taking into consideration leap years.
   */
  public static int getDaysInMonth(int year, int month)
  {              
    checkMonth(month);             
    if (month == 2)
      return isLeapYear(year) ? 29 : 28;
    else
      return daysInMonth[month-1];
  }

  /**
   * Given a year, return the number of days in that
   * year taking into consideration leap years.
   */
  public static int getDaysInYear(int year)
  {
    return isLeapYear(year) ? 366 : 365;
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Get value in millis. 
   */
  public long get()
  {
    return millis;
  }

  /**
   * Set value in millis. 
   */
  public void set(long millis, TimeZone timeZone)
  {
    this.millis   = millis;                          
    this.timeZone = timeZone;
    this.bits0    = bits1 = 0;
    if (timeZone != null)
      tz = timeZone.getID();
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "abstime".
   */
  public String getElement()
  {
    return "abstime";
  }

  /**
   * Return BinObix.ABSTIME.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.ABSTIME;
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {
    StringBuffer s = new StringBuffer(32);

    s.append( getYear() ).append('-');

    int month = getMonth();
    if (month < 10) s.append('0');
    s.append( month ).append( '-' );

    int day = getDay();
    if (day < 10) s.append('0');
    s.append( day ).append( 'T' );

    int hour = getHour();
    if (hour < 10) s.append('0');
    s.append( hour ).append( ':' );

    int min = getMinute();
    if (min < 10) s.append('0');
    s.append( min ).append( ':' );

    int sec = getSecond();
    if (sec < 10) s.append('0');
    s.append( sec ).append( '.' );

    int millis = getMillisecond();
    if (millis < 10) s.append('0');
    if (millis < 100) s.append('0');
    s.append( millis );   
    
    int offset = getTimeZoneOffset(); 
    if (offset == 0)
    {
      s.append('Z');
    }     
    else
    {
      int hrOff = Math.abs(offset / (1000*60*60));
      int minOff = Math.abs((offset % (1000*60*60)) / (1000*60));
      
      if (offset < 0) s.append('-');
      else s.append('+');
      
      if (hrOff < 10) s.append('0');
      s.append(hrOff);
      
      s.append(':');
      if (minOff < 10) s.append('0');
      s.append(minOff);
    }

    return s.toString();
  }                      
  
  /**
   * Parse value string into a Abstime.
   */
  public static Abstime parse(String val)
    throws Exception
  {
    Abstime a = new Abstime();
    a.decodeVal(val);
    return a;
  }

  /**
   * Decode the value from a string.
   */
  public void decodeVal(String val)
    throws Exception
  {                   
    char[] c = val.toCharArray();
    try
    {
      int i = 0;

      int year = (int)(c[i++] - '0') * 1000 +
                 (int)(c[i++] - '0') * 100 +
                 (int)(c[i++] - '0') * 10 +
                 (int)(c[i++] - '0') * 1;

      if (c[i++] != '-') throw new Exception();

      int mon = (int)(c[i++] - '0') * 10 +
                (int)(c[i++] - '0') * 1;

      if (c[i++] != '-') throw new Exception();

      int day = (int)(c[i++] - '0') * 10 +
                (int)(c[i++] - '0') * 1;

      if (c[i++] != 'T') throw new Exception();

      int hour = (int)(c[i++] - '0') * 10 +
                 (int)(c[i++] - '0') * 1;

      if (c[i++] != ':') throw new Exception();

      int min = (int)(c[i++] - '0') * 10 +
                (int)(c[i++] - '0') * 1;

      if (c[i++] != ':') throw new Exception();

      int sec = (int)(c[i++] - '0') * 10 +
                (int)(c[i++] - '0') * 1;
      
      int ms = 0;
      if (c[i] == '.')                  
      {         
        i++;
        ms = (c[i++] - '0') * 100;
        if ('0' <= c[i] && c[i] <= '9') ms += (c[i++] - '0') * 10;
        if ('0' <= c[i] && c[i] <= '9') ms += (c[i++] - '0') * 1;

        // skip any additional fractional digits
        while(i < c.length && '0' <= c[i]  && c[i] <= '9') i++;
      }

      // timezone offset sign
      int tzOff = 0;
      char sign = c[i++];        
      if (sign != 'Z')
      {
        if (sign != '+' && sign != '-')
          throw new Exception();
  
        // timezone hours
        int hrOff = (int)(c[i++] - '0');
        if (i < c.length && c[i] != ':')
          hrOff = hrOff*10 + (int)(c[i++] - '0');
  
        // timezone minutes
        int minOff = 0;
        if (i < c.length)
        {
          if (c[i++] != ':') throw new Exception();
          minOff = 10*(int)(c[i++] - '0') + (int)(c[i++] - '0');
        }
  
        tzOff = hrOff*(60*60*1000) + minOff*(60*1000);          
        if (sign == '-') tzOff *= -1;
      }                           

      Calendar cal = new GregorianCalendar(year, mon-1, day, hour, min, sec);
      cal.set(Calendar.MILLISECOND, ms);
      cal.setTimeZone(new SimpleTimeZone(tzOff, "Offset")); 

      // save    
      set(cal.getTime().getTime(), timeZone);
    }
    catch(Exception e)
    {
      throw new Exception("Invalid abstime: " + val);
    }
  }

  /**
   * Encode the value as a Java code literal to pass to the constructor.
   */
  public String encodeJava()
  {
    return String.valueOf(millis) + "L";
  }    

////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////
  
  /**
   * Get the min facet or null if unspecified.
   */
  public Abstime getMin()
  {
    return min;
  }

  /**
   * Set the min facet.
   */
  public void setMin(Abstime min)
  {
    this.min = min;
  }

  /**
   * Get the max facet or null if unspecified.
   */
  public Abstime getMax()
  {
    return max;
  }

  /**
   * Set the max facet.
   */
  public void setMax(Abstime max)
  {
    this.max = max;
  }
  
  /**
   * Get the tz facet.
   * @return tz
   */
  public String getTz()
  {
    return tz;
  }
  
  /**
   * Set the tz facet.
   * @param tz
   */
  public void setTz(String tz)
  {
    // Do not make this an exception failure yet. 
//    this.tz = tz;
//    if (!TimeZone.getTimeZone(tz).equals(timeZone))
//      throw new IllegalStateException("tz facet must match timezone");
    if (tz == null) return;
    TimeZone newTz = TimeZone.getTimeZone(tz);
    if (newTz == null)
    {
      System.out.println("no timezone for tz facet:"+tz);
      return;
    }
    
    // if the timezone isn't what I have now, then we need to 
    // clear the fields and recompute them
    if (!newTz.equals(timeZone)) 
    {                 
      bits0 = bits1 = 0;          
    }       
    
    this.tz = tz;
    this.timeZone = newTz;
  }

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

  private static long toMillis(int year, int month, int day, int hour, int min, int sec, int millis, TimeZone timeZone)
  {                                      
    checkMonth(month);
    Calendar c = new GregorianCalendar(timeZone);
    c.set(year, month-1, day, hour, min, sec);
    c.set( Calendar.MILLISECOND, millis );
    return c.getTime().getTime();
  }

  private static int checkMonth(int month)
  {
    if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be 1 to 12");
    return month;
  }

////////////////////////////////////////////////////////////////
// Millis To Fields
////////////////////////////////////////////////////////////////

  /**
   * Map millis and timeZone to its component fields.
   *
   * Bits0:
   *  ------------------------------------------------
   *  Field    Num Bits  Range    Loc
   *  ------------------------------------------------
   *  Year       16      short    16-31
   *  Millis     16      short    0-15
   *
   * Bits1:
   *  ------------------------------------------------
   *  Field    Num Bits  Range    Loc
   *  ------------------------------------------------
   *  Daylight    1       0-1     29-29
   *  Month       4       1-12    25-28
   *  Day         5       1-31    20-24
   *  Hour        5       0-23    15-19
   *  Minutes     6       0-59    9-14
   *  Seconds     6       0-59    3-8
   *  Weekday     3       0-6     0-2
   * ------------------------------------------------
   */
  private void millisToFields()
  {
    // init a calendar with timeZone and millis
    Calendar calendar = new GregorianCalendar(timeZone);
    Date date = new Date(millis);
    calendar.setTime(date);

    // set year bits
    int x = calendar.get(Calendar.YEAR);
    bits0 |= ((x & 0xFFFF) << 16);

    // set millisecond bits
    x = calendar.get(Calendar.MILLISECOND);
    bits0 |= ((x & 0xFFFF) << 0);

    // set month bits
    x = calendar.get(Calendar.MONTH) + 1;
    bits1 |= ((x & 0x0F) << 25);

    // set day bits
    x = calendar.get(Calendar.DAY_OF_MONTH);
    bits1 |= ((x & 0x1F) << 20);

    // set hour bits
    x = calendar.get(Calendar.HOUR_OF_DAY);
    bits1 |= ((x & 0x1F) << 15);

    // set minute bits
    x = calendar.get(Calendar.MINUTE);
    bits1 |= ((x & 0x3F) << 9);

    // set seconds bits
    x = calendar.get(Calendar.SECOND);
    bits1 |= ((x & 0x3F) << 3);

    // set weekday
    x = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    bits1 |= ((x & 0x07) << 0);

    // set daylight bit    
    if (timeZone.inDaylightTime(date))
      bits1 |= (0x01 << 29);
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  /** Number of milliseconds from Java epoch of 1970 to 2000 epoch. */
  public static final long JAVA_2000 = 946684800000L;

  private static final int[] daysInMonth =
    { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  static final TimeZone defaultTimeZone = TimeZone.getDefault();
  static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
  
  private long millis;
  private int bits0, bits1;   
  private TimeZone timeZone;
  private Abstime min;
  private Abstime max;
  private String tz;
  
}
