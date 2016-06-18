package com.aptitekk.agenda.core.utilities.notification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableInjection {

    public static final String OMITTED_PARAM = "***Omitted***";

    /**
     * Maps each name of object dependency given to said object
     *
     * @param dependencies
     * @return Map<name, dependency instance>
     */
    public static Map<String, Object> mapDependencies(Object[] dependencies) {
        Map<String, Object> newDep = new HashMap<String, Object>();
        for (Object obj : dependencies) {
            newDep.put(obj.getClass().getSimpleName().substring(0, 1).toUpperCase()
                    + obj.getClass().getSimpleName().substring(1), obj);
            newDep.put(obj.getClass().getSimpleName().substring(0, 1).toLowerCase()
                    + obj.getClass().getSimpleName().substring(1), obj);
            // System.out.println("Added " + obj.getClass().getSimpleName().substring(0, 1).toUpperCase()
            // + obj.getClass().getSimpleName().substring(1));
            // System.out.println("Added " + obj.getClass().getSimpleName().substring(0, 1).toLowerCase()
            // + obj.getClass().getSimpleName().substring(1));
        }
        return newDep;
    }

    /**
     * Extracts fields containing "{xyz}" or "{x.y.z}"<br>
     * <hr />
     * First validates parameters (spaces, checks for escapes)<br>
     * Second omits specified parameters<br>
     * Third reforms the call tree (Ex: "{x.y.z}" to "{x.getY.getZ}")<br>
     * Fourth invokes the call tree and replaces parameter with returned object
     *
     * @param input
     * @param dependencies
     * @return parsedInput
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static String fillParameters(String input, Map<String, Object> dependencies)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Matcher m = Pattern.compile("\\{([^\\}]+)\\}").matcher(input);
        while (m.find()) {
            String parameter = m.group(1);
            // Parameter validation
            if (parameter.contains(" ")) {
                continue;
            }
            if (parameter.contains(".")) {
                String[] split = parameter.split("\\.");
                String objName = split[0];
                split = reformCall(split);
                if (dependencies.containsKey(objName)) {
                    input = input.replace("{" + parameter + "}",
                            String.valueOf(invokeMethodsStackByName(split, dependencies.get(objName))));
                }

            } else {
                if (dependencies.get(parameter) != null)
                    input.replace("{" + parameter + "}", dependencies.get(parameter).toString());
            }
        }
        return input;
    }

    /**
     * Replaces "{x.y.z}" with "{x.getY.getZ}"<br>
     * Readies string for recursive invoking
     *
     * @param callQueue
     * @return reformatted call
     */
    private static String[] reformCall(String[] callQueue) {
        for (int i = 1; i < callQueue.length; i++) {
            callQueue[i] = "get" + callQueue[i].substring(0, 1).toUpperCase() + callQueue[i].substring(1);
            // System.out.println("call[" + i + "] " + call[i]);
        }
        return callQueue;
    }

    /**
     * Recursively invokes the calling tree using reflection and a given root Object
     *
     * @param callQueue
     * @param rootObject
     * @return method tree output
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static Object invokeMethodsStackByName(String[] callQueue, Object rootObject)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method method;
        Object obj = rootObject;
        for (int i = 1; i < callQueue.length; i++) {
            // System.out.println("Invoking call[" + i + "] " + call[i]);
            method = obj.getClass().getMethod(callQueue[i]);
            if (method.isAnnotationPresent(OmitInjection.class))
                return OMITTED_PARAM;
            obj = method.invoke(obj);
        }
        // System.out.println("Received: " + String.valueOf(obj));
        return obj;
    }

}
