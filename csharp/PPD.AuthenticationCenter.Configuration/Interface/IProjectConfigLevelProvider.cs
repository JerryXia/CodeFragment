using System;
using System.Collections.Generic;

namespace PPD.AuthenticationCenter.Configuration
{
    public interface IProjectConfigLevelProvider
    {

        List<ProjectConfigLevelItem> GetConfigLevels();

    }
}
