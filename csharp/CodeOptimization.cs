// 优化前
enum TemplateCode
{
    None = 0,
    Head = 1,
    Menu = 2,
    Foot = 3,
    Welcome = 4,
}

public string GetHtml(TemplateCode tc)
{
    switch (tc)
    {
        case TemplateCode.Head:
            return GetHead();
        case TemplateCode.Menu:
            return GetMenu();
        case TemplateCode.Foot:
            return GetFoot();
        case TemplateCode.Welcome:
            return GetWelcome();
        default:
            throw new ArgumentOutOfRangeException("tc");
    }
}

// 优化后
readonly static Func<string>[] GetTemplate = InitTemplateFunction();

private static Func<string>[] InitTemplateFunction()
{
    var arr = new Func<string>[5];
    arr[1] = GetHead;
    arr[2] = GetMenu;
    arr[3] = GetFoot;
    arr[4] = GetWelcome;
    return arr;
}

public string GetHtml(TemplateCode tc)
{
    var index = (int)tc;
    if (index >= 1 && index <= 4)
    {
        return GetTemplate[index]();
    }
    throw new ArgumentOutOfRangeException("tc");
}


// 不过有的时候,枚举不一定都是连续的数字,那么也可以使用Dictionary
readonly static Dictionary<TemplateCode, Func<string>> TemplateDict = InitTemplateFunction();

private static Dictionary<TemplateCode, Func<string>> InitTemplateFunction()
{
    var ditc = new Dictionary<TemplateCode, Func<string>>();
    ditc.Add(TemplateCode.Head, GetHead);
    ditc.Add(TemplateCode.Menu, GetMenu);
    ditc.Add(TemplateCode.Foot, GetFoot);
    ditc.Add(TemplateCode.Welcome, GetWelcome);
    return ditc;
}

public string GetHtml(TemplateCode tc)
{
    Func<string> func;
    if (TemplateDict.TryGetValue(tc,out func))
    {
        return func();
    }
    throw new ArgumentOutOfRangeException("tc");
}
