package it.polimi.telcodb2.EJB.utils;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ParseUtils {
    // Null safe parsing of string to integer
    public static Integer toInteger(String integerString, Integer nullValue) {
        return integerString == null || integerString.isEmpty()?
                nullValue :
                Integer.parseInt(integerString);
    }
    // Null safe parsing of string to float
    public static Float toFloat(String floatString, Integer nullValue) {
        return floatString == null || floatString.isEmpty() ?
                nullValue :
                Float.parseFloat(floatString);
    }
    // Null safe parsing of String array to List<String>
    public static List<String> toStringListSafe(String[] strings) {
        return strings == null ?
                new ArrayList<String>() :
                Arrays.asList(strings);
    }
    // Parse list of strings to list of integers
    public static List<Integer> toIntegerList(List<String> stringList, boolean skipNull) {
        return stringList.isEmpty() ?
                new ArrayList<Integer>() : // if stringList is empty return empty integer list
                // if skipNull is true filter out null objects, else consider everything
                (skipNull ? stringList.stream().filter(Objects::nonNull) : stringList.stream())
                        .map(elem -> ParseUtils.toInteger(elem, 0)) // parse elements
                        .collect(Collectors.toList());  // convert string to list
    }
    // Parse list of strings to list of floats
    public static List<Float> toFloatList(List<String> stringList, boolean skipNull) {
        return stringList.isEmpty() ?
                new ArrayList<Float>() : // if stringList is empty return empty float list
                // if skipNull is true filter out null objects, else consider everything
                (skipNull ? stringList.stream().filter(Objects::nonNull) : stringList.stream())
                        .map(elem -> ParseUtils.toFloat(elem, 0)) // parse elements
                        .collect(Collectors.toList());  // convert string to list
    }

    // Parse formatted string date into java.util.Date
    public static LocalDate toDate(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }
        return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY));
    }
}
