const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

$('body').on('click', 'a[action-jump="action-jump"]', function () {
    var $this = $(this);
    $beansTree.tree('expand', $(document.getElementById($this.attr('href').substring(1))).parent());
});

let app = new Vue({
    el: '#app',
    data: {
        initHash: ''
    },
    computed: {
        logCount: function () {
            let that = this;
            return 0;
        }
    },
    created: function () {
        console.info('created');
        let that = this;
        that.initHash = location.hash;
        that.listBeans();
    },
    updated: function () {
        console.info('updated');
        this.$nextTick(function () {

        });
    },
    watch: {

    },
    methods: {
        listBeans: function () {
            let that = this;
            $.get(dashboardPathPrefix + '/beans', {}, function (res) {
                var treeData = that.generateTreeData(res);
                $beansTree = $('#beansTree');
                $beansTree.tree({ data: treeData });
                setTimeout(function () {
                    location.hash = '';
                    location.hash = that.initHash;
                    if (that.initHash && that.initHash.length > 0) {
                        $beansTree.tree('expand', $(document.getElementById(that.initHash.substring(1))).parent());
                    }
                }, 50);
            });
        },
        generateTreeData: function (res) {
            var treeData = [];
            for (var i = 0, len = res.length; i < len; i++) {
                var item = res[i];
                var contextRoot = {
                    title: item.context,
                    open: true,
                    children: []
                };
                for (var j = 0, jlen = item.beans.length; j < jlen; j++) {
                    var beanItem = item.beans[j];

                    var beanTreeItem = {
                        html: '<a href="#" id="' + beanItem.bean + '">' + beanItem.bean + '</a>',
                        // url: '#',
                        children: []
                    };
                    var type = { title: beanItem.type };
                    var resource = { title: beanItem.resource == 'null' ? 'Container Generated' : beanItem.resource };
                    var dependencies = { title: 'Dependencies' };

                    beanTreeItem.children.push(type);
                    beanTreeItem.children.push(resource);
                    if (beanItem.dependencies.length > 0) {
                        dependencies['children'] = [];
                        dependencies['open'] = true;
                        for (var k = 0, klen = beanItem.dependencies.length; k < klen; k++) {
                            dependencies.children.push({ html: '<a href="#' + beanItem.dependencies[k] + '" action-jump="action-jump">' + beanItem.dependencies[k] + "</a>" });
                        }
                    }
                    beanTreeItem.children.push(dependencies);

                    contextRoot.children.push(beanTreeItem);
                }
                treeData.push(contextRoot);
            }
            return treeData;
        }
    }
});
