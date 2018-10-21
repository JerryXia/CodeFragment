<#include "../header.ftl">
<#include "../commonStyles.ftl">
<link rel="stylesheet" href="https://qidian.gtimg.com/lulu/theme/peak/css/common/comp/Table.css">
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
                <h2 class="section_title">${title}</h2>
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
                            <label class="left f14" for="header">携带Header</label>
                            <div class="ovh">
                                <div class="ui-textarea-x" style="width:500px;">
                                    <textarea id="header" name="header" maxlength="140" rows="5">${instanceNodeGroup.hkConf.header!""}</textarea>
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
                    <div class="table-x table-checkbox">
                        <div id="tbOpt_checkconfs" class="table-header">
                            <h2 class="table-title">检测uri: http://{域名}{检测路径}?{时间戳的参数名}=Date.now()</h2>
                            <div class="table-operate">
                                <a href="javascript:" class="dark" id="btnRefreshFromConfig" ><i class="fas fa-redo-alt"></i> 刷新</a>
                            </div>
                        </div>
                        <table id="tb_checkconfs" class="ui-table">
                            <thead>
                                <tr>
                                  <th scope="col"><input type="checkbox" id="chkAll"><label class="ui-checkbox" for="chkAll"></label></th>
                                  <th scope="col" width="140">域名</th>
                                  <th scope="col" width="140">upstream_group</th>
                                  <th scope="col" width="140">检测路径</th>
                                  <th scope="col" width="100">时间戳的参数名</th>
                                  <th scope="col">携带Header</th>
                                  <th scope="col" width="100">#</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#if serverNodes??>
                                    <#list serverNodes as serverNode>
                                        <#assign groupKeys = serverNode.groups?keys>
                                        <#list groupKeys as groupKey>
                                            <tr>
                                                <td><input type="checkbox" id="chk_tb_checkconf_${serverNode?index}_${groupKey?index}"><label class="ui-checkbox" for="chk_tb_checkconf_${serverNode?index}_${groupKey?index}"></label></td>
                                                <td>${serverNode.serverName!""}</td>
                                                <td>${groupKey!""}</td>
                                                <td>${serverNode.groups[groupKey].hkConf.path!""}</td>
                                                <td>${serverNode.groups[groupKey].hkConf.queryWithTimestampParamName!""}</td>
                                                <td><div class="ell">${serverNode.groups[groupKey].hkConf.header!""}</div></td>
                                                <td><a href="/healthcheck/serverHealthCheckConfig?s=${serverNode.serverName?html}&g=${groupKey?html}" class="ui-button ui-button-primary" role="button">编辑</a></td>
                                            </tr>
                                        </#list>
                                    </#list>
                                </#if>
                            </tbody>
                        </table>
                    </div>
                </#if>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>
seajs.config(config).use(['common/comp/Form', 'common/ui/Dialog', 'common/comp/Table'], function(Form, Dialog, Table) {
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

    new Table($('#tb_checkconfs'), {
        onCheck: function (allChecked, allUnchecked, container) {
            var $opt = $('#tbOpt_checkconfs');
            if (allUnchecked == true) {
                $opt.removeClass('checked');
            } else {
                $opt.addClass('checked');
            }
        }
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