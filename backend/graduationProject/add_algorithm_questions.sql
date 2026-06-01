-- 添加更多算法题目
-- 题目1: 两数之和（已存在，就不管了）
-- 题目2: 反转链表
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time) 
VALUES (1, '反转链表', '给定一个单链表的头节点 head，请你反转这个链表，并返回反转后的链表的头节点。

输入格式：
第一行：链表节点个数 n
第二行：n 个整数，表示链表节点的值

输出格式：
一行整数，表示反转后的链表节点值', 'import java.util.*;

public class Main {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int i = 0; i < n; i++) {
            cur.next = new ListNode(sc.nextInt());
            cur = cur.next;
        }
        
        // 在这里写你的代码
        
        // 输出结果
        cur = dummy.next;
        while (cur != null) {
            System.out.print(cur.val + " ");
            cur = cur.next;
        }
    }
}', 1, NOW(), NOW());

-- 题目2的测试用例
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (2, '5
1 2 3 4 5', '5 4 3 2 1', 1);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (2, '2
1 2', '2 1', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (2, '1
0', '0', 0);

-- 题目3: 斐波那契数列
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time) 
VALUES (1, '斐波那契数列', '斐波那契数列，又称黄金分割数列，指的是这样一个数列：1、1、2、3、5、8、13、21、……
在数学上，斐波那契数列以如下被以递归的方法定义：F(1)=1，F(2)=1, F(n)=F(n-1)+F(n-2)（n>=3，n∈N*）。

输入格式：
一个整数 n

输出格式：
一个整数，表示第 n 个斐波那契数', 'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        // 在这里写你的代码
    }
}', 1, NOW(), NOW());

-- 题目3的测试用例
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (3, '1', '1', 1);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (3, '10', '55', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (3, '5', '5', 0);

-- 题目4: 最大子数组和
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time) 
VALUES (1, '最大子数组和', '给定一个整数数组 nums，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

输入格式：
第一行：数组长度 n
第二行：n 个整数

输出格式：
一个整数，表示最大和', 'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = sc.nextInt();
        }
        
        // 在这里写你的代码
    }
}', 2, NOW(), NOW());

-- 题目4的测试用例
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (4, '9
-2 1 -3 4 -1 2 1 -5 4', '6', 1);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (4, '1
1', '1', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (4, '5
5 4 -1 7 8', '23', 0);

-- 题目5: 两数之和（另一个版本）
INSERT INTO code_question (user_id, title, content, code_template, difficulty, create_time, update_time) 
VALUES (1, '有效的括号', '给定一个只包括 (、)、{、}、[、] 的字符串 s，判断字符串是否有效。

有效字符串需满足：
1. 左括号必须用相同类型的右括号闭合。
2. 左括号必须以正确的顺序闭合。

输入格式：
一行字符串

输出格式：
true 或 false', 'import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        
        // 在这里写你的代码
    }
}', 2, NOW(), NOW());

-- 题目5的测试用例
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (5, '()', 'true', 1);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (5, '()[]{}', 'true', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (5, '(]', 'false', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (5, '([)]', 'false', 0);
INSERT INTO test_case (question_id, stdin, stdout, is_example) 
VALUES (5, '{[]}', 'true', 0);

-- 查看添加的题目
SELECT id, title, difficulty FROM code_question;
