            var min = new DateTime(1970, 1, 1);
            var now = DateTime.Now;
            var minLocal = TimeZone.CurrentTimeZone.ToLocalTime(new System.DateTime(1970, 1, 1));

            Console.WriteLine((now.ToUniversalTime() - min).TotalMilliseconds);
            Console.WriteLine((now - min).TotalMilliseconds);
            Console.WriteLine((now - minLocal).TotalMilliseconds);

            long xxxxx = (now.ToUniversalTime().Ticks - min.Ticks) / 10000; //注意这里有时区问题，用now就要减掉8个小时
            Console.WriteLine(xxxxx);
