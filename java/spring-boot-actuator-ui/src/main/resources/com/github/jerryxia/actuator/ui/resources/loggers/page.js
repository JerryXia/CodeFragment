const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        levels: [],
        loggers: {}
    },
    computed: {
        logCount: function () {
            let that = this;
            return 0;
        }
    },
    created: function () {
        console.info('created');
        this.listLoggers();
    },
    updated: function () {
        console.info('updated');
    },
    watch: {
        'loggers': {
            handler: function (newVal, oldVal) {

            },
            deep: true
        }
    },
    methods: {
        listLoggers: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/loggers', {}, function (res) {
                that.levels = res.levels;
                that.loggers = res.loggers;
            });
        },
        selectLevel: function (loggerName, logger, level) {
            let that = this;
            logger.configuredLevel = level;
            logger.effectiveLevel = level;
            // modify
            let postData = {
                configuredLevel: level
            };
            utils.jsonPost(dashboardPathPrefix + '/loggers/' + loggerName, postData, function () {
                    new $.zui.Messager('Modify logger\'s level success', {
                        type: 'success',
                        placement: 'top-right'
                    }).show();
                    that.listLoggers();
                }, function () {
                    new $.zui.Messager('Modify logger\'s level fail', {
                        type: 'danger',
                        placement: 'top-right'
                    }).show();
                }, function () {

                });
        }
    }
});
