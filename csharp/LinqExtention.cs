public static class LinqExtention
{
    public static void ForEach<T>(this IEnumerable<T> source, Action<T> func)
    {
        foreach(var item in source)
        {
            func(item);
        }
    }
}