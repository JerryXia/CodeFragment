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
                                                    <#if node?index == 0>
                                                        <td rowspan="${serverNode.groups[groupKey].nodes?size}">${serverNode.serverName}</td>
                                                        <td rowspan="${serverNode.groups[groupKey].nodes?size}">${groupKey}</td>
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
<script>
if (appNodesForm.content.value && appNodesForm.content.value.length > 0) {
    var contentObj = JSON.parse(appNodesForm.content.value);
    var contentObjPrettyJson = JSON.stringify(contentObj, null, 2);
    appNodesForm.content.value = contentObjPrettyJson;
}

seajs.config(config).use(['common/comp/Form', 'common/ui/Dialog', 'common/ui/Tab'], function(Form, Dialog, Tab) {
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


    // 点击第二个选项卡
    $('a[data-rel=tab1]').on('click', function() {
        
    });

    // 选项卡方法
    new Tab($('#tabView > a.ui-tab-tab').filter(function () {
        return /^(:?javas|#)/.test(this.getAttribute('href'));
    }), {
        callback: function () {
            var line;
            // IE10+
            if ($.isFunction(history.pushState)) {
                line = $(this).parent().find('i');
                if (!line.length) {
                    line = $('<i></i>').addClass('ui-tab-line').prependTo($(this).parent());
                }
                line.css({
                    display: 'block',
                    width: $(this).width(),
                    left: $(this).position().left
                });
            }
        }
    });
});
</script>
<#include "../footer.ftl">