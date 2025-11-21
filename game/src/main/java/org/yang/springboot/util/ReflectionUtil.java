package org.yang.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.yang.business.instruction.ICommand;
import org.yang.business.weapon.IWeapon;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class ReflectionUtil {


    /**
     * 获取objClass类所在的目录下的impl目录下所有的类
     *
     * @param objClass 类
     * @return 类集合
     */
//    public static Set<Class<?>> getClassSet(Class<?> objClass) {
//        return getClassSet(objClass.getPackage().getName() + ".impl");
//    }

    public static <T> Set<Class<? extends T>> getClassSet(Class<? extends T> objClass) {
        Set<Class<?>> classSet = getClassSet(objClass.getPackage().getName() + ".impl");
        return classSet.stream().map(item -> (Class<? extends T>) item).collect(Collectors.toSet());
    }

    /**
     * 根据包名获取包下面所有的类名
     *
     * @param packageName 包名称 例如 com.business.util;
     * @return 类集合
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();// 第一个class类的集合
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;// 定义一个枚举的集合 并进行循环来处理这个目录下的things
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {// 循环迭代下去
                URL url = dirs.nextElement();// 获取下一个元素
                String protocol = url.getProtocol();// 得到协议的名称
                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    findClassInPackageByFile(packageName, filePath, true, classes);// 以文件的方式扫描整个包下的文件 并添加到集合中
                    continue;
                }
                if ("jar".equals(protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();// 获取jar
                    Enumeration<JarEntry> entries = jar.entries();
                    findClassInPackageByJar(packageName, entries, packageDirName, true, classes); // 从此jar包 得到一个枚举类
                }
            }
        } catch (Exception e) {
            log.error("根据包名获取包下面所有的类名异常", e);
        }
        return classes;
    }

    /**
     * @param packageName 包名
     * @return 所有的类集合
     */
    public static Set<Class<?>> getClassList(String packageName) {
        return getClassSet(packageName);
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    private static void findClassInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) throws ClassNotFoundException {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) return;//用户定义包名 " + packageName + " 下没有任何文件
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles(file -> {// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
        });
        if (dirFiles == null) return;
        for (File file : dirFiles) {// 循环所有文件
            if (file.isDirectory()) { // 如果是目录 则继续扫描
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
                continue;
            }
            String className = file.getName().substring(0, file.getName().length() - 6); // 如果是java类文件 去掉后面的.class 只留下类名
            classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
        }
    }

    /**
     * 以jar的形式来获取包下的所有Class
     */
    private static void findClassInPackageByJar(String packageName, Enumeration<JarEntry> entries, String packageDirName, final boolean recursive, Set<Class<?>> classes) throws ClassNotFoundException {
        while (entries.hasMoreElements()) {// 同样的进行循环迭代
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') name = name.substring(1);// 获取后面的字符串
            // 如果前半部分和定义的包名相同
            if (!name.startsWith(packageDirName)) continue;
            int idx = name.lastIndexOf('/');
            // 如果以"/"结尾 是一个包
            if (idx != -1) packageName = name.substring(0, idx).replace('/', '.');// 获取包名 把"/"替换成"."
            if ((idx == -1) && !recursive) continue;
            if ((!name.endsWith(".class")) || entry.isDirectory()) continue;
            // 如果可以迭代下去,并且是一个包
            // 如果是一个.class文件 而且不是目录
            String className = name.substring(packageName.length() + 1, name.length() - 6);// 去掉后面的".class" 获取真正的类名
            classes.add(Class.forName(packageName + '.' + className));//添加到classes
        }
    }
}