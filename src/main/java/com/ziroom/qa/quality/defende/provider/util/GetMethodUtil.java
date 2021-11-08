package com.ziroom.qa.quality.defende.provider.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:Elaine
 * @Description:
 * @Date: Created in 上午11:11 2018/8/17
 * @Version: 1.0
 */
public class GetMethodUtil {

    private static final String METHOD_SUB_PATH = "impl";

    /**
     * 获取制定路径下所有子类或接口实现类
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> getAllAssignedClass(Class<?> cls, String subPath) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> c : getClasses(cls, subPath)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }


    /**
     * 取得当前类路径下制定目录下的所有类，如果是当前路径输入空字符串
     *
     * @param cls
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> getClasses(Class<?> cls, String subPath) {
        String currentPackage = cls.getPackage().getName();
        String pk = StringUtils.isEmpty(subPath)? currentPackage : currentPackage+"."+ subPath;
        String path = pk.replace('.', '/');
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List classList = new ArrayList<>();
        try {
            //获取所有匹配的文件
            Resource[] resources = resolver.getResources(path + "/*.*");
            for(Resource resource : resources) {
                String name = resource.getFilename();
                classList.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }


    /**
     * 获取方法名
     * @return 方法名列表
     * @throws ClassNotFoundException
     */
    public static Map<String, Method[]> getMethodNames(){
        Map<String, Method[]> resultMap = new HashMap<>();
        try {
            for (Class<?> c : getAllAssignedClass(IFunction.class, METHOD_SUB_PATH)) {
                Class forName = Class.forName(c.getName());
                Method[] methods = forName.getDeclaredMethods();
                resultMap.put(c.getName(),methods);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
