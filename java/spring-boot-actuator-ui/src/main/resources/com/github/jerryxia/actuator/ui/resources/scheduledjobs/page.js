const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        cronTasksCheckedList: [],
        cronTasks: [],
        fixedDelayTasksCheckedList: [],
        fixedDelayTasks: [],
        fixedRateTasksCheckedList: [],
        fixedRateTasks: []
    },
    computed: {
        triggerBtnEnabled: function () {
            let that = this;
            for (var k in that.cronTasksCheckedList) {
                if (that.cronTasksCheckedList[k]) {
                    return true;
                }
            }
            for (var k in that.fixedDelayTasksCheckedList) {
                if (that.fixedDelayTasksCheckedList[k]) {
                    return true;
                }
            }
            for (var k in that.fixedRateTasksCheckedList) {
                if (that.fixedRateTasksCheckedList[k]) {
                    return true;
                }
            }
            return false;
        }
    },
    created: function () {
        console.info('created');
        this.listRegisteredTasks();
    },
    updated: function () {
        console.info('view updated');
    },
    methods: {
        listRegisteredTasks: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/dashboard/scheduledjobs/registeredTasks', {}, function (res) {
                that.cronTasks = res.cronTasks;
                that.fixedDelayTasks = res.fixedDelayTasks;
                that.fixedRateTasks = res.fixedRateTasks;
            });
        },
        triggerTasks: function () {
            let that = this;
            let postData = {
                cronTasks: that.cronTasksCheckedList.join(','),
                fixedDelayTasks: that.fixedDelayTasksCheckedList.join(','),
                fixedRateTasks: that.fixedRateTasksCheckedList.join(',')
            };
            utils.formPost(dashboardPathPrefix + '/dashboard/scheduledjobs/triggerRegisteredTasks', postData, function () {

            }, function () {

            }, function () {

            });
        }
    }
});
