using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PPD.AuthenticationCenter.Configuration.FastReflection
{
    public interface IFastReflectionFactory<TKey, TValue>
    {
        TValue Create(TKey key);
    }
}
