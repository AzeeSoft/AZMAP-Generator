package com.azeesoft.mapdatagenerator.java.tools;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aziz on 6/26/2015.
 */
public class DateTool {
    public static String[] months={"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};

    public DateTool()
    {

    }

    public static String getCurrentDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static String getCurrentDOW()
    {
        return new SimpleDateFormat("EEE", Locale.US).format(new Date());
    }

    public static String getCurrentDay()
    {
        return new SimpleDateFormat("dd", Locale.US).format(new Date());
    }

    public static String getCurrentMonth()
    {
        return new SimpleDateFormat("MM", Locale.US).format(new Date());
    }

    public static String getCurrentYear()
    {
        return new SimpleDateFormat("yyyy", Locale.US).format(new Date());
    }

    public static String getDayOfWeek(String s)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Date date= null;
        try {
            date = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEE", Locale.US).format(date);
    }

    public static String reorderDate(String s)
    {
        int[] d=splitDate(s);

        return formattedMode(d[2])+"-"+formattedMode(d[1])+"-"+formattedMode(d[0]);
    }

    public static String getTimeFromMins(int mins)
    {
        if(mins==-1)
        {
            return "--:--";
        }
        int hour,min;
        String ap;
        hour=mins/60;
        min=mins%60;

        if(hour>=12)
        {
            ap=" pm";
            if(hour>12)
            {
                hour-=12;
            }
        }
        else
        {
            ap=" am";
            if(hour==0)
                hour=12;
        }

        return formattedMode(hour)+":"+formattedMode(min)+ap;

    }

    public static String getHoursFromMins(int mins)
    {
        int hour,min;
        hour=mins/60;
        min=mins%60;

        if(hour!=0)
            return hour+" hrs "+min+" mins";
        else
            return min+" mins";
    }

    public static int getCurrentTime()
    {
        return (getCurrentHour()*60)+getCurrentMin();
    }

    public static int getCurrentHour()
    {
        DateFormat df = new SimpleDateFormat("HH", Locale.US);
        String time= df.format(Calendar.getInstance(Locale.US).getTime());

//        QuickTools.print("Hour:"+time);

        return Integer.parseInt(time);
    }

    public static int getCurrentMin()
    {
        DateFormat df = new SimpleDateFormat("mm", Locale.US);
        String time= df.format(Calendar.getInstance(Locale.US).getTime());

//        QuickTools.print("Min:"+time);

        return Integer.parseInt(time);
    }

    public static String getMonthName(String d)
    {
        return getMonthName(splitDate(d)[1]);
    }

    public static String getMonthName(int month)
    {
        return months[month-1];
    }

    public static String formattedMode(int t)
    {
        if(t<10)
        {
            return "0"+t;
        }

        return Integer.toString(t);
    }

    public static int[] splitDate(String dateString)
    {
        int[] i=new int[3];
        String[] s=dateString.split("-",3);

        i[0]= Integer.parseInt(s[0]);
        i[1]= Integer.parseInt(s[1]);
        i[2]= Integer.parseInt(s[2]);

        return i;
    }

    public static String dateToString(int year, int month, int day)
    {
        return formattedMode(year)+"-"+formattedMode(month)+"-"+formattedMode(day);
    }

    public static String incrementDay(int day, int month, int year, int inc)
    {

        String s=dateToString(year,month,day);

        return incrementDay(s,inc);

    }

    public static String incrementDay(String d, int inc)
    {
        Calendar c= Calendar.getInstance(Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(d));
        } catch (ParseException e) {
            e.printStackTrace();
            return d;
        }

        c.add(Calendar.DATE,inc);
        d=sdf.format(c.getTime());

        return d;
    }

    public static boolean isLeapYear(int year)
    {
        return (year%4)==0;
    }

    public static boolean isMonth30(int month)
    {
        switch(month)
        {
            case 4:
            case 6:
            case 9:
            case 11:

                return true;
            default:
                return false;

        }
    }

    public static boolean isMonth31(int month)
    {
        if(month==2)
            return false;

        return !isMonth30(month);
    }

    public static boolean isLeapMonth(int month,int year)
    {
        return isLeapYear(year) && month==2;
    }


}
