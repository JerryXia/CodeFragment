<#include "../header.ftl">
<#include "../commonStyles.ftl">
<style>
label.left, span.left {
    width: 150px;
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
                <#if itemEditMode>
                    <form action="/healthcheck/serverHealthCheckConfigSave" method="post" id="serverConfForm" class="mt30">
                        <div class="fix mt15">
                            <label class="left f14 w100" for="serverName">域名<span class="red abs">*</span></label>
                            <div class="ovh">
                                <input type="text" id="serverName" class="ui-input" size="40" name="serverName" value="${serverName!""}" required />
                            </div>
                        </div>
                        <div class="fix mt15">
                            <label class="left f14" for="path">检测路径<span class="red abs">*</span></label>
                            <div class="ovh">
                                <input type="text" id="path" class="ui-input" size="50" name="path" autocomplete="off" value="${currentServerHkConf.path!""}" required />
                            </div>
                        </div>
                        <div class="fix mt15">
                            <label class="left f14" for="queryWithTimestampParamName">携带时间戳的参数名称<span class="red abs">*</span></label>
                            <div class="ovh">
                                <input type="text" id="queryWithTimestampParamName" class="ui-input" size="20" name="queryWithTimestampParamName" autocomplete="off" value="${currentServerHkConf.queryWithTimestampParamName!""}" required />
                            </div>
                        </div>
                        <div class="fix mt15">
                            <label class="left f14" for="cookie">携带Cookie</label>
                            <div class="ovh">
                                <div class="ui-textarea-x" style="width:500px;">
                                    <textarea id="cookie" maxlength="140" rows="5">${currentServerHkConf.cookie!""}</textarea>
                                    <div class="ui-textarea"></div>
                                </div>
                            </div>
                        </div>
                        <div class="fix mt20">
                            <span class="left">&nbsp;</span>
                            <div class="cell">
                                <input type="submit" id="confSave" class="clip">
                                <label for="confSave" class="ui-button ui-button-primary">保存</label>
                            </div>
                        </div>
                    </form>
                <#else>
                    <table class="ui-table mt15">
                        <thead>
                            <tr>
                              <th scope="col">#</th>
                              <th scope="col">域名</th>
                              <th scope="col">检测路径</th>
                              <th scope="col">携带时间戳的参数名称</th>
                              <th scope="col">携带Cookie</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if serverNodes??>
                                <#list serverNodes as serverNode>
                                    <tr>
                                        <td><a href="/healthcheck/serverHealthCheckConfig?s=${serverNode.serverName}" class="ui-button ui-button-primary" role="button">编辑</a></td>
                                        <td>${serverNode.serverName!""}</td>
                                        <td>${serverNode.hkConf.path!""}</td>
                                        <td>${serverNode.hkConf.queryWithTimestampParamName!""}</td>
                                        <td>${serverNode.hkConf.cookie!""}</td>
                                    </tr>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                </#if>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>
seajs.config(config).use(['common/comp/Form', 'common/ui/Dialog'], function(Form, Dialog) {
    var myForm = new Form($('form'), {
        avoidSend: function() {
            
        },
        success: function(res) {
            // 表单重置
            // this[0].reset();
            new Dialog().alert('<h6>修改成功！</h6>', { type: 'success' });
            location.reload();
        }
    }, {
        label: true
    });
});
</script>
<#include "../footer.ftl">