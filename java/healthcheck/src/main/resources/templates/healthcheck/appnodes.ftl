<#include "../header.ftl">
<#include "../commonStyles.ftl">
</head>
<body>
    <#include "../logoArea.ftl">

<div class="main">
    <#include "../leftMenu.ftl">
    <!-- 主内容 -->
    <section>
        <div class="section_main">
            <div class="section_auto">
                <h2 class="section_title">${title}</h2>
                <div id="tabView" class="ui-tab-tabs">
                    <a href="javascript:" class="ui-tab-tab checked" data-rel="tab0">UI配置</a>
                    <a href="javascript:" class="ui-tab-tab" data-rel="tab1">Json配置</a>
                </div>
                <div class="ui-tab-contents">
                    <div id="tab0" class="ui-tab-content checked">
                        <div class="mt30" style="">
                            <table class="ui-table">
                                <thead>
                                    <tr>
                                      <th scope="col">域名</th>
                                      <th scope="col">upstream_group</th>
                                      <th scope="col">自动检查模式</th>
                                      <th scope="col">节点</th>
                                      <th scope="col">权重</th>
                                      <th scope="col">存活</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <#if serverNodes?size gt 0 >
                                        <#list serverNodes as serverNode>
                                          <#list serverNode.groups?keys as groupKey>
                                            <#list serverNode.groups[groupKey].nodes as node>
                                                <tr>
                                                    <#assign autoHealthCheckModeId = "ahc,${serverNode.serverName},${groupKey}" >
                                                    <#if node?index == 0>
                                                        <td rowspan="${serverNode.groups[groupKey].nodes?size}">${serverNode.serverName}</td>
                                                        <td rowspan="${serverNode.groups[groupKey].nodes?size}">${groupKey}</td>
                                                        <td rowspan="${serverNode.groups[groupKey].nodes?size}">
                                                          <#if serverNode.groups[groupKey].autoHealthCheckMode>
                                                            <input type="checkbox" id="${autoHealthCheckModeId}" checked="checked" />
                                                            <label class="ui-switch" for="${autoHealthCheckModeId}"></label>
                                                          <#else>
                                                            <input type="checkbox" id="${autoHealthCheckModeId}" />
                                                            <label class="ui-switch" for="${autoHealthCheckModeId}"></label> 
                                                          </#if>
                                                        </td>
                                                    </#if>
                                                    <td>${node.ip}:${node.port?c}</td>
                                                    <td>${node.weight?c}</td>
                                                    <td>
                                                        <#assign activeId = "actived,${serverNode.serverName},${groupKey},${node.ip},${node.port?c}" >
                                                        <#if node.actived>
                                                            <input type="checkbox" id="${activeId}" checked="checked" <#if serverNode.groups[groupKey].autoHealthCheckMode>disabled="disabled"<#else></#if> />
                                                            <label class="ui-switch" for="${activeId}"></label>
                                                        <#else>
                                                            <input type="checkbox" id="${activeId}" <#if serverNode.groups[groupKey].autoHealthCheckMode>disabled="disabled"<#else></#if> />
                                                            <label class="ui-switch" for="${activeId}"></label> 
                                                        </#if>
                                                    </td>
                                                </tr>
                                            <#else>
                                                <tr><td>${appNode.serverName}</td><td></td><td></td><td></td><td></td></tr>
                                            </#list>
                                          </#list>
                                        </#list>
                                    </#if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="tab1" class="ui-tab-content">
                        <div>
                            <form action="/healthcheck/appNodesSave" id="appNodesForm" method="post">
                                <div class="fix mt15">
                                   <label class="left f14" for="content">配置</label>
                                   <div class="ovh">
                                       <div class="ui-textarea-x">
                                            <textarea id="content" name="content" rows="25">${prettyServerNodeConfContent}</textarea>
                                            <div class="ui-textarea"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="fix mt20">
                                    <span class="left">&nbsp;</span>
                                    <div class="cell">
                                        <input type="submit" class="clip" id="btnSave" disabled="disabled" />
                                        <label for="btnSave" class="ui-button ui-button-primary">保存</label>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<#include "../footer.ftl">