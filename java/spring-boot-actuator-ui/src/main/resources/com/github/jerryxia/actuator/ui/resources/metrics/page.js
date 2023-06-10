const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        health: {
            status: "OFF",
            diskSpace: {
                status: "OFF",
                total: 0,
                free: 0,
                threshold: 0
            }
        },
        metrics: {

        }
    },
    computed: {

    },
    created: function () {
        console.info('created');
        this.listMetrics();
    },
    updated: function () {
        console.info('updated');
    },
    methods: {
        listMetrics: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/metrics', {}, function (metrics) {

                var metricData = {};

                var merge = function (array, obj) {
                    for (var i = 0; i < array.length; i++) {
                        if (array[i].name === obj.name) {
                            for (var a in obj) {
                                if (obj.hasOwnProperty(a)) {
                                    array[i][a] = obj[a];
                                }
                            }
                            return;
                        }
                    }
                    array.push(obj);
                }

                var addData = function (groupname, valueObj, max) {
                    var group = metricData[groupname] || { max: 0, values: [] };
                    merge(group.values, valueObj);
                    if (typeof max !== 'undefined' && max > group.max) {
                        group.max = max;
                    }
                    metricData[groupname] = group;
                }

                var matchers = [{
                    regex: /(gauge\..+)\.val/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            value: value
                        }, value);
                        $scope.showRichGauges = true;
                    }
                }, {
                    regex: /(gauge\..+)\.avg/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            avg: value.toFixed(2)
                        });
                    }
                }, {
                    regex: /(gauge\..+)\.min/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            min: value
                        });
                    }
                }, {
                    regex: /(gauge\..+)\.max/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            max: value
                        }, value);
                    }
                }, {
                    regex: /(gauge\..+)\.count/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            count: value
                        });
                    }
                }, {
                    regex: /(gauge\..+)\.alpha/,
                    callback: function () { /* NOP */
                    }
                }, {
                    regex: /(gauge\..+)/,
                    callback: function (metric, match, value) {
                        addData('gauge', {
                            name: match[1],
                            value: value
                        }, value);
                    }
                }, {
                    regex: /^([^.]+).*/,
                    callback: function (metric, match, value) {
                        addData(match[1], {
                            name: metric,
                            value: value
                        }, value);
                    }
                }];


                for (var metric in metrics) {
                    if (metrics.hasOwnProperty(metric)) {
                        for (var i = 0; i < matchers.length; i++) {
                            var match = matchers[i].regex.exec(metric);
                            if (match !== null) {
                                matchers[i].callback(metric, match, metrics[metric]);
                                break;
                            }
                        }
                    }
                }
                for (var groupname in metricData) {
                    if (metricData.hasOwnProperty(groupname)) {
                        that.metrics.push({
                            name: groupname,
                            values: metricData[groupname].values,
                            max: metricData[groupname].max || 0
                        });
                    }
                }
            });
        }
    }
});
