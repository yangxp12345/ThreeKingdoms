package org.yang.business.calc;



import java.util.List;
import java.util.Random;


/**
 * 随机迭代器
 */
public class IterModel {

    public static <T> T randomPop(List<T> list) {//引用可被删除
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.remove(index);
    }
}
