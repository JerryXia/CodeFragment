using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using System.Web.Caching;
using System.Web.Script.Serialization;

namespace PPD.AuthenticationCenter.Configuration
{
    using PPD.AuthenticationCenter.Configuration.FastReflection;

    public class ConfigManager : ConfigManager<DictionaryElement>
    {

    }

    public class ConfigManager<T> where T : class, new()
    {
        private static List<ProjectConfigLevelItem> _configLevelList;
        private static int _level;


        public static void LoadProjectConfigLevel(IProjectConfigLevelProvider configLevelProvider)
        {
            if (configLevelProvider == null)
            {
                throw new ArgumentNullException("configLevelProvider can not be null");
            }

            var configLevelList = configLevelProvider.GetConfigLevels();
            if (configLevelList != null && configLevelList.Count > 0)
            {
                _configLevelList = configLevelList;
            }
            else
            {
                throw new ArgumentNullException("configLevelProvider.GetConfigLevels() result is null");
            }
        }

        public static void Init(int level)
        {
            if (_level != 0)
            {
                throw new Exception("you don't need to call the function: 'Init(int level)' again");
            }
            else
            {
                _level = level;

                if (_configLevelList != null && _configLevelList.Count > 0)
                {
                    _runOptions = LoadRunOptions();
                }
                else
                {
                    LoadProjectConfigLevel(new DefaultProjectConfigLevelProvider());
                    _runOptions = LoadRunOptions();
                }
            }
        }


        private static T _runOptions;
        public static T Current
        {
            get
            {
                return _runOptions;
            }
        }


        private static T LoadRunOptions()
        {
            T options = new T();
            string[] configFilePaths = GetConfigPath(options);

            for (int i = 0; i < configFilePaths.Length; i++)
            {
                bool isExists = false;
                T t1 = LoadOptionsFromFile(configFilePaths[i], out isExists);

                if (isExists)
                {
                    if (i == 0)
                    {
                        options = t1;
                    }
                    else if (i == _level)
                    {
                        PropertyInfo[] pis0 = options.GetType().GetProperties();
                        PropertyInfo[] pis1 = t1.GetType().GetProperties();
                        for (int j = 0; j < pis0.Length; j++)
                        {
                            if (pis0[j].CanWrite)
                            {
                                var newValue = GetPropertyValue(t1, pis1, pis0[j].Name);
                                if (newValue != null)
                                {
                                    pis0[j].FastSetValue(options, newValue);
                                }
                            }
                        }
                    }
                    else
                    {
                        //
                    }
                }
                else
                {
                    if (i == 0)
                    {
                        throw new FileNotFoundException(string.Format("{0} Not Found", configFilePaths[i]));
                    }
                }
            }

            return options;
        }
        private static object GetPropertyValue(object instance, PropertyInfo[] pis1, string pName)
        {
            for (int i = 0; i < pis1.Length; i++)
            {
                if (pis1[i].Name == pName)
                {
                    return pis1[i].FastGetValue(instance);
                }
            }
            return null;
        }

        private static T LoadOptionsFromFile(string configFilePath, out bool fileExists)
        {
            T options = new T();

            fileExists = File.Exists(configFilePath);
            if (fileExists)
            {
                string content = ReadFromFile(configFilePath);
                var jss = new JavaScriptSerializer();
                options = jss.Deserialize<T>(content);
            }

            var fileMinitor = new FileMonitor(new List<string>
            {
                configFilePath
            }, DefaultCacheRemovedCallback);
            fileMinitor.Init();

            return options;
        }

        private static void DefaultCacheRemovedCallback(string key, object value, CacheItemRemovedReason reason)
        {
            // 由于事件发生时，文件可能还没有完全关闭，让程序等待50毫秒。
            System.Threading.Thread.Sleep(50);

            // 重新加载配置参数
            _runOptions = LoadRunOptions();
        }


        private static string[] GetConfigPath(T tEntity)
        {
            string configFilePath = null;
            List<ProjectConfigLevelItem> extraSettings = null;

            Type type = tEntity.GetType();
            ConfigNameAttribute cfgNameAttr = (ConfigNameAttribute)Attribute.GetCustomAttribute(type, typeof(ConfigNameAttribute));
            if (cfgNameAttr == null)
            {
                configFilePath = CombineAppDataPaths(Constant.DEFAULT_CONFIGPATHDIR, type.Name + Constant.DEFAULT_EXTNAME);

                extraSettings = _configLevelList;

                string[] arr = new string[extraSettings.Count + 1];
                arr[0] = configFilePath;
                for (int i = 0; i < extraSettings.Count; i++)
                {
                    arr[i + 1] = CombineAppDataPaths(Constant.DEFAULT_CONFIGPATHDIR,
                        string.Format("{0}.{1}{2}", type.Name, extraSettings[i].ConfigName, Constant.DEFAULT_EXTNAME));
                }
                return arr;
            }
            else
            {
                configFilePath = CombineAppDataPaths(cfgNameAttr.PathDirs, cfgNameAttr.SettingConfigName + cfgNameAttr.ExtName);

                extraSettings = cfgNameAttr.CfgLevelsProvider.GetConfigLevels();
                if (extraSettings != null && extraSettings.Count > 0)
                {
                    bool isExists = extraSettings.FindIndex(q => q.Index == 0) > -1;
                    if (isExists)
                    {
                        throw new Exception("IProjectConfigLevelProvider.GetConfigLevels() List<ProjectConfigLevelItem> can not has the default Index:1");
                    }
                }

                string[] arr = new string[extraSettings.Count + 1];
                arr[0] = configFilePath;
                for (int i = 0; i < extraSettings.Count; i++)
                {
                    arr[i + 1] = CombineAppDataPaths(cfgNameAttr.PathDirs, string.Format("{0}.{1}{2}",
                        cfgNameAttr.SettingConfigName, extraSettings[i].ConfigName, cfgNameAttr.ExtName));
                }
                return arr;
            }
        }



        private static readonly string AppWebDir = AppDomain.CurrentDomain.BaseDirectory.TrimEnd(Path.DirectorySeparatorChar);

        private static string CombineAppDataPaths(string[] paths, string fileName)
        {
            string[] pathsArr = (paths != null && paths.Length > 0) ? new string[paths.Length + 2] : new string[2];

            pathsArr[0] = AppWebDir;

            if (paths != null)
            {
                for (int i = 0; i < paths.Length; i++)
                {
                    pathsArr[i + 1] = paths[i];
                }
            }

            pathsArr[pathsArr.Length - 1] = fileName;
            string filePath = Path.Combine(pathsArr);
            return filePath;
        }

        private static string ReadFromFile(string filePath)
        {
            if (File.Exists(filePath))
            {
                var fs = new FileStream(filePath, FileMode.Open, FileAccess.Read, FileShare.Read);
                int bufferLen = Convert.ToInt32(fs.Length);
                byte[] buffer = new byte[bufferLen];
                fs.Position = 0;
                fs.Read(buffer, 0, bufferLen);
                fs.Close();
                fs.Dispose();

                string fileContent = Encoding.UTF8.GetString(buffer);
                return fileContent;
            }
            return string.Empty;
        }



    }

}
