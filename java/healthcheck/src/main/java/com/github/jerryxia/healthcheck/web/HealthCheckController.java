/**
 * 
 */
package com.github.jerryxia.healthcheck.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.utils.collection.CollectionUtil;
import org.springside.modules.utils.io.FilePathUtil;
import org.springside.modules.utils.io.FileTreeWalker;
import org.springside.modules.utils.io.FileUtil;
import org.springside.modules.utils.mapper.JsonMapper;

import com.github.jerryxia.devutil.dataobject.web.response.SimpleRes;
import com.github.jerryxia.healthcheck.common.Const;
import com.github.jerryxia.healthcheck.domain.ActuatorInstanceNode;
import com.github.jerryxia.healthcheck.domain.ServerCheckFactory;
import com.github.jerryxia.healthcheck.domain.SpringBootActuatorClient;
import com.github.jerryxia.healthcheck.domain.conf.InstanceNode;
import com.github.jerryxia.healthcheck.domain.conf.InstanceNodeGroup;
import com.github.jerryxia.healthcheck.domain.conf.ServerNode;
import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HealthCheckController extends BaseController {
    @Autowired
    private Environment env;

    @PostConstruct
    public void init() throws IOException {
        Const.CONF_DIR = env.getProperty("app.conf.dir");
        Const.CONF_DIR_FILE = new File(Const.CONF_DIR);
        Const.FTL_Configuration.setDirectoryForTemplateLoading(Const.CONF_DIR_FILE);
        Const.CONF_SERVER_NODES_FILE = new File(FilePathUtil.contact(Const.CONF_DIR, "serverNodes.json"));
        initFileIfNotExists(Const.CONF_SERVER_NODES_FILE, "[]");
    }

    private void initFileIfNotExists(File file, String content) {
        if (!FileUtil.isFileExists(file)) {
            try {
                FileUtil.touch(file);
                FileUtil.write(content, file);
            } catch (IOException e) {
                log.error(file.getName(), e);
            }
        }
    }

    @GetMapping("/healthcheck/")
    public String index() {
        return "redirect:/healthcheck/appNodes";
    }

    @GetMapping("/healthcheck/appNodes")
    public ModelAndView appNodesPage() {
        ModelAndView mv = new ModelAndView("healthcheck/appnodes");

        ArrayList<ServerNode> serverNodes = null;
        String confContent = null;
        try {
            confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ARRAYLIST_SERVERNODE_TYPE);
            // if pretty confContent
            // JsonMapper.INSTANCE.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(serverNodes);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }
        mv.addObject("serverNodes", serverNodes);
        mv.addObject("prettyServerNodeConfContent", confContent);

        mv.addObject("menuKey", 1);
        tkd(mv, "应用节点清单", null, null);
        return mv;
    }

    @ResponseBody
    @PostMapping("/healthcheck/appNodesSave")
    public SimpleRes appNodesSave(String content) {
        val response = new SimpleRes();

        ArrayList<ServerNode> serverNodes = JsonMapper.INSTANCE.fromJson(content, Const.ARRAYLIST_SERVERNODE_TYPE);
        if (serverNodes != null) {
            String savingJsonContent = JsonMapper.INSTANCE.toJson(serverNodes);
            try {
                FileUtil.write(savingJsonContent, Const.CONF_SERVER_NODES_FILE);
            } catch (IOException e) {
                response.failWithMsg("保存失败");
            }
        } else {
            response.failWithMsg("json格式不正确");
        }
        return response;
    }

    @GetMapping("/healthcheck/appNginxConfTpls")
    public ModelAndView appNginxConfTplsPage(@RequestParam(name = "f", defaultValue = "") String filePath) {
        ModelAndView mv = new ModelAndView("healthcheck/appNginxConfTpls");

        List<File> confTplFiles = FileTreeWalker.listFileWithExtension(Const.CONF_DIR_FILE, "ftl");
        if(StringUtils.isBlank(filePath)) {
            List<String> allConfFiles = confTplFiles.stream().map(q -> q.getAbsolutePath()).collect(Collectors.toList());
            mv.addObject("allConfFiles", allConfFiles);
        } else {
            File queryFile = null;
            try {
                queryFile = confTplFiles.stream().filter(q -> StringUtils.compare(filePath, q.getAbsolutePath()) == 0).findFirst().get();
            } catch(NullPointerException e) {
                log.error(filePath, e);
            }
            String queryFileContent = StringUtils.EMPTY;
            if(queryFile != null) {
                try {
                    queryFileContent = FileUtil.toString(queryFile);
                } catch (IOException e) {
                    
                }
            }
            mv.addObject("filePath", filePath);
            mv.addObject("queryFileContent", queryFileContent);
        }

        mv.addObject("menuKey", 2);
        tkd(mv, "Nginx配置模板清单", null, null);
        return mv;
    }
    @ResponseBody
    @PostMapping("/healthcheck/appNginxConfTplSave")
    public SimpleRes appNginxConfTplSave(String filePath, String content) {
        val response = new SimpleRes();

        try {
            FileUtil.write(content, new File(filePath));
        } catch (IOException e) {
            response.failWithMsg("保存失败");
        }
        return response;
    }

    @GetMapping("/healthcheck/serverHealthCheckConfig")
    public ModelAndView serverHealthCheckConfig(@RequestParam(name = "s", defaultValue = "") String serverName, @RequestParam(name = "g", defaultValue = "") String groupName) {
        ModelAndView mv = new ModelAndView("healthcheck/serverHealthCheckConfig");

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ARRAYLIST_SERVERNODE_TYPE);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }

        if (StringUtils.isNotEmpty(serverName) && StringUtils.isNotEmpty(groupName)) {
            ServerNode currentServerNode = null;
            if (serverNodes != null) {
                for (val serverNode : serverNodes) {
                    if (serverName.equals(serverNode.getServerName())) {
                        currentServerNode = serverNode;
                        break;
                    }
                }
            }
            InstanceNodeGroup instanceNodeGroup = null;
            if (currentServerNode != null) {
                instanceNodeGroup = currentServerNode.getGroups().get(groupName);
            }

            // 单个模式
            mv.addObject("itemEditMode", currentServerNode != null);
            mv.addObject("serverName", serverName);
            mv.addObject("currentServerNode", currentServerNode);
            mv.addObject("instanceNodeGroup", instanceNodeGroup);
        } else {
            // 列表模式
            mv.addObject("itemEditMode", false);
        }
        mv.addObject("serverNodes", serverNodes);

        mv.addObject("menuKey", 3);
        tkd(mv, "应用设置", null, null);
        return mv;
    }

    @GetMapping("/healthcheck/springbootActuatorLogSetting")
    public ModelAndView springbootActuatorLogSetting(@RequestParam(name = "s", defaultValue = "") String serverName, @RequestParam(name = "g", defaultValue = "") String groupName,
            @RequestParam(name = "n", defaultValue = "") String instanceNode, @RequestParam(name = "sln", defaultValue = "") String searchedloggerName) {
        ModelAndView mv = new ModelAndView("healthcheck/springbootActuatorLogSetting");

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ARRAYLIST_SERVERNODE_TYPE);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
            serverNodes = new ArrayList<ServerNode>();
        }
        ArrayList<String> serverNames = new ArrayList<String>(serverNodes.size());
        ArrayList<String> groupNames = new ArrayList<String>();
        ArrayList<String> instanceNodeNames = new ArrayList<String>();
        ServerNode selectedServerNode = null;
        InstanceNodeGroup selectedInstanceNodeGroup = null;
        InstanceNode selectedInstanceNode = null;
        boolean hasReqServerName = false;
        boolean hasReqGroupName = false;
        boolean hasReqInstanceNode = false;
        String loggersJsonContent = null;

        if (CollectionUtil.isNotEmpty(serverNodes)) {
            if (StringUtils.isNotEmpty(serverName)) {
                val serverNodesIterator = serverNodes.iterator();
                while (serverNodesIterator.hasNext()) {
                    val serverNode = serverNodesIterator.next();
                    serverNames.add(serverNode.getServerName());
                    if (StringUtils.compare(serverNode.getServerName(), serverName) == 0) {
                        hasReqServerName = true;
                        selectedServerNode = serverNode;
                        val groupEntrySetIterator = serverNode.getGroups().entrySet().iterator();
                        while (groupEntrySetIterator.hasNext()) {
                            val groupEntry = groupEntrySetIterator.next();
                            groupNames.add(groupEntry.getKey());
                        }
                    }
                }

                if (hasReqServerName) {
                    if (StringUtils.isNotEmpty(groupName)) {
                        val groupNamesIterator = groupNames.iterator();
                        while (groupNamesIterator.hasNext()) {
                            if (StringUtils.compare(groupName, groupNamesIterator.next()) == 0) {
                                hasReqGroupName = true;
                                selectedInstanceNodeGroup = selectedServerNode.getGroups().get(groupName);
                            }
                        }

                        if (hasReqGroupName && StringUtils.isNotEmpty(instanceNode)) {
                            val instanceNodesIterator = selectedInstanceNodeGroup.getNodes().iterator();
                            while (instanceNodesIterator.hasNext()) {
                                val instanceNodeItem = instanceNodesIterator.next();
                                String instanceNodeName = String.format("%s:%d", instanceNodeItem.getIp(), instanceNodeItem.getPort());
                                instanceNodeNames.add(instanceNodeName);
                                if (StringUtils.compare(instanceNode, instanceNodeName) == 0) {
                                    hasReqInstanceNode = true;
                                    selectedInstanceNode = instanceNodeItem;
                                }
                            }

                            if (hasReqInstanceNode) {
                                if (selectedInstanceNodeGroup.getMsConf() != null) {
                                    val node = new ActuatorInstanceNode();
                                    node.setServerName(serverName);
                                    node.setIp(selectedInstanceNode.getIp());
                                    node.setPort(selectedInstanceNode.getPort());
                                    node.setQueryWithTimestampParamName(selectedInstanceNodeGroup.getMsConf().getQueryWithTimestampParamName());
                                    node.setContextPath(selectedInstanceNodeGroup.getMsConf().getContextPath());
                                    node.setHeader(selectedInstanceNodeGroup.getMsConf().getHeader());
                                    loggersJsonContent = new SpringBootActuatorClient().getLoggers(node);
                                } else {
                                    return getMvRedirect("/healthcheck/appNodes", "msConf属性还未配置！");
                                }
                            } else {

                            }
                        } else {
                            val instanceNodesIterator = selectedInstanceNodeGroup.getNodes().iterator();
                            while (instanceNodesIterator.hasNext()) {
                                val instanceNodeItem = instanceNodesIterator.next();
                                String instanceNodeName = String.format("%s:%d", instanceNodeItem.getIp(), instanceNodeItem.getPort());
                                instanceNodeNames.add(instanceNodeName);
                            }
                        }
                    }
                }
            } else {
                val serverNodesIterator = serverNodes.iterator();
                while (serverNodesIterator.hasNext()) {
                    val serverNode = serverNodesIterator.next();
                    serverNames.add(serverNode.getServerName());
                }
            }
        }

        mv.addObject("serverNodes", serverNodes);
        mv.addObject("serverNames", serverNames);
        mv.addObject("groupNames", groupNames);
        mv.addObject("instanceNodeNames", instanceNodeNames);

        mv.addObject("s", serverName);
        mv.addObject("g", groupName);
        mv.addObject("n", instanceNode);
        mv.addObject("sln", searchedloggerName);
        mv.addObject("loggersJsonContent", loggersJsonContent);

        mv.addObject("menuKey", 4);
        tkd(mv, "SpringBoot-Actuator-Log设置", null, null);
        return mv;
    }

    @ResponseBody
    @PostMapping("/healthcheck/modifyLoggerLevel")
    public SimpleRes modifyLoggerLevel(String serverName, String groupName, String ip, int port, String loggerName, String configuredLevel) {
        val response = new SimpleRes();

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ARRAYLIST_SERVERNODE_TYPE);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
            serverNodes = new ArrayList<ServerNode>();
        }

        val serverNode = serverNodes.stream().filter(q -> serverName.equals(q.getServerName())).findFirst().get();

        val node = new ActuatorInstanceNode();
        node.setServerName(serverName);
        node.setIp(ip);
        node.setPort(port);
        node.setContextPath(serverNode.getGroups().get(groupName).getMsConf().getContextPath());
        node.setHeader(serverNode.getGroups().get(groupName).getMsConf().getHeader());
        String moidfyResult = new SpringBootActuatorClient().modifyLoggerLevel(node, loggerName, configuredLevel);
        log.debug(moidfyResult);
        return response;
    }

    @GetMapping("/healthcheck/serverHkRobots")
    public ModelAndView serverHkRobots() {
        ModelAndView mv = new ModelAndView("healthcheck/serverHkRobots");

        mv.addObject("managers", ServerCheckFactory.MANAGERS);

        mv.addObject("menuKey", 11);
        tkd(mv, "检测Robots", null, null);
        return mv;
    }

    @GetMapping("/healthcheck/lbClassicStatusFrame")
    public ModelAndView lbClassicStatusFrame() {
        ModelAndView mv = new ModelAndView("healthcheck/lbClassicStatusFrame");
        mv.addObject("menuKey", 12);
        tkd(mv, "日志记录", null, null);
        return mv;
    }

    @ResponseBody
    @PostMapping("/healthcheck/refreshServerNodes")
    public SimpleRes refreshServerNodes(String serverNames) {
        val response = new SimpleRes();
        RecordLogViewStatusMessagesServlet.info("手动刷新指定serverNames: " + JsonMapper.INSTANCE.toJson(serverNames), this);

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ARRAYLIST_SERVERNODE_TYPE);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }
        if (serverNodes != null) {
            String[] serverNameArray = StringUtils.split(serverNames, ',');
            val needRefreshServerNodes = new ArrayList<ServerNode>(serverNameArray.length);
            for (String serverName : serverNameArray) {
                try {
                    val serverNode = serverNodes.stream().filter(q -> serverName.equals(q.getServerName())).findFirst().get();
                    needRefreshServerNodes.add(serverNode);
                } catch (NullPointerException e) {

                }
            }
            ServerCheckFactory.dispatch(needRefreshServerNodes);
        } else {
            response.failWithMsg("serverNodes读取异常");
        }
        return response;
    }

    @ResponseBody
    @PostMapping("/healthcheck/refreshServerNodeNginxConf")
    public SimpleRes refreshServerNodeNginxConf(String serverNames) {
        val response = new SimpleRes();
        RecordLogViewStatusMessagesServlet.info("手动刷新nginx.conf: " + JsonMapper.INSTANCE.toJson(serverNames), this);

        String[] serverNameArray = StringUtils.split(serverNames, ',');
        for (String serverName : serverNameArray) {
            val serverCheckManager = ServerCheckFactory.MANAGERS.get(serverName);
            if (serverCheckManager != null) {
                // 重新刷新配置文件
                serverCheckManager.workForLoadBalance();
                RecordLogViewStatusMessagesServlet.info(String.format("%s刷新OK", serverName), this);
            } else {
                RecordLogViewStatusMessagesServlet.error(String.format("%s刷新失败，不存在此serverName", serverName), this);
            }
        }
        return response;
    }
}
