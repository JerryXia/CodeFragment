<#include "../header.ftl">
<#include "../commonStyles.ftl">
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

                <#if allConfFiles??>
                    <p>files:</p>
                    <ul>
                        <#list allConfFiles as confTplFile>
                            <li><a href="?f=${confTplFile?url}"><span class="blue ui-tips">${confTplFile}</span></a></li>
                        </#list>
                    </ul>
                <#else>
                    <div>
                        <form action="/healthcheck/appNginxConfTplSave" id="appNginxConfTplForm" method="post">
                            <input type="hidden" name="filePath" value="${filePath}" />
                            <div class="fix mt15">
                               <label class="left f14" for="content">配置</label>
                               <div class="ovh">
                                   <div class="ui-textarea-x">
                                        <textarea id="content" name="content" rows="25">${queryFileContent}</textarea>
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
                </#if>

            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>

</script>
<#include "../footer.ftl">