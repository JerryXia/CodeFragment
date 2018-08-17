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
                <div class="mt30">
                    <table class="ui-table">
                        <thead>
                            <tr>
                              <th scope="col">域名</th>
                              <th scope="col">节点</th>
                              <th scope="col">权重</th>
                              <th scope="col">存活</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if appNodes?size gt 0 >
                                <#list appNodes as appNode>
                                <#list appNode.nodes as node>
                                    <tr>
                                        <#if appNode.nodes?? >
                                            <#if node?index == 0>
                                                <td rowspan="${appNode.nodes?size}">${appNode.serverName}</td>
                                            </#if>
                                            <td>${node.ip}:${node.port?c}</td>
                                            <td>${node.weight?c}</td>
                                            <td>
                                                <#assign activeId = "switch${node.ip}_${node.port?c}" >
                                                <#if node.actived>
                                                    <input type="checkbox" id="${activeId}" checked="checked" disabled="disabled"/>
                                                    <label class="ui-switch" for="${activeId}"></label>
                                                <#else>
                                                    <input type="checkbox" id="${activeId}" disabled="disabled"/>
                                                    <label class="ui-switch" for="${activeId}"></label> 
                                                </#if>
                                            </td>
                                        <#else>
                                            <td>${appNode.serverName}</td>
                                            <td></td><td></td><td></td>
                                        </#if>
                                    </tr>
                                </#list>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>
seajs.config(config).use(['common/comp/Form', 'common/ui/Dialog'], function(Form, Dialog) {
    var myForm = new Form($('form'), {
        avoidSend: function() {
            if (appNodesForm.content.value && appNodesForm.content.value.length > 0) {
                myForm.ajax();
                return true;
            } else {
                new Dialog().alert('<h6>内容不能空！</h6>', { type: 'warning' });
                return true;
            }
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