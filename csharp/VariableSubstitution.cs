#region xor两个变量交换

int a = 2;
int b = 3;
Console.WriteLine("a={0}", a);
Console.WriteLine("b={0}", b);
a = a ^ b ^ (b = a);
Console.WriteLine("a={0}", a);
Console.WriteLine("b={0}", b);

#endregion
