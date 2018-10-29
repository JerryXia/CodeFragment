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

<script>
let currSelectedLoggers = ${loggersJsonContent!'{}'};
</script>
<#include "../commonScripts.ftl">
<script src="https://cdn.staticfile.org/blueimp-md5/2.10.0/js/md5.min.js"></script>
<script>
seajs.config(config).use(['common/ui/Select', 'common/ui/Tips', 'common/ui/Drop'], function(Select, Tips, Drop) {
    $('form select').each(function () {
        new Select($(this));
    });
    new Tips().init();

    $('form select').change(function() {
        searchLoggersForm.submit();
    });

    let generateLoggerNameRowHtml = function(loggerName, level) {
        let css = '';
        switch(level) {
            case 'TRACE':
                css = 'gray';
                break;
            case 'DEBUG':
                css = 'green';
                break;
            case 'INFO':
                css = 'blue';
                break;
            case 'WARN':
                css = 'orange';
                break;
            case 'ERROR':
                css = 'red';
                break;
            case 'OFF':
                css = 'dark';
                break;
            default:
                break;
        }
        let html = '<span class="logger '+ css +' ui-tips" id="l'+md5(loggerName)+'" title="'+ level+'">'+ loggerName +'</span>';
        return html;
    }

    if(currSelectedLoggers.loggers['ROOT']) {
        $('#ROOT').html(generateLoggerNameRowHtml('ROOT', currSelectedLoggers.loggers['ROOT'].effectiveLevel));
    }

    // child nodes
    let loggerNamesTree = {};
    for(let loggerName in currSelectedLoggers.loggers) {
        if(loggerName && loggerName !== 'ROOT') {
            let loggerNamePaths = loggerName.split('.');
            let lastTreeNode = null;
            for(let i = 0, len = loggerNamePaths.length; i < len; i++) {
                let loggerNamePrefix = loggerNamePaths.slice(0, i + 1).join('.');
                if(i === 0) {
                    if(typeof loggerNamesTree[loggerNamePrefix] === 'undefined') {
                        loggerNamesTree[loggerNamePrefix] = currSelectedLoggers.loggers[loggerNamePrefix];
                    }
                    if(typeof loggerNamesTree[loggerNamePrefix] !== 'undefined') {
                        lastTreeNode = loggerNamesTree[loggerNamePrefix];
                    }
                }
                if(i > 0 && lastTreeNode) {
                    if(typeof currSelectedLoggers.loggers[loggerNamePrefix] !== 'undefined') {
                        if(typeof lastTreeNode['children'] === 'undefined') {
                            lastTreeNode['children'] = {};
                        }
                        lastTreeNode['children'][loggerNamePrefix] = currSelectedLoggers.loggers[loggerNamePrefix];
                        lastTreeNode = lastTreeNode['children'][loggerNamePrefix];
                    }
                }
            }
        }
    }
    console.log(loggerNamesTree);
    let loggersHtml = '';
    let generateTree = function(node) {
        loggersHtml += '<ul>';
        for(let loggerName in node) {
            loggersHtml += '<li>';
            loggersHtml += generateLoggerNameRowHtml(loggerName, currSelectedLoggers.loggers[loggerName].effectiveLevel);
            if(node[loggerName].children && JSON.stringify(node[loggerName].children) !== '{}') {
                generateTree(node[loggerName].children);
            }
            loggersHtml += '</li>';
        }
        loggersHtml += '</ul>';
    }
    generateTree(loggerNamesTree);
    $('#ROOT').after(loggersHtml);

    let currLoggerName = null;
    $('span.logger').each(function(i, v) {
        let objId = v.id;
        new Drop($('#' + objId), $('#loggerLevelSetting'), {
            eventType: 'click',
            onShow: function (trigger, target) {
                currLoggerName = trigger.text();
                let configuredLevel = currSelectedLoggers.loggers[currLoggerName].configuredLevel;
                if(configuredLevel && configuredLevel.length > 0){
                    $('#loggerLevels').val(configuredLevel);
                } else {
                    $('#loggerLevels').val('');
                }
                target.css('border', '1px solid #999999');
            }
        });
    });
    $('#loggerLevels').change(function() {
        let currLevel = $(this).val();
        if(currLevel && currLevel.length > 0) {
            $.post('/healthcheck/modifyLoggerLevel', { 
                serverName: $('#s').val(),
                groupName: $('#g').val(),
                ip: $('#n').val().split(':')[0],
                port: $('#n').val().split(':')[1],
                loggerName: currLoggerName,
                configuredLevel: currLevel
            }, function(res) {
                if(res.code === 0) {
                    location.reload();y
                } else {
                    new Dialog().alert('<h6>'+res.msg+'</h6>', { type: 'warning' });
                }
            });
        }
    });

});
</script>
<#include "../footer.ftl">