const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        methods: ['GET', 'HEAD', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'TRACE'],
        corsRules: [],
        defaultRule: {
            urlPath: '/**',
            allowedOrigins: [],
            allowedOriginsText: '',
            allowedMethods: ['GET', 'HEAD', 'POST', 'PUT', 'DELETE'],
            allowedHeaders: [],
            allowedHeadersText: '',
            exposedHeaders: [],
            exposedHeadersText: '',
            allowCredentials: true,
            maxAge: 3600
        },
        selectedRule: {
            urlPath: '',
            allowedOrigins: [],
            allowedOriginsText: '',
            allowedMethods: [],
            allowedHeaders: [],
            allowedHeadersText: '',
            exposedHeaders: [],
            exposedHeadersText: '',
            allowCredentials: true,
            maxAge: 0
        }
    },
    computed: {
        triggerBtnEnabled: function() {

        }
    },
    created: function() {
        console.info('created');
        let that = this;
        that.loadCorsRules();

        Vue.nextTick(function() {
            console.log('init html5Validate');
            $("form").html5Validate(function() {
                that.submitData();
                return false;
            }, {
                validate: function() {
                    // $("#checkBox").testRemind("至少选择3项");
                    return true;
                }
            });
        });
    },
    updated: function() {
        console.info('updated');
    },
    methods: {
        loadCorsRules: function() {
            let that = this;
            $.get(dashboardPathPrefix + '/dashboard/corsconfig/rules', {}, function(res) {
                for (var k in res) {
                    var val = res[k];
                    val['urlPath'] = k;
                    that.corsRules.push(val);
                }
            });
        },
        createRule: function() {
            let that = this;
            that.selectedRule = JSON.parse(JSON.stringify(that.defaultRule));
            $('#ruleModal').modal('toggle', 'fit');
        },
        editRule: function(rule) {
            let that = this;
            that.selectedRule = rule;
            that.selectedRule.allowedOriginsText = rule.allowedOrigins.join('\r\n');
            that.selectedRule.allowedHeadersText = rule.allowedHeaders.join('\r\n');
            that.selectedRule.exposedHeadersText = rule.exposedHeaders.join('\r\n');
            $('#ruleModal').modal('toggle', 'fit');
        },
        submitData: function() {
            let that = this;
            that.selectedRule.allowedOrigins = that.parseLinesToArray(that.selectedRule.allowedOriginsText);
            that.selectedRule.allowedHeaders = that.parseLinesToArray(that.selectedRule.allowedHeadersText);
            that.selectedRule.exposedHeaders = that.parseLinesToArray(that.selectedRule.exposedHeadersText);
            utils.jsonPost(dashboardPathPrefix + '/dashboard/corsconfig/rule?urlPath=' + encodeURIComponent(that.selectedRule.urlPath), that.selectedRule, function(res) {
                $('#ruleModal').modal('hide', 'fit');
            }, function() {

            }, function(res) {

            });
            return false;
        },
        parseLinesToArray: function(lines) {
            var result = [];
            var arr = lines.split('\n');
            for (let i = 0, len = arr.length; i < len; i++) {
                var line = arr[i];
                if (line && line.trim().length > 0) {
                    result.push(line.trim());
                }
            }
            return result;
        }
    }
});