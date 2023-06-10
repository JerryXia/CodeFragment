const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        appScheduledTasks: []
    },
    computed: {
        triggerBtnEnabled: function() {
            let that = this;

            return false;
        }
    },
    created: function() {
        console.info('created');
        this.listScheduledTasks();
    },
    updated: function() {
        console.info('view updated');
    },
    watch: {
        'appScheduledTasks': {
            handler: function(newVal, oldVal) {
                let that = this;
                if (oldVal.length == 0) {
                    return false;
                }
                let postData = {};
                for (let i = 0, len = newVal.length; i < len; i++) {
                    let voItem = newVal[i];
                    postData[voItem.applicationName] = {
                        nodes: {},
                        tasks: {}
                    };
                    for (let j = 0, jlen = voItem.nodeNames.length; j < jlen; j++) {
                        postData[voItem.applicationName]['nodes'][voItem.nodeNames[j]] = voItem.nodeActive[voItem.nodeNames[j]];
                        postData[voItem.applicationName]['tasks'] = voItem.tasks;
                    }
                }
                console.log('postData', postData);
                utils.jsonPost(dashboardPathPrefix + '/dashboard/scheduledtaskconfig/modifyTaskNodes', postData, function() {

                }, function() {

                }, function() {

                });
            },
            deep: true
        }
    },
    methods: {
        listScheduledTasks: function() {
            let that = this;
            $.get(dashboardPathPrefix + '/dashboard/scheduledtaskconfig/scheduledtasks', {}, function(res) {
                let mAppScheduledTasks = [];
                for (let k in res) {
                    let taskNames = [];
                    let tasks = res[k].tasks;
                    let nodes = res[k].nodes;
                    let nodeNames = [];
                    for (var taskName in tasks) {
                        taskNames.push(taskName);
                    }
                    for (var nodeName in nodes) {
                        nodeNames.push(nodeName);
                    }
                    let item = {
                        applicationName: k,
                        taskNames: taskNames,
                        nodeNames: nodeNames,
                        nodeActive: res[k].nodes,
                        tasks: res[k].tasks
                    };
                    mAppScheduledTasks.push(item);
                }
                console.log(JSON.stringify(mAppScheduledTasks));
                that.appScheduledTasks = mAppScheduledTasks;
            });
        }
    }
});
