const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
    el: '#app',
    data: {
        dashboardPathPrefix: dashboardPathPrefix,
        threads: [],
        collapseToggleStatus: 'hide'
    },
    computed: {
        allCollapseSelectors: function(){
            let that = this;
            let selectors = _.map(that.threads, function(thread){ return '#thread_' + thread.threadId  });
            return selectors.join(',');
        }
    },
    created: function () {
        console.info('created');
        this.listThreads();
    },
    updated: function () {
        console.info('updated');
        let that = this;
        Vue.nextTick(function () {
            window.prettyPrint();
            $(that.allCollapseSelectors).collapse({
                parent: '#threadPanels',
                toggle: false
            });
        });
    },
    watch: {
        'collapseToggleStatus': {
            handler: function (newVal, oldVal) {
                let that = this;
                $(that.allCollapseSelectors).collapse(newVal);
            },
            deep: false
        }
    },
    methods: {
        listThreads: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/dump', {}, function (res) {
                that.threads = res;
            });
        },
        getStateClass: function (thread) {
            switch (thread.threadState) {
                case 'NEW':
                case 'TERMINATED':
                    return 'label-info';
                case 'RUNNABLE':
                    return 'label-success';
                case 'BLOCKED':
                    return 'label-important';
                case 'TIMED_WAITING':
                case 'WAITING':
                    return 'label-warning';
                default:
                    return 'label-info';
            }
        }
    }
});
