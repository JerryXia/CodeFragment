using System;
using System.Collections.Generic;
using System.Linq;

namespace PPD.AuthenticationCenter.Configuration
{
    public class DefaultProjectConfigLevelProvider : IProjectConfigLevelProvider
    {
        private List<ProjectConfigLevelItem> list;

        public DefaultProjectConfigLevelProvider()
        {
            list = new List<ProjectConfigLevelItem>();
        }

        public List<ProjectConfigLevelItem> GetConfigLevels()
        {
            list.Add(new ProjectConfigLevelItem(1, "Debug"));
            list.Add(new ProjectConfigLevelItem(2, "ExtraDebug"));
            list.Add(new ProjectConfigLevelItem(3, "Test"));
            list.Add(new ProjectConfigLevelItem(4, "ExtraTest"));
            list.Add(new ProjectConfigLevelItem(5, "PrePublish"));
            list.Add(new ProjectConfigLevelItem(6, "ExtraPublish"));
            list.Add(new ProjectConfigLevelItem(7, "Release"));
            return list;
        }
    }
}
