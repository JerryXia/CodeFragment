/**
 * 
 */
package com.github.jerryxia.healthcheck.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import org.springside.modules.utils.io.FileUtil;
import org.springside.modules.utils.mapper.JsonMapper;

import com.github.jerryxia.devutil.dataobject.web.response.GeneralResponse;
import com.github.jerryxia.healthcheck.model.ServerNode;
import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;
import com.github.jerryxia.healthcheck.model.HealthCheckConf;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HealthCheckController extends BaseController {
    @Autowired
    private Environment env;

    private static String CONF_DIR;
    private static File   CONF_SERVER_NODES_FILE;

    @PostConstruct
    public void init() {
        CONF_DIR = env.getProperty("app.conf.dir");
        CONF_SERVER_NODES_FILE = new File(CONF_DIR + "/serverNodes.json");
        initFile(CONF_SERVER_NODES_FILE, "[]");
    }

    private void initFile(File file, String content) {
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
        String prettyContent = null;
        try {
            String confContent = FileUtil.toString(CONF_SERVER_NODES_FILE);
            val javaType = JsonMapper.INSTANCE.buildCollectionType(ArrayList.class, ServerNode.class);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, javaType);
            prettyContent = JsonMapper.INSTANCE.getMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(serverNodes);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }
        mv.addObject("appNodes", serverNodes);
        mv.addObject("prettyServerNodeConfContent", prettyContent);

        mv.addObject("menuKey", 1);
        mv.addObject("title", "应用节点清单");
        mv.addObject("keywords", "");
        mv.addObject("description", "");

        return mv;
    }
    @ResponseBody
    @PostMapping("/healthcheck/appNodesSave")
    public GeneralResponse appNodesSave(String content) {
        val response = okResponse();

        val javaType = JsonMapper.INSTANCE.buildCollectionType(ArrayList.class, ServerNode.class);
        ArrayList<ServerNode> serverNodes = JsonMapper.INSTANCE.fromJson(content, javaType);
        if (serverNodes != null) {
            String savingJsonContent = JsonMapper.INSTANCE.toJson(serverNodes);
            try {
                FileUtil.write(savingJsonContent, CONF_SERVER_NODES_FILE);
            } catch (IOException e) {
                failResponse(response, "保存失败");
            }
        } else {
            failResponse(response, "json格式不正确");
        }
        return response;
    }

    @GetMapping("/healthcheck/serverHealthCheckConfig")
    public ModelAndView serverHealthCheckConfig(@RequestParam(name = "s", defaultValue = "") String serverName) {
        ModelAndView mv = new ModelAndView("healthcheck/serverHealthCheckConfig");

        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(CONF_SERVER_NODES_FILE);
            val javaType = JsonMapper.INSTANCE.buildCollectionType(ArrayList.class, ServerNode.class);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, javaType);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }

        if(StringUtils.isNotEmpty(serverName)) {
            ServerNode currentServerNode = null;
            if (serverNodes != null) {
                for (val serverNode : serverNodes) {
                    if (serverName.equals(serverNode.getServerName())) {
                        currentServerNode = serverNode;
                        break;
                    }
                }
            }
            // 单个模式
            mv.addObject("itemEditMode", currentServerNode != null);
            mv.addObject("serverName", serverName);
            mv.addObject("currentServerHkConf", currentServerNode.getHkConf());
        } else {
            // 列表模式
            mv.addObject("itemEditMode", false);
        }
        mv.addObject("serverNodes", serverNodes);

        mv.addObject("menuKey", 2);
        mv.addObject("title", "应用设置");
        mv.addObject("keywords", "");
        mv.addObject("description", "");

        return mv;
    }


    @GetMapping("/healthcheck/serverHkRobots")
    public ModelAndView serverHkRobots() {
        ModelAndView mv = new ModelAndView("healthcheck/serverHkRobots");

        RecordLogViewStatusMessagesServlet.info("ererer", this);

        mv.addObject("menuKey", 3);
        mv.addObject("title", "检测Robot");
        mv.addObject("keywords", "");
        mv.addObject("description", "");
        return mv;
    }
    
}
