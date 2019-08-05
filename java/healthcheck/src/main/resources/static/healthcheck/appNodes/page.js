if (appNodesForm.content.value && appNodesForm.content.value.length > 0) {
    var contentObj = JSON.parse(appNodesForm.content.value);
    var contentObjPrettyJson = JSON.stringify(contentObj, null, 4);
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
            new Dialog().alert('<h6>修改成功，需要前往“应用设置”刷新Robot！</h6>', { type: 'success', buttons: [{
                    events: function(event) {
                        event.data.dialog.remove();
                        location.reload();
                    }
                }]
            });
        }
    }, {
        label: true
    });

    // 点击switch开关
    $('input[type="checkbox"]').on('click', function() {
        var $this = $(this);
        var checked = $this.prop('checked');
        var ids = $this.attr('id').split(',');
        var action = ids[0];
        var serverName = ids[1];
        var groupName = ids[2];
        switch(action) {
        case 'ahc':
            var contentObj = JSON.parse(appNodesForm.content.value);
            for(var i = 0, len = contentObj.length; i < len; i++) {
                var serverNode = contentObj[i];
                if(serverName === serverNode.serverName) {
                    for(var k in serverNode.groups) {
                        if(groupName === k) {
                            var instanceNodeGroup = serverNode.groups[k];
                            instanceNodeGroup['autoHealthCheckMode'] = checked;
                        }
                    }
                }
            }
            appNodesForm.content.value = JSON.stringify(contentObj);
            myForm.submit();
            break;
        case 'actived':
        	var nodeip = ids[3];
        	var nodeport = ids[4];
            var contentObj = JSON.parse(appNodesForm.content.value);
            for(var i = 0, len = contentObj.length; i < len; i++) {
                var serverNode = contentObj[i];
                if(serverName === serverNode.serverName) {
                    for(var k in serverNode.groups) {
                        if(groupName === k) {
                            var instanceNodeGroup = serverNode.groups[k];
                            for(var j = 0, jlen = instanceNodeGroup.nodes.length; j < jlen; j++) {
                                var instanceNode = instanceNodeGroup.nodes[j];
                                if(nodeip === instanceNode.ip && nodeport == instanceNode.port) {
                                	instanceNode.actived = checked;
                                }
                            }
                        }
                    }
                }
            }
            appNodesForm.content.value = JSON.stringify(contentObj);
            myForm.submit();
            break;
        }
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