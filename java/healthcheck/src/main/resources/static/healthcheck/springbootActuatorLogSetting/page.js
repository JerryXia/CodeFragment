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