const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        customMappings: [],
        defaultMappings: []
    },
    computed: {
        logCount: function () {
            let that = this;
            return 0;
        }
    },
    created: function () {
        console.info('created');
        this.listMappings();
    },
    updated: function () {
        console.info('updated');
    },
    methods: {
        listMappings: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/mappings', {}, function (res) {
                var sortedMappings = that.sortMappings(res);
                that.customMappings = sortedMappings.customMappings;
                that.defaultMappings = sortedMappings.defaultMappings;
            });
        },
        sortMappings: function (mappings) {
            var customMappings = [];
            var defaultMappings = [];
            for (var k in mappings) {
                var item = mappings[k];
                item['path'] = k;
                switch (item.bean) {
                    case 'requestMappingHandlerMapping':
                        customMappings.push(item);
                        break;
                    case 'resourceHandlerMapping':
                    case 'faviconHandlerMapping':
                    case 'endpointHandlerMapping':
                    default:
                        defaultMappings.push(item);
                        break;
                }
            }
            return {
                customMappings: customMappings,
                defaultMappings: defaultMappings
            };
        }
    }
});
