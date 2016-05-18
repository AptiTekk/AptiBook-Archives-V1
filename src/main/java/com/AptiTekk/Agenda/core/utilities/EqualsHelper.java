package com.AptiTekk.Agenda.core.utilities;

public class EqualsHelper {

    public static boolean areEquals(Object var1, Object var2)
    {
        return (var1 == var2) || (var1 != null && var2 != null && var1.equals(var2));
    }

    public static int calculateHashCode(Object... objects)
    {
        int hashCode = 0;
        for(Object object : objects)
        {
            hashCode += (object == null ? 0 : object.hashCode());
        }

        return hashCode * 31;
    }

}
