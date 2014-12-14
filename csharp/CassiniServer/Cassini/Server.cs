namespace Cassini
{
    using Microsoft.Win32;
    using System;
    using System.Diagnostics;
    using System.Reflection;
    using System.Threading;
    using System.Web;
    using System.Web.Hosting;

    public class Server : MarshalByRefObject
    {
        private Host _host;
        private string _installPath;
        private string _physicalPath;
        private int _port;
        public WaitCallback _restartCallback;
        private string _virtualPath;

        public Server(int port, string virtualPath, string physicalPath)
        {
            this._port = port;
            this._virtualPath = virtualPath;
            this._physicalPath = physicalPath.EndsWith(@"\") ? physicalPath : (physicalPath + @"\");
            this._restartCallback = new WaitCallback(this.RestartCallback);
            this._installPath = this.GetInstallPathAndConfigureAspNetIfNeeded();
            this.CreateHost();
        }

        private void CreateHost()
        {
            this._host = (Host)ApplicationHost.CreateApplicationHost(typeof(Host), this._virtualPath, this._physicalPath);
            //this._host = CreateWorkerAppDomianWithHost(this._virtualPath, this._physicalPath, typeof(Host)) as Host;
            this._host.Configure(this, this._port, this._virtualPath, this._physicalPath, this._installPath);
        }


        private object CreateWorkerAppDomianWithHost(string virtualPath, string physicalPath, Type hostType)
        {
            //通过反射使用私有的BuildMangeHost类型，这种方式创建web应用程序域，不需程序集注册到GAC或者放置到bin文件夹
            //唯一的应用程序名
            string uniqueAppString = string.Concat(virtualPath, physicalPath.ToLowerInvariant());
            //获取唯一的Id
            string appId = (uniqueAppString.GetHashCode()).ToString("x");
            //在Web应用程序域中创建BuildManagerHost
            var appManager = ApplicationManager.GetApplicationManager();
            //System.Web.Compilation.BuildMangerHost是一个内部类，不能再CSDN查到
            var buildManagerHostType = typeof(HttpRuntime).Assembly.GetType("System.Web.Compilation.BuildManagerHost");
            //为应用程序域创建对象
            var buildManagerHost = appManager.CreateObject(appId, buildManagerHostType, virtualPath, physicalPath, false);
            //调用BuildManagerHost.RegisterAssembly方法将类型注册到应用程序域
            buildManagerHostType.InvokeMember("RegisterAssembly",
                                                BindingFlags.Instance | BindingFlags.InvokeMethod | BindingFlags.NonPublic,
                                                null, buildManagerHost, new object[2]
                                            {
                                                hostType.Assembly.FullName,
                                                hostType.Assembly.Location
                                            });
            //现在可以使用类型创建对象实例
            return appManager.CreateObject(appId, hostType, virtualPath, physicalPath, false);
        }

        private string GetInstallPathAndConfigureAspNetIfNeeded()
        {
            RegistryKey key = null;
            RegistryKey key2 = null;
            RegistryKey key3 = null;
            string str = null;
            try
            {
                try
                {
                    FileVersionInfo versionInfo = FileVersionInfo.GetVersionInfo(typeof(HttpRuntime).Module.FullyQualifiedName);
                    string str2 = string.Format("{0}.{1}.{2}.{3}", new object[] { versionInfo.FileMajorPart, versionInfo.FileMinorPart, versionInfo.FileBuildPart, versionInfo.FilePrivatePart });
                    string name = @"Software\Microsoft\ASP.NET\" + str2;
                    if (!str2.StartsWith("1.0."))
                    {
                        name = name.Substring(0, name.LastIndexOf('.') + 1) + "0";
                    }
                    key2 = Registry.LocalMachine.OpenSubKey(name);
                    if (key2 != null)
                    {
                        return (string)key2.GetValue("Path");
                    }
                    key = Registry.LocalMachine.OpenSubKey(@"Software\Microsoft\ASP.NET");
                    if (key == null)
                    {
                        key = Registry.LocalMachine.CreateSubKey(@"Software\Microsoft\ASP.NET");
                        key.SetValue("RootVer", str2);
                    }
                    string str4 = "v" + str2.Substring(0, str2.LastIndexOf('.'));
                    key3 = Registry.LocalMachine.OpenSubKey(@"Software\Microsoft\.NETFramework");
                    string str5 = (string)key3.GetValue("InstallRoot");
                    if (str5.EndsWith(@"\"))
                    {
                        str5 = str5.Substring(0, str5.Length - 1);
                    }
                    key2 = Registry.LocalMachine.CreateSubKey(name);
                    str = str5 + @"\" + str4;
                    key2.SetValue("Path", str);
                    key2.SetValue("DllFullPath", str + @"\aspnet_isapi.dll");
                    return str;
                }
                catch
                {
                }
            }
            finally
            {
                if (key2 != null)
                {
                    key2.Close();
                }
                if (key != null)
                {
                    key.Close();
                }
                if (key3 != null)
                {
                    key3.Close();
                }
            }
            return str;
        }

        public override object InitializeLifetimeService()
        {
            return null;
        }

        public void Restart()
        {
            ThreadPool.QueueUserWorkItem(this._restartCallback);
        }

        private void RestartCallback(object unused)
        {
            this.Stop();
            this.CreateHost();
            this.Start();
        }

        public void Start()
        {
            if (this._host != null)
            {
                this._host.Start();
            }
        }

        public void Stop()
        {
            if (this._host != null)
            {
                try
                {
                    this._host.Stop();
                }
                catch
                {
                }
            }
        }

        public string InstallPath
        {
            get
            {
                return this._installPath;
            }
        }

        public string PhysicalPath
        {
            get
            {
                return this._physicalPath;
            }
        }

        public int Port
        {
            get
            {
                return this._port;
            }
        }

        public string RootUrl
        {
            get
            {
                return ("http://localhost:" + this._port + this._virtualPath);
            }
        }

        public string VirtualPath
        {
            get
            {
                return this._virtualPath;
            }
        }
    }
}

