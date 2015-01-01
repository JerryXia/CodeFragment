using System;
using System.Collections.Generic;

namespace PPD.AuthenticationCenter.Configuration
{
    public sealed class ProjectConfigLevelItem : IComparer<ProjectConfigLevelItem>
    {

        public int Index { get; private set; }
        public string ConfigName { get; private set; }

        public ProjectConfigLevelItem(int index, string configName)
        {
            if (index < 1)
            {
                throw new ArgumentException("index is not invalid");
            }

            if (string.IsNullOrEmpty(configName) || string.IsNullOrEmpty(configName.Trim()))
            {
                throw new ArgumentNullException("configName can not be null");
            }

            this.Index = index;
            this.ConfigName = configName;
        }

        public int Compare(ProjectConfigLevelItem x, ProjectConfigLevelItem y)
        {
            return x.Index - y.Index;
        }

    }
}
