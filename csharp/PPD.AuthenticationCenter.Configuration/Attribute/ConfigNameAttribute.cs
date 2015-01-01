using System;
using System.Collections.Generic;

namespace PPD.AuthenticationCenter.Configuration
{
    [AttributeUsage(AttributeTargets.Class)]
    public class ConfigNameAttribute : Attribute
    {

        public ConfigNameAttribute(string configName)
            : this(configName, Constant.DEFAULT_CONFIGPATHDIR, Constant.DEFAULT_EXTNAME, new DefaultProjectConfigLevelProvider())
        {
        }

        public ConfigNameAttribute(string configName, IProjectConfigLevelProvider cfgLevels)
            : this(configName, Constant.DEFAULT_CONFIGPATHDIR, Constant.DEFAULT_EXTNAME, cfgLevels)
        {
        }

        public ConfigNameAttribute(string configName, string[] pathDirs)
            : this(configName, pathDirs, Constant.DEFAULT_EXTNAME, new DefaultProjectConfigLevelProvider())
        {
        }

        public ConfigNameAttribute(string configName, string[] pathDirs, IProjectConfigLevelProvider cfgLevels)
            : this(configName, pathDirs, Constant.DEFAULT_EXTNAME, cfgLevels)
        {
        }

        public ConfigNameAttribute(string configName, string extName)
            : this(configName, Constant.DEFAULT_CONFIGPATHDIR, extName, new DefaultProjectConfigLevelProvider())
        {
        }

        public ConfigNameAttribute(string configName, string extName, IProjectConfigLevelProvider cfgLevels)
            : this(configName, Constant.DEFAULT_CONFIGPATHDIR, extName, cfgLevels)
        {
        }

        public ConfigNameAttribute(string configName, string[] pathDirs, string extName, IProjectConfigLevelProvider cfgLevels)
        {
            this.SettingConfigName = configName;
            this.PathDirs = pathDirs;
            this.ExtName = extName;
            this.CfgLevelsProvider = cfgLevels;
        }

        public string[] PathDirs { get; private set; }

        public string SettingConfigName { get; private set; }

        public string ExtName { get; private set; }

        public IProjectConfigLevelProvider CfgLevelsProvider { get; private set; }


    }
}
