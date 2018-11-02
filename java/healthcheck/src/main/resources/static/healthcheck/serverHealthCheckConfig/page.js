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

    var $table = new Table($('#tb_checkconfs'), {
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
                        var $checkedItems = $table.el.container.find('td:first-child [type=checkbox]').filter(':checked');
                        var s = [];
                        $checkedItems.each(function(i, v) {
                            var currs = $(v).data('s');
                            if(s.indexOf(currs) === -1){
                                s.push(currs);
                            }
                        });
                        $.post('/healthcheck/refreshServerNodes', { serverNames: s.join(',') }, function(res) {
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

    $('#btnRefreshConfFromConfig').click(function() {
        var refreshConfirm = new Dialog().confirm('确定要刷新nginx配置吗？确保你修改的是的正确格式的配置文件！', {
            buttons: [{
                events: {
                    click: function() {
                        var $checkedItems = $table.el.container.find('td:first-child [type=checkbox]').filter(':checked');
                        var s = [];
                        $checkedItems.each(function(i, v) {
                            var currs = $(v).data('s');
                            if(s.indexOf(currs) === -1){
                                s.push(currs);
                            }
                        });
                        $.post('/healthcheck/refreshServerNodeNginxConf', { serverNames: s.join(',') }, function(res) {
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