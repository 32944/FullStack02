-- 添加更多算法题目到数据库

-- 题目1: 两数之和（已存在，跳过或更新）
INSERT IGNORE INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time)
VALUES (
  1,
  '两数之和',
  '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。

输入格式:
第一行：数组元素（用空格分隔）
第二行：目标值 target

输出格式:
两个整数，用空格分隔',
  'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 读取输入
        String[] numsStr = scanner.nextLine().split(" ");
        int[] nums = new int[numsStr.length];
        for (int i = 0; i < numsStr.length; i++) {
            nums[i] = Integer.parseInt(numsStr[i]);
        }
        int target = scanner.nextInt();
        
        // 在这里写你的代码
    }
}',
  1,
  NOW(),
  NOW()
);

-- 题目2: 斐波那契数列
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time)
VALUES (
  1,
  '斐波那契数列',
  '写一个函数，输入 n ，求斐波那契（Fibonacci）数列的第 n 项（即 F(N)）。

斐波那契数列的定义如下：
F(0) = 0
F(1) = 1
F(N) = F(N - 1) + F(N - 2)

输入格式:
一个整数 n

输出格式:
一个整数 F(n)',
  'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        
        // 在这里写你的代码
    }
}',
  1,
  NOW(),
  NOW()
);

-- 题目3: 反转字符串
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time)
VALUES (
  1,
  '反转字符串',
  '编写一个函数，其作用是将输入的字符串反转过来。

输入格式:
一个字符串 s

输出格式:
反转后的字符串',
  'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        
        // 在这里写你的代码
    }
}',
  1,
  NOW(),
  NOW()
);

-- 题目4: 回文数
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time)
VALUES (
  1,
  '回文数',
  '给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。

输入格式:
一个整数 x

输出格式:
true 或 false',
  'import java.util.*;

public class Main {
    public static