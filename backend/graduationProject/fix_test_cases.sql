-- 修复测试用例的输入格式
-- 确保输入是两行：第一行是数组，第二行是目标值

UPDATE test_case 
SET stdin = '2 7 11 15
9' 
WHERE id = 4;

UPDATE test_case 
SET stdin = '3 2 4
6' 
WHERE id = 5;

UPDATE test_case 
SET stdin = '3 3
6' 
WHERE id = 6;

UPDATE test_case 
SET stdin = '1 2 3 4 5
9' 
WHERE id = 7;

UPDATE test_case 
SET stdin = '-1 -2 -3 -4 -5
-8' 
WHERE id = 8;

UPDATE test_case 
SET stdin = '0 4 3 0
0' 
WHERE id = 9;

UPDATE test_case 
SET stdin = '2 5 5 11
10' 
WHERE id = 10;

-- 查看修复后的结果
SELECT * FROM test_case WHERE question_id = 1;
