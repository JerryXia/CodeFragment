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
import org.springside.modules.utils.io.FileUtil;
import org.springside.modules.utils.io.type.StringBuilderWriter;
import org.springside.modules.utils.mapper.JsonMapper;
import org.springside.modules.utils.text.Charsets;

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
    private final ServerNode         serverNode;
    private ServerCheckManagerStatus status;
    private ArrayList<Thread>        serverCheckWorkers;
    private String                   lastStatusHash;

    public ServerCheckManager(ServerNode node) throws IOException {
        Assert.notNull(node, "serverNode can not be null");
        Assert.notNull(node.getGroups(), "groups can not be null");

        if (StringUtils.isBlank(node.getVhostConfTplFilePath())) {
            node.setVhostConfTplFilePath(node.getServerName() + ".conf.ftl");
        }
        this.serverNode = node;
        this.status = ServerCheckManagerStatus.STOPPED;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }

    public ServerCheckManagerStatus getStatus() {
        return status;
    }

    public ArrayList<Thread> getServerCheckWorkers() {
        return serverCheckWorkers;
    }

    public synchronized void receiveUpdateReport(final String groupName, final CheckingInstanceNode checkingInstanceNode) {
        InstanceNodeGroup instanceNodeGroup = serverNode.getGroups().get(groupName);
        if (instanceNodeGroup != null) {
            for (InstanceNode instanceNode : instanceNodeGroup.getNodes()) {
                if (instanceNode.getIp().equals(checkingInstanceNode.getIp()) && instanceNode.getPort() == checkingInstanceNode.getPort()) {
                    instanceNode.setActived(checkingInstanceNode.isActived());
                    break;
                }
            }
        }
        workForLoadBalance();

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ServerNodeArrayListType);
            for (int i = 0; i < serverNodes.size(); i++) {
                ServerNode curr = serverNodes.get(i);
                if (this.serverNode.getServerName().equals(curr.getServerName())) {
                    serverNodes.set(i, this.serverNode);
                    break;
                }
            }
            String savingJsonContent = JsonMapper.INSTANCE.toJson(serverNodes);
            FileUtil.write(savingJsonContent, Const.CONF_SERVER_NODES_FILE);
        } catch (IOException e) {
            log.error("serverNodes.json read or write fail", e);
        }
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
                FileUtils.writeStringToFile(new File(confFileName), newNginxConf, Charsets.UTF_8);
                doubleInfo(String.format("生成新的%s", confFileName));
            } catch (IOException e) {
                log.error("FileUtils.writeStringToFile error", e);
            }

            doubleInfo("执行命令: nginx -t");
            String nginxConfCheckResult = callShell("nginx -t");
            if (nginxConfCheckResult != null && (nginxConfCheckResult.indexOf("syntax is ok") > -1 || nginxConfCheckResult.indexOf("test is successful") > -1)) {
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
            dataModel.put("instanceNodeGroups", this.serverNode.getGroups());
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
        if (this.serverCheckWorkers == null) {
            this.serverCheckWorkers = new ArrayList<Thread>();
        }
        val entrySetIterator = serverNode.getGroups().entrySet().iterator();
        while (entrySetIterator.hasNext()) {
            val entry = entrySetIterator.next();
            String groupName = entry.getKey();
            InstanceNodeGroup instanceNodeGroup = entry.getValue();
            for (int j = 0; j < instanceNodeGroup.getNodes().size(); j++) {
                val instanceNode = instanceNodeGroup.getNodes().get(j);
                // 生成每个working的检查任务
                CheckingInstanceNode checkingInstanceNode = new CheckingInstanceNode();
                checkingInstanceNode.setServerName(serverNode.getServerName());
                BeanUtils.copyProperties(instanceNode, checkingInstanceNode);
                BeanUtils.copyProperties(instanceNodeGroup.getHkConf(), checkingInstanceNode);
                checkingInstanceNode.setVhostConfTplFilePath(serverNode.getVhostConfTplFilePath());

                CheckTask checkTask = new CheckTask(this, groupName, checkingInstanceNode);
                String workerName = String.format("%s-%s-hcWorker-%d", serverNode.getServerName(), groupName, j);
                Thread worker = new Thread(checkTask, workerName);
                worker.setDaemon(true);
                serverCheckWorkers.add(worker);
            }
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
