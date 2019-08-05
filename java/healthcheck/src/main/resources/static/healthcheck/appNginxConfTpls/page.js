seajs.config(config).use(['common/comp/Form', 'common/ui/Dialog'], function(Form, Dialog) {
    var myForm = new Form($('form'), {
        avoidSend: function() {
            if (appNginxConfTplForm.content.value && appNginxConfTplForm.content.value.length > 0) {
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
            new Dialog().alert('<h6>修改成功！</h6>', { type: 'success', buttons: [{
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
});