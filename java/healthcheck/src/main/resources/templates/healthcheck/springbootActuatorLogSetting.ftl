<#include "../header.ftl">
<#include "../commonStyles.ftl">
<link rel="stylesheet" href="https://qidian.gtimg.com/lulu/theme/peak/css/common/comp/Table.css">
<style>
label.left, span.left {
    width: 150px;
}
.section_main ul li, .section_main ol li {
    margin: 0px;
}
span.logger:hover {
    background-color: #ECEEF3;
    cursor: pointer;
}
.green {
    color: #5CB85C;
}
.blue {
    color: #5BC0DE;
}
.orange {
    color: #F0AD4E;
}
.red {
    color: #D9534F;
}
#loggerLevels {
    opacity: 1;
}
</style>
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
                <form id="searchLoggersForm" action="/healthcheck/springbootActuatorLogSetting" method="get">
                    Server：<select name="s">
                        <option value="">请选择</option>
                        <#list serverNames as serverName>
                            <option value="${serverName}" <#if serverName == s >selected="selected"</#if> >${serverName}</option>
                        </#list>
                    </select>
                    Group：<select name="g">
                        <option value="">请选择</option>
                        <#list groupNames as groupName>
                            <option value="${groupName}" <#if groupName == g >selected="selected"</#if> >${groupName}</option>
                        </#list>
                    </select>
                    Node：<select name="n">
                        <option value="">请选择</option>
                        <#list instanceNodeNames as instanceNodeName>
                            <option value="${instanceNodeName}" <#if instanceNodeName == n >selected="selected"</#if> >${instanceNodeName}</option>
                        </#list>
                    </select>
                </form>

                <p>loggers:</p>
                <p id="ROOT"></p>

            </div>
        </div>
    </section>
</div>

<input type="hidden" id="s" value="${(s!'')?html}" />
<input type="hidden" id="g" value="${(g!'')?html}" />
<input type="hidden" id="n" value="${(n!'')?html}" />
<div id="loggerLevelSetting" class="dn abs zx1" style="padding:20px; background-color: #fff;">
    <select id="loggerLevels">
        <option value="">未设置</option>
        <option value="OFF">OFF</option>
        <option value="ERROR">ERROR</option>
        <option value="WARN">WARN</option>
        <option value="INFO">INFO</option>
        <option value="DEBUG">DEBUG</option>
        <option value="TRACE">TRACE</option>
    </select>
</div>

<#include "../commonScripts.ftl">
<script src="https://cdn.staticfile.org/blueimp-md5/2.10.0/js/md5.min.js"></script>
<script>
let currSelectedLoggers = ${loggersJsonContent!'{}'};
</script>
<#include "../footer.ftl">