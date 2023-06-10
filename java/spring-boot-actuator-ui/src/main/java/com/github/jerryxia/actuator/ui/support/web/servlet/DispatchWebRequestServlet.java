package com.github.jerryxia.actuator.ui.support.web.servlet;

import com.github.jerryxia.actuator.ui.support.json.JSONWriter;
import com.github.jerryxia.actuator.ui.support.json.RuntimeJsonComponentProviderFactory;
import com.github.jerryxia.actuator.ui.support.spring.SpringTemplateLoader;
import com.github.jerryxia.actuator.ui.util.StringBuilderWriter;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Administrator
 */
@SuppressWarnings("serial")
public class DispatchWebRequestServlet extends AbstractResourceServlet {

    private final static int RESULT_CODE_SUCCESS = 1;
    private final static int RESULT_CODE_ERROR = -1;
    private static final String RESOURCE_PATH = "com/github/jerryxia/actuator/ui/resources";
    private final freemarker.template.Configuration ftlConfiguration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
    private String urlPrefix;

    public DispatchWebRequestServlet() {
        super(RESOURCE_PATH);
    }

    @Override
    public void init() throws ServletException {
        getServletContext().log("spring-boot-actuator-ui DispatchWebServlet     : init");
        this.urlPrefix = getInitParameter("urlPrefix");
        getServletContext().log("spring-boot-actuator-ui DispatchWebServlet     : urlPrefix=" + this.urlPrefix);

        this.ftlConfiguration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        //ftlConfiguration.setClassForTemplateLoading(Bootstrapper.class, "");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        try {
            Resource path = resourceLoader.getResource("classpath:" + resourcePath);
            File file = path.getFile();  // will fail if not resolvable in the file system
            getServletContext().log("Template loader path [" + path + "] resolved to file path [" + file.getAbsolutePath() + "]");
            this.ftlConfiguration.setTemplateLoader(new FileTemplateLoader(file));
        } catch (Exception ex) {
            getServletContext().log("Cannot resolve template loader path [" + resourcePath + "] to [java.io.File]: using SpringTemplateLoader as fallback", ex);
            this.ftlConfiguration.setTemplateLoader(new SpringTemplateLoader(resourceLoader, "classpath:" + resourcePath));
        }
    }

    @Override
    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException, IOException {
        String filePath = getFilePath(fileName);

        if (fileName.endsWith(".jpg")) {
            byte[] bytes = super.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
            return;
        }
        String text = null;
        if (fileName.endsWith(".html")) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            text = super.readFromResource(filePath);
        } else if (fileName.endsWith(".css")) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/css");
            text = super.readFromResource(filePath);
        } else if (fileName.endsWith(".js")) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/javascript");
            text = super.readFromResource(filePath);
        } else {
            HashMap<String, Object> viewData = tkd(fileName, fileName, fileName, uri);
            if(fileName.startsWith("/index")) {
                viewData.put("page_global_navIndex", 19.9);
            }
            if(fileName.startsWith("/env")) {
                viewData.put("page_global_navIndex", 11.1);
            }
            if(fileName.startsWith("/mappings")) {
                viewData.put("page_global_navIndex", 11.2);
            }
            if(fileName.startsWith("/beans")) {
                viewData.put("page_global_navIndex", 11.3);
            }
            if(fileName.startsWith("/loggers")) {
                viewData.put("page_global_navIndex", 11.4);
            }
            if(fileName.startsWith("/trace")) {
                viewData.put("page_global_navIndex", 11.5);
            }
            if(fileName.startsWith("/threads")) {
                viewData.put("page_global_navIndex", 11.6);
            }
            if(fileName.startsWith("/details")) {
                viewData.put("page_global_navIndex", 11.7);
            }
            text = generateFtlContent(this.ftlConfiguration, fileName.substring(1) + ".ftl", viewData);
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
        }
        response.getWriter().write(text);
    }

    @Override
    protected String process(String url, HttpServletRequest req) {
        long startTime = System.nanoTime();
        Map<String, String> parameters = getParameters(url);
        String jsonpCallback = parameters.get("callback");
        String resp = null;

        // if (url.startsWith("/xxx.json")) {
        //     resp = returnJsonpResult(jsonpCallback, RESULT_CODE_SUCCESS, buildSnoopResult(req), getServerStat(startTime));
        // }

        // if (resp == null) {
        //     resp = returnJSONResult(RESULT_CODE_ERROR, null, getServerStat(startTime));
        // }
        return resp;
    }

    private Map<String, String> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String parametersStr = subString(url, "?", null);
        if (parametersStr == null || parametersStr.length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String[] parametersArray = parametersStr.split("&");
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        for (String parameterStr : parametersArray) {
            int index = parameterStr.indexOf("=");
            if (index <= 0) {
                continue;
            }

            String name = parameterStr.substring(0, index);
            String value = parameterStr.substring(index + 1);
            parameters.put(name, value);
        }
        return parameters;
    }

    private String subString(String src, String start, String to) {
        int indexFrom = start == null ? 0 : src.indexOf(start);
        int indexTo = to == null ? src.length() : src.indexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom > indexTo) {
            return null;
        }

        if (null != start) {
            indexFrom += start.length();
        }
        return src.substring(indexFrom, indexTo);
    }

    private Map<String, Long> getServerStat(long startTime) {
        Map<String, Long> map = new HashMap<String, Long>(2);
        map.put("time", System.currentTimeMillis());
        map.put("generated", System.nanoTime() - startTime);
        return map;
    }

    private String toJSONString(Object o) {
        try {
            return RuntimeJsonComponentProviderFactory.tryFindImplementation().toJson(o);
        } catch (NoClassDefFoundError error) {
            JSONWriter writer = new JSONWriter();
            writer.writeObject(o);
            return writer.toString();
        }
    }

    private HashMap<String, Object> tkd(String title, String keywords, String description, String uri) {
        HashMap<String, Object> viewData = new HashMap<String, Object>();
        viewData.put("title", title);
        viewData.put("description", description);
        viewData.put("keywords", keywords);
        viewData.put("page_global_actuatorUiPathPrefix", uri);
        viewData.put("page_global_dashboardPathPrefix", this.urlPrefix != null ? this.urlPrefix : "");
        return viewData;
    }

    private String generateTemplateContent(freemarker.template.Configuration ftlConfiguration, String templateContent, HashMap<String, Object> viewData) throws IOException {
        freemarker.template.Template ftl = new freemarker.template.Template(templateContent, templateContent, ftlConfiguration);
        StringBuilderWriter sbWriter = new StringBuilderWriter(2048);
        HashMap<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("viewData", viewData);
        try {
            ftl.process(dataModel, sbWriter);
        } catch (TemplateException e) {
            getServletContext().log("TemplateException: " + templateContent, e);
        } catch (IOException e) {
            getServletContext().log("IOException: " + templateContent, e);
        }
        return sbWriter.toString();
    }

    private String generateFtlContent(freemarker.template.Configuration ftlConfiguration, String templateFilePath, HashMap<String, Object> viewData) {
        freemarker.template.Template ftl = null;
        try {
            ftl = ftlConfiguration.getTemplate(templateFilePath);
        } catch (TemplateNotFoundException e) {
            getServletContext().log("TemplateNotFound: " + templateFilePath, e);
        } catch (MalformedTemplateNameException e) {
            getServletContext().log("MalformedTemplateName " + templateFilePath, e);
        } catch (ParseException e) {
            getServletContext().log("Parse Fail: " + templateFilePath, e);
        } catch (IOException e) {
            getServletContext().log("IOException: " + templateFilePath, e);
        }
        StringBuilderWriter sbWriter = new StringBuilderWriter(1024 * 16);
        if (ftl != null) {
            HashMap<String, Object> dataModel = new HashMap<String, Object>();
            dataModel.put("viewData", viewData);
            try {
                ftl.process(dataModel, sbWriter);
            } catch (TemplateException e) {
                getServletContext().log("TemplateException: " + templateFilePath, e);
            } catch (IOException e) {
                getServletContext().log("IOException: " + templateFilePath, e);
            }
        }
        return sbWriter.toString();
    }

}
