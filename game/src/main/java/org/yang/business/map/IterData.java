package org.yang.business.map;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 可变迭代器,用于遍历角色行动数据
 */
public class IterData<T> {
    private LinkedList<T> dataList;

    public IterData(List<T> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        this.dataList = new LinkedList<>(dataList);
    }

    public boolean hasNext() {
        return dataList != null && !dataList.isEmpty();
    }

    public T next() {
        if (!hasNext()) return null;
        return dataList.removeFirst();
    }


    public void remove(T unit) {
        dataList.remove(unit);
    }

    public static void main(String[] args) {
        IterData<String> iterData = new IterData<>(Arrays.asList("1","2", "3", "4", "5"));
        while (iterData.hasNext()) {
            String next = iterData.next();
            if (next.equals("3")) {
                iterData.remove("4");
            }
            System.out.println(next);
        }
    }
}