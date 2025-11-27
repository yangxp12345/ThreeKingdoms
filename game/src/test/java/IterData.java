import org.yang.business.calc.DataCalc;

import java.util.*;

/**
 * 可变迭代器
 */
public class IterData<T> {


    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("112_f","116_e","114_a", "115_a", "114_c", "114_d"));
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String[] split1 = s1.split("_");
                String[] split2 = s2.split("_");
                Integer randomUnitEQ = new Random().nextInt(2) - 1;
                int randomUnit = split1[0].compareTo(split2[0]);



                if(!split1[0].equals(split2[0])) {
                    System.out.println("("+s1+":"+s2+") = "+randomUnit);
                    return randomUnit; // 按字符串长度排序
                }
                System.out.println("("+s1+":"+s2+") = "+randomUnitEQ);
                return randomUnitEQ;
            }
        });
        System.out.println(list); // 输出: [apple, orange, banana]

    }
}