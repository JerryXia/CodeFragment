const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

Vue.filter('percentUsedCalc', function (left, right) {
    if (right && right > 0) {
        return Math.floor(100 * left / right);
    } else {
        return 0;
    }
});
Vue.filter('humanBytes', function (input, unit) {
    var units = {
        B: Math.pow(1024, 0),
        K: Math.pow(1024, 1),
        M: Math.pow(1024, 2),
        G: Math.pow(1024, 3),
        T: Math.pow(1024, 4),
        P: Math.pow(1024, 5)
    };
    input = input || 0;
    unit = unit || 'B';

    var bytes = input * (units[unit] || 1);

    var chosen = 'B';
    for (var u in units) {
        if (units[chosen] < units[u] && bytes >= units[u]) {
            chosen = u;
        }
    }
    return (bytes / units[chosen]).toFixed(1).replace(/\.0$/, '').replace(/,/g, '') + chosen;
});
function padZero(i, n) {
    var s = i + '';
    while (s.length < n) {
        s = '0' + s;
    }
    return s;
}
Vue.filter('timeInterval', function (input) {
    var s = input || 0;
    var d = padZero(Math.floor(s / 86400000), 2);
    var h = padZero(Math.floor(s % 86400000 / 3600000), 2);
    var m = padZero(Math.floor(s % 3600000 / 60000), 2);
    var sec = padZero(Math.floor(s % 60000 / 1000), 2);
    return d + ':' + h + ':' + m + ':' + sec;
});


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
            "mem": 1,
            "mem.free": 0,
            "heap.committed": 1,
            "heap.init": 0,
            "heap.used": 0,
            "heap": 1,
            "nonheap.committed": 1,
            "nonheap.init": 0,
            "nonheap.used": 0,
            "nonheap": 0,
            "uptime": 0,
            "processors": 1,
            "classes": 0,
            "classes.loaded": 0,
            "classes.unloaded": 0,
            "threads.peak": 0,
            "threads.daemon": 0,
            "threads.totalStarted": 0,
            "threads": 1,
            "gc.ps_marksweep.count": 0,
            "gc.ps_marksweep.time": 0,
            "gc.ps_scavenge.count": 0,
            "gc.ps_scavenge.time": 0,
            "httpsessions.max": -1,
            "httpsessions.active": 0
        },
        systemload: 0
    },
    computed: {
        diskSpaceUsed: function () {
            let that = this;
            let used = that.health.diskSpace.total - that.health.diskSpace.free;
            return used;
        },
        diskSpaceUsedPercent: function () {
            let that = this;
            let used = that.health.diskSpace.total - that.health.diskSpace.free;
            return Vue.filter('percentUsedCalc')(used, that.health.diskSpace.total);
        },
        memory: function () {
            let that = this;
            let used = that.metrics.mem - that.metrics['mem.free'];
            return {
                total: that.metrics.mem,
                used: used,
                percentUsed: Vue.filter('percentUsedCalc')(used, that.metrics.mem),
                unit: 'K'
            };
        },
        heap: function () {
            let that = this;
            return {
                total: that.metrics['heap.committed'],
                used: that.metrics['heap.used'],
                init: that.metrics['heap.init'],
                max: that.metrics['heap.max'] || that.metrics.heap,
                unit: that.metrics['heap.max'] ? 'B' : 'K',
                percentUsed: Vue.filter('percentUsedCalc')(that.metrics['heap.used'], that.metrics['heap.committed']),
            };
        },
        nonheap: function () {
            let that = this;
            return {
                total: that.metrics['nonheap.committed'],
                used: that.metrics['nonheap.used'],
                init: that.metrics['nonheap.init'],
                max: that.metrics['nonheap.max'] || that.metrics.heap,
                unit: that.metrics['nonheap.max'] ? 'B' : 'K',
                percentUsed: Vue.filter('percentUsedCalc')(that.metrics['nonheap.used'], that.metrics['nonheap.committed']),
            };
        },
        gcs: function () {
            let that = this;
            let gcArray = [];

            for (let key in that.metrics) {
                let value = that.metrics[key];

                var match = /gc\.(.+)\.time/.exec(key);
                if (match !== null) {
                    gcArray.push({
                        name: match[1],
                        rows: 2,
                        pName: 'time',
                        pValue: value + ' ms'
                    });
                    gcArray.push({
                        name: match[1],
                        rows: 0,
                        pName: 'count',
                        pValue: that.metrics['gc.' + match[1] + '.count']
                    });
                }
            }
            console.log(gcArray);
            return gcArray;
        },
        sessions: function () {
            let that = this;
            return {
                active: that.metrics['httpsessions.active'],
                max: that.metrics['httpsessions.max'] < 0 ? 'unbounded' : that.metrics['httpsessions.max']
            };
        }
    },
    created: function () {
        console.info('created');
        this.listHeathInfo();
        this.listMetrics();
    },
    updated: function () {
        console.info('updated');
    },
    watch: {
        'systemload.average': {
            handler: function (newVal, oldVal) {
                let that = this;
                that.systemload = Vue.filter('percentUsedCalc')(that.metrics['systemload.average'], 1) / 100;
            },
            deep: false
        }
    },
    methods: {
        listHeathInfo: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/health', {}, function (res) {
                that.health.status = res.status;
                that.health.diskSpace = res.diskSpace;
            });
        },
        isHealthDetail: function (t, n) {
            return "status" !== t && null !== n && (Array.isArray(n) || "object" != typeof n);
        },
        isChildHealth: function (t, n) {
            return null !== n && !Array.isArray(n) && "object" == typeof n;
        },
        listMetrics: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/metrics', {}, function (res) {
                that.metrics = res;
            });
        },
        getProgressBarClass: function (percentage) {
            if (percentage < 60) {
                return 'progress-bar-success';
            } else if (percentage >= 60 && percentage < 75) {
                return 'progress-bar-info';
            } else if (percentage >= 75 && percentage <= 95) {
                return 'progress-bar-warning';
            } else {
                return 'progress-bar-danger';
            }
        }
    }
});

