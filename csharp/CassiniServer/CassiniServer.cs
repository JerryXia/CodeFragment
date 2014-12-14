using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Cassini;
using System.Diagnostics;
using System.Threading;

namespace CassiniServer
{
    public partial class CassiniServer : Form
    {
        private int port;

        public int Port
        {
            get { return port; }
            set { port = value; }
        }

        private string virtualPath;

        public string VirtualPath
        {
            get { return virtualPath; }
            set { virtualPath = value; }
        }
        private string physicalPath;

        public string PhysicalPath
        {
            get { return physicalPath; }
            set { physicalPath = value; }
        }

        Server server;
        public CassiniServer()
        {
            InitializeComponent();
        }

        private void CassiniServer_Load(object sender, EventArgs e)
        {
            try
            {
                this.SizeChanged += new System.EventHandler(this.Form1_SizeChanged);

                Random random = new Random();
                //Port = random.Next(1000, 65535);
                Port = 80;
                virtualPath = "/";
                physicalPath = Application.StartupPath.Substring(0, Application.StartupPath.LastIndexOf("\\"));
                server = new Server(Port, VirtualPath, PhysicalPath);
                server.Start();

                linkLabel1.Text = "http://localhost:" + port;
                textBox1.Text = port.ToString();
                textBox2.Text = virtualPath;
                textBox3.Text = physicalPath;
                linkLabel1_LinkClicked(null, null);

                notifyIcon1.Text = physicalPath;


                this.Visible = false;
                this.notifyIcon1.Visible = true;

                this.notifyIcon1.BalloonTipIcon = ToolTipIcon.Info;
                this.notifyIcon1.BalloonTipTitle = physicalPath;
                this.notifyIcon1.BalloonTipText = "http://localhost:" + port;
                this.notifyIcon1.ShowBalloonTip(2000);
            }
            catch(Exception ex)
            {
                // MessageBox.Show(this, "请将该文件考到网站的Bin目录在运行", "系统提示", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                // button1_Click(sender, e);
                MessageBox.Show(this, ex.ToString(), "系统提示", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                button1_Click(sender, e);
            }

        }
        private void Form1_SizeChanged(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Minimized)
            {
                this.Visible = false;
                this.notifyIcon1.Visible = true;
            }
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Process.Start(this.linkLabel1.Text);
        }

        private bool Exit;

        private void button1_Click(object sender, EventArgs e)
        {
            this.Exit = true;
            Application.Exit();
        }

        protected override void WndProc(ref Message m)
        {
            if (m.Msg == 0x11)
            {
                this.Exit = true;
            }
            base.WndProc(ref m);
        }


        private void CassiniServer_FormClosing(object sender, FormClosingEventArgs e)
        {

            if (!this.Exit)
            {
                e.Cancel = true;
                base.WindowState = FormWindowState.Minimized;
                base.Hide();

                this.notifyIcon1.BalloonTipIcon = ToolTipIcon.Info;
                this.notifyIcon1.BalloonTipTitle = physicalPath;
                this.notifyIcon1.BalloonTipText = "http://localhost:" + port;
                this.notifyIcon1.ShowBalloonTip(2000);
            }
            else
            {
                base.OnClosing(e);
                if (!e.Cancel)
                {
                    this.notifyIcon1.Dispose();
                }
            }
        }

        private void notifyIcon1_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            this.Visible = true;
            this.WindowState = FormWindowState.Normal;
            this.notifyIcon1.Visible = false;
        }

        private void 退出ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            button1_Click(sender, e);
        }

        private void 显示详细信息ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            notifyIcon1_MouseDoubleClick(sender, null);
        }

        private void toolStripMenuItem1_Click(object sender, EventArgs e)
        {
            linkLabel1_LinkClicked(sender, null);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            ThreadPool.QueueUserWorkItem(server._restartCallback);
            ThreadPool.QueueUserWorkItem(new WaitCallback(reStart));
        }

        private void reStart(object unused)
        {
            this.notifyIcon1.BalloonTipIcon = ToolTipIcon.Info;
            this.notifyIcon1.BalloonTipTitle = physicalPath;
            this.notifyIcon1.BalloonTipText = "http://localhost:" + port;
            this.notifyIcon1.ShowBalloonTip(2000);
        }

        private void toolStripMenuItem2_Click(object sender, EventArgs e)
        {
            button2_Click(sender, null);
        }
    }
}
