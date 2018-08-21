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
                                <input type="text" id="path" class="ui-input" size="50" name="path" autocomplete="off" value="${instanceNodeGroup.hkConf.path!""}" required />
                            </div>
                        </div>
                        <div class="fix mt15">
                            <label class="left f14" for="queryWithTimestampParamName">携带时间戳的参数名称<span class="red abs">*</span></label>
                            <div class="ovh">
                                <input type="text" id="queryWithTimestampParamName" class="ui-input" size="20" name="queryWithTimestampParamName" autocomplete="off" value="${instanceNodeGroup.hkConf.queryWithTimestampParamName!""}" required />
                            </div>
                        </div>
                        <div class="fix mt15">
                            <label class="left f14" for="cookie">携带Cookie</label>
                            <div class="ovh">
                                <div class="ui-textarea-x" style="width:500px;">
                                    <textarea id="cookie" maxlength="140" rows="5">${instanceNodeGroup.hkConf.cookie!""}</textarea>
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
                    <div class="rel mt30">
                        <input type="button" id="btnRefreshFromConfig" class="ui-button ui-button-warning" value="刷新" />
                    </div>
                    <table class="ui-table mt15">
                        <thead>
                            <tr>
                              <th scope="col">#</th>
                              <th scope="col">域名</th>
                              <th scope="col">upstream_group</th>
                              <th scope="col">检测路径</th>
                              <th scope="col">携带时间戳的参数名称</th>
                              <th scope="col">携带Cookie</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if serverNodes??>
                                <#list serverNodes as serverNode>
                                    <#assign groupKeys = serverNode.groups?keys>
                                    <#list groupKeys as groupKey>
                                        <tr>
                                            <td><a href="/healthcheck/serverHealthCheckConfig?s=${serverNode.serverName}&g=${groupKey}" class="ui-button ui-button-primary" role="button">编辑</a></td>
                                            <td>${serverNode.serverName!""}</td>
                                            <td>${groupKey!""}</td>
                                            <td>${serverNode.groups[groupKey].hkConf.path!""}</td>
                                            <td>${serverNode.groups[groupKey].hkConf.queryWithTimestampParamName!""}</td>
                                            <td>${serverNode.groups[groupKey].hkConf.cookie!""}</td>
                                        </tr>
                                    </#list>
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

    $('#btnRefreshFromConfig').click(function() {
        var refreshConfirm = new Dialog().confirm('确定要刷新吗？确保你修改的是的正确格式的配置文件！', {
            buttons: [{
                events: {
                    click: function() {
                        $.post('/healthcheck/refreshServerNodes', {}, function(res) {
                            if(res.code === 0) {
                                refreshConfirm.remove();
                                location.href = '/healthcheck/lbClassicStatusFrame';
                            } else {
                                new Dialog().alert('<h6>'+res.msg+'</h6>', { type: 'warning' });
                            }
                        });
                    }
                }
            }, {}]
        });
    });
});
</script>
<#include "../footer.ftl">