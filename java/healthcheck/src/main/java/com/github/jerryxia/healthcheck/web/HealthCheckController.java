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

import com.fasterxml.jackson.databind.JavaType;
import com.github.jerryxia.devutil.dataobject.web.response.GeneralResponse;
import com.github.jerryxia.healthcheck.common.Const;
import com.github.jerryxia.healthcheck.domain.ServerCheckFactory;
import com.github.jerryxia.healthcheck.domain.ServerNode;
import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HealthCheckController extends BaseController {
    @Autowired
    private Environment env;

    private static String CONF_DIR;

    @PostConstruct
    public void init() throws IOException {
        CONF_DIR = env.getProperty("app.conf.dir");
        Const.FTL_Configuration.setDirectoryForTemplateLoading(new File(CONF_DIR));
        Const.CONF_SERVER_NODES_FILE = new File(CONF_DIR + "/serverNodes.json");
        initFile(Const.CONF_SERVER_NODES_FILE, "[]");
        Const.ServerNodeArrayListType = JsonMapper.INSTANCE.buildCollectionType(ArrayList.class, ServerNode.class);
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
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ServerNodeArrayListType);
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

        ArrayList<ServerNode> serverNodes = JsonMapper.INSTANCE.fromJson(content, Const.ServerNodeArrayListType);
        if (serverNodes != null) {
            String savingJsonContent = JsonMapper.INSTANCE.toJson(serverNodes);
            try {
                FileUtil.write(savingJsonContent, Const.CONF_SERVER_NODES_FILE);
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
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ServerNodeArrayListType);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }

        if (StringUtils.isNotEmpty(serverName)) {
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

        mv.addObject("managers", ServerCheckFactory.MANAGERS);

        mv.addObject("menuKey", 3);
        mv.addObject("title", "检测Robot");
        mv.addObject("keywords", "");
        mv.addObject("description", "");
        return mv;
    }

    @GetMapping("/healthcheck/lbClassicStatusFrame")
    public ModelAndView lbClassicStatusFrame() {
        ModelAndView mv = new ModelAndView("healthcheck/lbClassicStatusFrame");
        mv.addObject("menuKey", 4);
        mv.addObject("title", "日志记录");
        mv.addObject("keywords", "");
        mv.addObject("description", "");
        return mv;
    }

    @ResponseBody
    @PostMapping("/healthcheck/refreshServerNodes")
    public GeneralResponse refreshServerNodes() {
        val response = okResponse();
        RecordLogViewStatusMessagesServlet.info("手动刷新配置......", this);
        ArrayList<ServerNode> serverNodes = null;
        try {
            String confContent = FileUtil.toString(Const.CONF_SERVER_NODES_FILE);
            serverNodes = JsonMapper.INSTANCE.fromJson(confContent, Const.ServerNodeArrayListType);
        } catch (IOException e) {
            log.error("serverNodes.json read fail", e);
        }
        if (serverNodes != null) {
            ServerCheckFactory.dispatch(serverNodes);
        } else {
            failResponse(response, "serverNodes读取异常");
        }
        return response;
    }
}
