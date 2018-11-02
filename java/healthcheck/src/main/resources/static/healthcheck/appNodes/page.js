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