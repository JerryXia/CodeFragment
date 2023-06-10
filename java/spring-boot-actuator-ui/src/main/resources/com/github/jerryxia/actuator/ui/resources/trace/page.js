const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        records: [],
        showHeaders: {

        },
        viewTriggerEvent: 'click'
    },
    computed: {
        logCount: function () {
            let that = this;
            return 0;
        }
    },
    created: function () {
        console.info('created');
        this.init();
    },
    updated: function () {
        console.info('updated');
    },
    watch: {
        'viewTriggerEvent': {
            handler: function (newVal, oldVal) {
                localStorage.setItem('dashboard_trace_viewTriggerEvent', newVal);
            },
            deep: false
        }
    },
    methods: {
        init: function () {
            this.loadConfig();
            this.listTrace();
        },
        loadConfig: function () {
            let that = this;
            let value = localStorage.getItem('dashboard_trace_viewTriggerEvent');
            if (value && value.length > 0) {
                switch (value) {
                    case 'click':
                    case 'hover':
                        that.viewTriggerEvent = value;
                        break;
                    default:
                        console.warn('find unknown viewTriggerEvent:' + value);
                        break;
                }
            }
        },
        listTrace: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/trace', {}, function (res) {
                that.records = res;
            });
        },
        toggleRequestHeaders: function (record, event) {
            let that = this;
            switch (that.viewTriggerEvent) {
                case 'hover':
                    if (event.type === 'mouseover') {
                        this.showHeaders = record.info.headers.request;
                        $('#headersModal').modal('toggle', 'fit');
                    }
                    break;
                case 'click':
                    if (event.type === 'click') {
                        this.showHeaders = record.info.headers.request;
                        $('#headersModal').modal('toggle', 'fit');
                    }
                    break;
            }

        },
        toggleResponseHeaders: function (record) {
            let that = this;
            switch (that.viewTriggerEvent) {
                case 'hover':
                    if (event.type === 'mouseover') {
                        this.showHeaders = record.info.headers.response;
                        $('#headersModal').modal('toggle', 'fit');
                    }
                    break;
                case 'click':
                    if (event.type === 'click') {
                        this.showHeaders = record.info.headers.response;
                        $('#headersModal').modal('toggle', 'fit');
                    }
                    break;
            }
        }
    }
});
