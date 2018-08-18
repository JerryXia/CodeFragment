/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springside.modules.utils.io.type.StringBuilderWriter;

import com.github.jerryxia.healthcheck.common.Const;
import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 *
 */
@Slf4j
public class ServerCheckManager {
    private ServerCheckManagerStatus status;
    private ArrayList<Thread>        serverCheckWorkers;

    private final ServerNode serverNode;
    private String           lastStatusHash;

    public ServerCheckManager(ServerNode node) throws IOException {
        Assert.notNull(node, "serverNode can not be null");
        Assert.notNull(node.getNodes(), "instanceNodes can not be null");

        if (StringUtils.isBlank(node.getVhostConfTplFilePath())) {
            node.setVhostConfTplFilePath(node.getServerName() + ".conf.ftl");
        }
        this.serverNode = node;
        this.status = ServerCheckManagerStatus.STOPPED;
    }

    public ServerCheckManagerStatus getStatus() {
        return status;
    }

    public ArrayList<Thread> getServerCheckWorkers() {
        return serverCheckWorkers;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }


    public synchronized void receiveUpdateReport(final CheckingInstanceNode checkingInstanceNode) {
        for (InstanceNode instanceNode : serverNode.getNodes()) {
            if (instanceNode.getIp().equals(checkingInstanceNode.getIp())
                    && instanceNode.getPort() == checkingInstanceNode.getPort()) {
                instanceNode.setActived(checkingInstanceNode.isActived());
                break;
            }
        }
        workForLoadBalance();
    }

    public void workForLoadBalance() {
        String newNginxConf = generateNginxConf(this.serverNode.getVhostConfTplFilePath());
        if (StringUtils.isBlank(newNginxConf)) {
            return;
        }
        String newNginxConfHash = DigestUtils.md5Hex(newNginxConf);
        if (newNginxConfHash.equalsIgnoreCase(this.lastStatusHash)) {
            // 不变
        } else {
            String confFileName = String.format("/etc/nginx/conf.d/%s.conf", this.serverNode.getServerName());
            try {
                FileUtils.writeStringToFile(new File(confFileName), newNginxConf);
                doubleInfo(String.format("生成新的%s", confFileName));
            } catch (IOException e) {
                log.error("FileUtils.writeStringToFile error", e);
            }

            doubleInfo("执行命令: nginx -t");
            String nginxConfCheckResult = callShell("nginx -t");
            if (nginxConfCheckResult != null && (nginxConfCheckResult.indexOf("syntax is ok") > -1
                    || nginxConfCheckResult.indexOf("test is successful") > -1)) {
                // nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
                // nginx: configuration file /etc/nginx/nginx.conf test is successful
                doubleInfo("执行命令: nginx -s reload 的结果--||");
                String nginxReloadResult = callShell("nginx -s reload");
                doubleInfo(nginxReloadResult);
                this.lastStatusHash = newNginxConfHash;
            } else {
                doubleError(nginxConfCheckResult);
            }
        }
    }

    public String generateNginxConf(String templateFilePath) {
        freemarker.template.Template ftl = null;
        try {
            ftl = Const.FTL_Configuration.getTemplate(templateFilePath);
        } catch (TemplateNotFoundException e) {
            doubleError("TemplateNotFound: " + templateFilePath);
        } catch (MalformedTemplateNameException e) {
            doubleError("MalformedTemplateName " + templateFilePath);
        } catch (ParseException e) {
            doubleError("Parse Fail: " + templateFilePath);
        } catch (IOException e) {
            doubleError("IOException: " + templateFilePath);
        }
        val sbWriter = new StringBuilderWriter(2048);
        if (ftl != null) {
            val dataModel = new HashMap<String, Object>();
            dataModel.put("serverName", this.serverNode.getServerName());
            dataModel.put("instanceNodes", this.serverNode.getNodes());
            try {
                ftl.process(dataModel, sbWriter);
            } catch (TemplateException e) {
                doubleError("TemplateException: " + templateFilePath);
            } catch (IOException e) {
                doubleError("IOException: " + templateFilePath);
            }
        }
        return sbWriter.toString();
    }

    public String callShell(String shellString) {
        String shellResult = null;
        Process process = null;
        try {
            // process = Runtime.getRuntime().exec(shellString);
            String[] cmdarray = { "/bin/sh", "-c", shellString };
            process = new ProcessBuilder(cmdarray).directory(null).redirectErrorStream(true).start();

            int exitValue = process.waitFor();
            if (0 == exitValue) {
                // OK
            } else {
                log.error("callShell fail, exitValue is :" + exitValue);
            }
            shellResult = IOUtils.toString(process.getInputStream(), "UTF-8");
        } catch (Exception e) {
            log.error("callShell error", e);
            shellResult = null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return shellResult;
    }

    private void dispatch() {
        if(serverCheckWorkers == null) {
            serverCheckWorkers = new ArrayList<Thread>(serverNode.getNodes().size());
        }
        for (int i = 0; i < serverNode.getNodes().size(); i++) {
            // 生成每个working的检查任务
            InstanceNode instanceNode = serverNode.getNodes().get(i);
            CheckingInstanceNode checkingInstanceNode = new CheckingInstanceNode();
            checkingInstanceNode.setServerName(serverNode.getServerName());
            BeanUtils.copyProperties(instanceNode, checkingInstanceNode);
            BeanUtils.copyProperties(serverNode.getHkConf(), checkingInstanceNode);
            checkingInstanceNode.setVhostConfTplFilePath(serverNode.getVhostConfTplFilePath());

            CheckTask checkTask = new CheckTask(this, checkingInstanceNode);
            Thread worker = new Thread(checkTask, serverNode.getServerName() + "-hkRobot-" + i);
            worker.setDaemon(true);
            serverCheckWorkers.add(worker);
        }
        for (Thread worker : serverCheckWorkers) {
            worker.start();
            doubleInfo(String.format("%s started", worker.getName()));
        }
    }

    private void destroy() {
        for (Thread worker : serverCheckWorkers) {
            worker.interrupt();
            RecordLogViewStatusMessagesServlet.info(String.format("%s interrupted", worker.getName()), this);
        }
        serverCheckWorkers.clear();
    }

    private void doubleInfo(String msg) {
        log.info(msg);
        RecordLogViewStatusMessagesServlet.info(msg, this);
    }

    private void doubleError(String msg) {
        log.error(msg);
        RecordLogViewStatusMessagesServlet.error(msg, this);
    }

    public synchronized void start() {
        switch (this.status) {
        case STOPPED:
            this.status = ServerCheckManagerStatus.STARTING;
            dispatch();
            this.status = ServerCheckManagerStatus.WORKING;
            break;
        case STARTING:
        case WORKING:
        case STPPING:
            // ignore
            break;
        default:
            break;
        }
    }

    public synchronized void stop() {
        switch (this.status) {
        case WORKING:
            this.status = ServerCheckManagerStatus.STPPING;
            destroy();
            this.status = ServerCheckManagerStatus.STOPPED;
            break;
        case STOPPED:
        case STARTING:
        case STPPING:
            // ignore
            break;
        default:
            break;
        }
    }
}
